package UsageStatisticClient;

import java.sql.SQLException;
import java.util.Calendar;

import org.junit.Test;

public class UsageStatisticTest {
	
	/*@Test
	public void TestPost() throws URISyntaxException
	{
RestTemplate restTemplate = new RestTemplate();
LogInformation log = new LogInformation();
URI uri  =new URI("http://localhost:8080/UsageStatisticsServer/post");
restTemplate.postForObject(uri, log, String.class);
	}*/
	
	@Test
	public void TestUsedCLear() throws SQLException
	{
		String functionality="klikniecie";
		String parameters="x=10,y=10";
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		instance.used(functionality, parameters);
		instance.dao.clearFirstLog();
	}
	
	@Test
	public void TestLogsAmount()
	{
		String functionality="klikniecie";
		String parameters="x=10,y=10";
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		instance.used(functionality, parameters);
		instance.commit();
	}
	
	@Test
	public void TestDatabase()
	{
		DaoTemporaryDatabaseH2 dao=new DaoTemporaryDatabaseH2();
		dao.openDatabase();
		try {
			
		//	dao.saveLog(new LogInformation(Calendar.getInstance().getTime(),"test","testujacy","testowe narzedzie","test_number=1"));
			System.out.println(dao.getLogsAmount());
			dao.clearFirstLog();
			System.out.println(dao.getLogsAmount());			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dao.closeDatabase();
		
		
	}	
	
	
}
	
	