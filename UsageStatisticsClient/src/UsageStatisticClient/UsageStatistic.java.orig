package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import java.util.StringTokenizer;

import javax.crypto.NoSuchPaddingException;

import org.springframework.web.client.RestTemplate;

final public class UsageStatistic implements UsageLogger{
	private URI serverURL;
	private String user;
	private String password;
	private String tool;
	private static UsageLogger instance;
	private RestTemplate restTemplate;
	private DaoTemporaryDatabaseInterface dao;
	private CommitListener committingDetails;
	private CommitThread commitThread;
	private static boolean debuglog;

	private void init() throws UsageStatisticException {
		user=null;
		password=null;
		serverURL=null;
		tool=null;
		debuglog=true;
		dao = new DaoTemporaryDatabaseH2(); 													// throwsa
		committingDetails = new CommitingDetailsEmpty();
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
		File file = new File("client-config.cfg");
		CipherAES cipher = new CipherAES();
		String config=cipher.readCiphered(file); 
        validateAndSetConfig(config);
	}
	
	private void validateAndSetConfig(String config) throws URISyntaxException, UsageStatisticException{
        StringTokenizer st = new StringTokenizer(config);
        String url=null, us=null, pass=null, too=null, deb=null;
        if(st.hasMoreTokens() && st.nextToken().equals("serverURL=") && st.hasMoreTokens()){
            url=st.nextToken();
            
        
            if(st.hasMoreTokens() && st.nextToken().equals("user=") && st.hasMoreTokens()){
                us=st.nextToken();
                
            
                if(st.hasMoreTokens() && st.nextToken().equals("password=") && st.hasMoreTokens()){
                    pass=st.nextToken();
                    
                    
                    if(st.hasMoreTokens() && st.nextToken().equals("tool=") && st.hasMoreTokens()){
                        too=st.nextToken();
                       
                        
                        if(st.hasMoreTokens() && st.nextToken().equals("debug=") && st.hasMoreTokens()){
                            deb=st.nextToken();
                            
                        }
                    }
                }
            }
        }
        if(url!=null && us!=null && pass!=null && too!=null && deb!=null){
        	serverURL=new URI(url+"/post");
            this.user = us;
            this.password = pass;
            this.tool = too;
            this.debuglog = deb.equals("on");
        }
        else{
        	throw new UsageStatisticException(UsageStatisticException.CONFIG_ERROR);
        }

        
    }


	private UsageStatistic() throws UsageStatisticException {
		init();
	}

	public void log(String functionality, String parameters) 
	{ 
		LogInformation log = new LogInformation(Calendar.getInstance().getTime(), functionality, user, tool, parameters);
		dao.saveLog(log);
		//dao.closeDatabase();
	}
	
	
	@Override	
	public synchronized void commit()
	{
		if(commitThread==null || !commitThread.isAlive()){
			commitThread = null;
			commitThread = new CommitThread();
			commitThread.setDaemon(true);
			commitThread.start();
		}
		
	}
	

	private synchronized void commitInCommit()
	{
		try
		{
			int logsAmount = dao.getLogsAmount();
			committingDetails.commitingStart();
			committingDetails.setLogsAmount(logsAmount);
			committingDetails.setInfo("Begin commiting");
			int i = 0;
			
			while (!dao.isEmpty() &&  i < logsAmount)
			{
				LogInformation log = dao.getFirstLog();

				if (log!=null)
				{
					String postForObject = restTemplate.postForObject(serverURL, log, String.class);
					if ("OK".equals(postForObject)) 
					{
						dao.clearFirstLog();
						committingDetails.step();
						
					}
					else if ("ERROR".equals(postForObject))
					{
						committingDetails.stepInvalid(Errors.CANNOT_SAVE_LOG);
					} 
					else
					{
						committingDetails
						.commitingFailureWithError(Errors.SERVER_DOESNT_RECEIVE_DATA);
					}
					
				} else
				{
					committingDetails.stepInvalid(Errors.LOG_WAS_NULL);
				}
				
				i++;
				
			}
				committingDetails.setInfo("Commiting finised succesful");
				committingDetails.commitingFinishedSuccesful();
		} 
		
		
		catch (org.springframework.web.client.HttpClientErrorException e)
		{
			committingDetails
			.commitingFailureWithError(Errors.SERVER_TURNED_OFF);
		} 
		
		
		catch (org.springframework.web.client.ResourceAccessException e)
		{
			committingDetails
			.commitingFailureWithError(Errors.ERROR_WITH_CONNECTION_TO_SERVER);
				
		} 
		catch (SQLException e)
		{	
			dao.resetDatabase();
			committingDetails
			.commitingFailureWithError(Errors.ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE);
		} 
		
		catch (org.springframework.web.client.RestClientException e)
		{
			committingDetails
			.commitingFailureWithError(Errors.CANNOT_EXTRACT_RESPONSE);
		}
	}

	

	public static UsageLogger getInstance()
	{
		try 
		{
			if (instance == null||instance instanceof UsageLoggerEmpty)
				instance=new UsageStatistic();
			else
				((UsageStatistic)instance).init();
		} 
		catch (UsageStatisticException e) 
		{
			instance=new UsageLoggerEmpty();
			errorlog(e);
		}
		return instance;
	}
	
private static void errorlog(UsageStatisticException e) 
{
		if (debuglog)
		{
			BufferedWriter out;
			try {
				out = new BufferedWriter(new FileWriter("debuglog.txt",true));
				out.write(Calendar.getInstance().getTime()+": "+e.getMessage()+"\n");
				out.close();
			} catch (IOException e1) 
			{}
		}
		
	}

	private void commitWait(){
		try {
			commitThread.join();
		} catch (InterruptedException e) {
			
		}
	}
	
	
	private class CommitThread extends Thread{
		
		@Override
		public void run(){
			commitInCommit();
		}
	}
	
	@Override
	public void setCommitListener(CommitListener cl) 
	{
		if (committingDetails==null)
		{
		committingDetails = new CommitingDetailsEmpty();
		}
		else
		{
		committingDetails=cl;
		}
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
	
}
