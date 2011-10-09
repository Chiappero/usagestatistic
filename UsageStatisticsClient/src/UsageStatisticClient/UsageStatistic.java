package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

final public class UsageStatistic {
	private URI serverURI = new URI(
			"http://localhost:8080/UsageStatisticsServer/post");
	private String user;
	private String password;
	private String tool;
	private static UsageStatistic instance;
	RestTemplate restTemplate;
	DaoTemporaryDatabaseInterface dao = new DaoTemporaryDatabaseSerialization(); // TODO
																					// create
																					// DAO
	CommitingDetailsInterface committingDetails;

	private void init() throws IOException, URISyntaxException { // TODO zeby
																	// init i
																	// getInstance
																	// nie mieli
																	// throwsa
		restTemplate = new RestTemplate();
		File file = new File("client-config.cfg"); // TODO2 - zakoduj to i dodaj
													// obsluge wyjatkow
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

		serverURI = new URI(bufferedReader.readLine());
		user = bufferedReader.readLine();
		password = bufferedReader.readLine();

	}

	private UsageStatistic(String tool,
			CommitingDetailsInterface committingDetails) throws IOException,
			URISyntaxException {
		this.tool = tool;
		this.committingDetails = committingDetails;
		init();
	}

	public boolean used(String functionality, String parameters) { // TODO dao
																	// (plik)
																	// sie moze
																	// wysypac
																	// podczas
																	// zapisywania
		LogInformation log = new LogInformation();
		log.setDate(Calendar.getInstance().getTime());
		log.setFunctionality(functionality);
		log.setParameters(parameters);
		log.setTool(tool);
		log.setUser(user);
		return dao.saveLog(log);

	}

	public void commit()
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
					restTemplate.postForObject(serverURI, log, String.class);
					committingDetails.step();
				} else
				{
					committingDetails.stepInvalid();
				}
				
				i++;
				dao.clearFirstLog();

			}
				committingDetails.setInfo("Commiting finised succesful");
				committingDetails.commitingFinishedSuccesful();
		} catch (org.springframework.web.client.ResourceAccessException e)
		{
			committingDetails
			.commitingFailureWithError("Error with connection to server");
				
		} catch (SQLException e)
		{
			committingDetails
			.commitingFailureWithError("Error with connection to local database");
		}
	}

	

	public static UsageStatistic getInstance(String tool,
			CommitingDetailsInterface committingDetails) {
		if (instance == null) {
			try {
				if (committingDetails == null) {
					committingDetails = new CommitingDetailsEmpty();
				}
				if (tool == null) {
					tool = "Domyslna Aplikacja";
				}
				instance = new UsageStatistic(tool, committingDetails);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return instance;
		} else
			return instance;
	}
}
