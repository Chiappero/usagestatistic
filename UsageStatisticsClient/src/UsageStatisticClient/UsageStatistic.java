package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

final public class UsageStatistic {
	private URI serverURL;
	private String user;
	private String password;
	private String tool;
	private static UsageStatistic instance;
	private RestTemplate restTemplate;
	private DaoTemporaryDatabaseInterface dao;
	private CommitingDetailsInterface committingDetails;
	private CommitThread commitThread;

	private void init(String toolInstance) throws UsageStatisticException {
		user=null;
		password=null;
		serverURL=null;
		tool=null;
		dao = new DaoTemporaryDatabaseH2(); 													// throwsa
		restTemplate = new RestTemplate();
		File file = new File("client-config.cfg"); // TODO2 - zakoduj to i dodaj
													// obsluge wyjatkow
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
			
			if (toolInstance!=null&&!toolInstance.isEmpty())
			{
				tool = toolInstance;
			} 
			else if (tool==null||tool.isEmpty())
				{
				
				tool = "Default Application";
				} //else zostaje domyslny tool wczytany przez loadera
			
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
		if (user==null||user.equals("")||password==null||password.equals(""))
			throw new UsageStatisticException(UsageStatisticException.INVALID_CONFIGURATION);
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
		

		
	}

	private UsageStatistic(String tool,
			CommitingDetailsInterface committingDetails) throws UsageStatisticException {
			
		
		
		setCommittingDetails(committingDetails);
		init(tool);
	}

	public boolean used(String functionality, String parameters) { 
		LogInformation log = new LogInformation(Calendar.getInstance().getTime(), functionality, user, tool, parameters);
		/*log.setDate(Calendar.getInstance().getTime());
		log.setFunctionality(functionality);
		log.setParameters(parameters);
		log.setTool(tool);
		log.setUser(user);*/
		boolean savesucc = dao.saveLog(log);
		dao.closeDatabase();
		return savesucc;
	}
	
	
	public void setCommittingDetails(CommitingDetailsInterface committingDetails)
	{
		if (committingDetails==null)
		{
		this.committingDetails = new CommitingDetailsEmpty();
		}
		else
		{
		this.committingDetails=committingDetails;
		}
	}
	
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
				dao.closeDatabase();
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
			System.out.println(e.getMessage());
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

	

	public static UsageStatistic getInstance(String tool, CommitingDetailsInterface committingDetails) throws UsageStatisticException 
	{
		
		if (instance == null) 
		{
			instance = new UsageStatistic(tool, committingDetails);
			return instance;
		} 
		else
		{
			instance.init(tool);
			instance.setCommittingDetails(committingDetails);
			return instance;
		}

		
	}
	
	public static UsageStatistic getInstance(String tool) throws UsageStatisticException
	{
		return getInstance(tool,null);
	}
	
	public static UsageStatistic getInstance() throws UsageStatisticException{
		return getInstance(null,null);
	}
	
	public void commitWait(){
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
	
	public void closeInstance()
	{
		commitThread.interrupt();//TODO czy to jest bezpieczne?
		commitThread=null;
		dao.closeDatabase();
		dao=null;
		restTemplate=null;
		commitThread=null;
		committingDetails=null;
		instance=null;
		password=null;
		serverURL=null;
		tool=null;
		
	}
	
}
