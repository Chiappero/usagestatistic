package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		debuglog=false;
		dao = new DaoTemporaryDatabaseH2(); 													// throwsa
		committingDetails = new CommitingDetailsEmpty();
		restTemplate = new RestTemplate();
		File file = new File("client-config.cfg"); // TODO2 - zakoduj to
		BufferedReader bufferedReader;
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			
			String line;
			while ((line=bufferedReader.readLine())!=null)
			{
				String[] field=line.split("=");
				if (field.length==2)
				{
					field[0]=field[0].trim();
					field[1]=field[1].trim();
					setField(field);
				}
			}
			bufferedReader.close();
			areFieldsSetCorrectly();
		}
			catch (URISyntaxException e)
			{
				throw new UsageStatisticException(UsageStatisticException.INVALID_SERVER_URL);
			}
		 catch (IOException e)
		 	{
			 throw new UsageStatisticException(UsageStatisticException.CANNOT_READ_CONFIGURATION_FILE);
		 	}

		

	}

	private void areFieldsSetCorrectly() throws UsageStatisticException 
	{
		if (user==null||user.equals(""))
			throw new UsageStatisticException(UsageStatisticException.INVALID_CONFIGURATION_USERNAME);
		if (password==null||password.equals(""))
			throw new UsageStatisticException(UsageStatisticException.INVALID_CONFIGURATION_PASSWORD);
		if (tool==null||tool.equals(""))
			throw new UsageStatisticException(UsageStatisticException.INVALID_CONFIGURATION_TOOL);
		if (serverURL==null||serverURL.toString().equals("/post"))
			throw new UsageStatisticException(UsageStatisticException.INVALID_SERVER_URL);


	}

	private void setField(String[] field) throws URISyntaxException 
	{
		if (field[0].equals("serverURL"))
			serverURL=new URI(field[1]+"/post");
		if (field[0].equals("user"))
			user=field[1];
		if (field[0].equals("password"))
			password=field[1];
		if (field[0].equals("tool"))
			tool=field[1];
		if (field[0].equals("debug"))
			{

			if (field[1].equals("on"))
				try
				{
					BufferedWriter out=new BufferedWriter(new FileWriter("debuglog.txt",true));
					out.close();
				debuglog=true;
				}
				catch (IOException e)
				{
					debuglog=false;
				}
			else debuglog=false;
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
