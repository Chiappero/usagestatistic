package UsageStatisticClient;

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
	public void TestUsedCLear()
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
}
	
	