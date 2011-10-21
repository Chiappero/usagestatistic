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
	private URI serverURI;
	private String user;
	private String password;
	private String tool;
	private static UsageStatistic instance;
	private RestTemplate restTemplate;
	private DaoTemporaryDatabaseInterface dao;
	private CommitingDetailsInterface committingDetails;

	private void init() throws UsageStatisticException {
		dao = new DaoTemporaryDatabaseH2(); 													// throwsa
		restTemplate = new RestTemplate();
		File file = new File("client-config.cfg"); // TODO2 - zakoduj to i dodaj
													// obsluge wyjatkow
		BufferedReader bufferedReader;
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			serverURI = new URI(bufferedReader.readLine());
			user = bufferedReader.readLine();
			password = bufferedReader.readLine();
			bufferedReader.close();
		}
			catch (URISyntaxException e)
			{
				throw new UsageStatisticException(UsageStatisticException.INVALID_SERVER_URI);
			}
		 catch (IOException e)
		 	{
			 throw new UsageStatisticException(UsageStatisticException.CANNOT_READ_CONFIGURATION_FILE);
		 	}

		

	}

	private UsageStatistic(String tool,
			CommitingDetailsInterface committingDetails) throws UsageStatisticException {
			
		
		if (tool == null)
		{
			tool = "Default Application";
		} 
		else
		{
			this.tool = tool;
		}
		setCommittingDetails(committingDetails);
		init();
	}

	public boolean used(String functionality, String parameters) { 
		try
		{
		LogInformation log = new LogInformation();
		log.setDate(Calendar.getInstance().getTime());
		log.setFunctionality(functionality);
		log.setParameters(parameters);
		log.setTool(tool);
		log.setUser(user);
		return dao.saveLog(log);
		}
		catch (Exception e)
		{
		return false;	
		}
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
					String postForObject = restTemplate.postForObject(serverURI, log, String.class);
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
							throw new ServerDoesntReceiveDataException();
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
		
		
		catch (ServerDoesntReceiveDataException e)
		{
			committingDetails
			.commitingFailureWithError(Errors.SERVER_DOESNT_RECEIVE_DATA);			
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
				
		} catch (SQLException e)
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
		
		catch (Exception e)
		{
			try
			{
			committingDetails.commitingFailureWithError(Errors.FATAL_EXCEPTION);
			}
			catch (Exception e2)
			{}
		}



	}

	

	public static UsageStatistic getInstance(String tool, CommitingDetailsInterface committingDetails) throws UsageStatisticException 
	{
		
		try
		{
			if (instance == null) 
			{
				instance = new UsageStatistic(tool, committingDetails);
				return instance;
			} 
			else
			{
			instance.init();
			instance.setCommittingDetails(committingDetails);
			return instance;
			}
		} 
		catch (UsageStatisticException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new UsageStatisticException(UsageStatisticException.CANNOT_GET_INSTANCE);
		}
		
	}
	
	public static UsageStatistic getInstance(String tool) throws UsageStatisticException
	{
		return getInstance(tool,null);
	}
}
