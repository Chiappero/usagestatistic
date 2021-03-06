package usagestatisticsclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.crypto.NoSuchPaddingException;

import org.springframework.web.client.RestTemplate;
/**
 * 
 * @class Contains the implementation of methods to collect and send logs
 *
 */
public final class UsageStatistic implements UsageLogger{
	private URI serverURL;
	private String user;
	private String password; 
	private String tool;
	private static UsageLogger instance;
	private RestTemplate restTemplate;
	private DaoTemporaryDatabaseInterface dao;
	private static boolean debuglog;
	
	
	private static final String DEBUGLOG_FILE = "debuglog.txt";
	private static final String CLIENT_CONFIG_FILE = "client-config.cfg";
	private static final String POST_PATH = "/post";
	private static final String ON = "on";
	
	@Override
	public void log(final String functionality, final String parameters) 
	{ 
		LogInformation log = new LogInformation(Calendar.getInstance().getTime(), functionality, user, tool, parameters);
		dao.saveLog(log);
	}
	
	@Override
	public int getLogsCount() 
	{
		try
		{
			return dao.getLogsAmount();
		} catch (SQLException e)
		{ 
			return 0;
		}
	}

	@Override
	public Date getOldestLogDate() {
		return dao.getOldestLogDate();
	}

	@Override
	public List<LogInformation> getAllLogs()
	{
		return dao.getAllLogs();
	}

	@Override
	public Runnable createCommitRunnable(final CommitListener cl)
	{
		return new CommitRunnable(cl);
	}
	/**
	 * Returns singleton of UsageLogger
	 * @return instance of UsageLogger
	 */
	public static UsageLogger getInstance()
	{
		try 
		{
			if (instance == null||instance instanceof UsageLoggerEmpty)
			{
			instance=new UsageStatistic();
			((UsageStatistic) instance).init();
			}
			
			
		} 
		catch (UsageStatisticException e) 
		{
			instance=new UsageLoggerEmpty();
			errorlog(e);
		}
		return instance;
	}

	private void init() throws UsageStatisticException {
		user=null;
		password=null;
		serverURL=null;
		tool=null;
		debuglog=true;
		dao = new DaoTemporaryDatabaseH2(); 													
		restTemplate = new RestTemplate();

			try {
				readFromCipheredFile();
			} catch (InvalidKeyException e) {
				throw new UsageStatisticException(UsageStatisticException.CIPHER_ERROR);
			} catch (NoSuchAlgorithmException e) {
				throw new UsageStatisticException(UsageStatisticException.CIPHER_ERROR);
			} catch (NoSuchPaddingException e) {
				throw new UsageStatisticException(UsageStatisticException.CIPHER_ERROR);
			} catch (InvalidAlgorithmParameterException e) {
				throw new UsageStatisticException(UsageStatisticException.CIPHER_ERROR);
			} catch (IOException e) {
				throw new UsageStatisticException(UsageStatisticException.CANNOT_READ_CONFIGURATION_FILE);
			} catch (URISyntaxException e) {
				throw new UsageStatisticException(UsageStatisticException.INVALID_SERVER_URL);
			}
		

	}
	
	private void readFromCipheredFile() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, URISyntaxException, UsageStatisticException{
		File file = new File(CLIENT_CONFIG_FILE);
		Ciphers cipher = new Ciphers();
		String config=cipher.readCiphered(file); 
		StringTokenizer st = new StringTokenizer(config);

        try{
        	st.nextToken();
        	serverURL=new URI(st.nextToken()+POST_PATH);//url=st.nextToken();
        	st.nextToken();
        	user=st.nextToken();
        	st.nextToken();
        	password=st.nextToken();
        	st.nextToken();
        	tool=st.nextToken();
        	st.nextToken();
        	debuglog=st.nextToken().equals(ON);
        }
        catch(NoSuchElementException e){
        	throw new UsageStatisticException(UsageStatisticException.CONFIG_ERROR);
        }
	}
	
	private UsageStatistic() throws UsageStatisticException {
		init();
	}

	private synchronized void commitInCommit(final CommitListener committingDetails)
	{
		try
		{
			int logsAmount = dao.getLogsAmount();
			committingDetails.commitingStart();
			committingDetails.setLogsAmount(logsAmount);
			int i = 0;
			
			while (!dao.isEmpty() &&  i < logsAmount)
			{
				LogInformation log = dao.getFirstLog();

				sendOneLog(committingDetails, log);
				i++;
				
			}	
				committingDetails.commitingFinishedSuccesful();
		} 
		
		
		catch (org.springframework.web.client.HttpClientErrorException e)
		{
			committingDetails
			.commitingFailureWithError(UsageStatisticException.SERVER_TURNED_OFF);
		} 
		
		
		catch (org.springframework.web.client.ResourceAccessException e)
		{
			committingDetails
			.commitingFailureWithError(UsageStatisticException.ERROR_WITH_CONNECTION_TO_SERVER);
				
		} 
		catch (SQLException e)
		{	
			dao.resetDatabase();
			committingDetails
			.commitingFailureWithError(UsageStatisticException.ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE);
		} 
		
		catch (org.springframework.web.client.RestClientException e)
		{
			committingDetails
			.commitingFailureWithError(UsageStatisticException.CANNOT_EXTRACT_RESPONSE);
		}
	}
	
	private void sendOneLog(final CommitListener committingDetails, LogInformation log) throws SQLException{
		if (log!=null)
		{
			PairLogInformationAndPassword pair = new PairLogInformationAndPassword(log,password);
			String postForObject = restTemplate.postForObject(serverURL, pair, String.class);
			if ("OK".equals(postForObject)) 
			{
				dao.clearFirstLog();
				committingDetails.step();
				
			}
			else if ("ERROR".equals(postForObject))
			{
				committingDetails.stepInvalid(UsageStatisticException.CANNOT_SAVE_LOG);
			} 
			else if ("CANNOT_AUTHENTICATE".equals(postForObject))
			{
				committingDetails
				.commitingFailureWithError(UsageStatisticException.CANNOT_AUTHENTICATE);
				return;
			}
			else
			{
				committingDetails
				.commitingFailureWithError(UsageStatisticException.SERVER_DOESNT_RECEIVE_DATA);
				return;
			}
			
		}
		else
		{
			committingDetails.stepInvalid(UsageStatisticException.LOG_WAS_NULL);
		}
		
	}

	private static void errorlog(final UsageStatisticException e) 
{
		if (debuglog)
		{
			BufferedWriter out;
			try {
				out = new BufferedWriter(new FileWriter(DEBUGLOG_FILE,true));
				out.write(Calendar.getInstance().getTime()+": "+e.getMessage()+"\n");
				out.close();
			} catch (IOException e1) 
			{}
		}
		
	}

	private class CommitRunnable implements Runnable{
		
		private CommitListener listener;
		

		public CommitRunnable(CommitListener listener)
		{
			if (listener == null)
			{
				this.listener = new CommitingDetailsEmpty();
			}
			else
			{
				this.listener = listener;
			}
		}


		@Override
		public void run(){
			commitInCommit(listener);
		}
	}
}
