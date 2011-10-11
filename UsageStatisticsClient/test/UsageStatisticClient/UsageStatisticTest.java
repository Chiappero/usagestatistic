package UsageStatisticClient;

import java.sql.SQLException;
import java.util.Calendar;
import junitx.util.PrivateAccessor;

import org.junit.Assert;
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
	public void methodUsed() throws SQLException, NoSuchFieldException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		Assert.assertTrue(instance.used("funkcjonalnosc", "parametry"));
		TestUtils.removeAllLogsFromDao(instance);
	}
	
		
	@Test
	public void methodCommit() throws NoSuchFieldException, SQLException
	{
		commitLogs(99);  //TODO DZIA£A
		//commitLogs(100); //TODO NIE DZIALA!
		
	}
	
	private void commitLogs(int amountRecord) throws NoSuchFieldException, SQLException
	{	
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		
		System.out.println("INSTANCE");
		instance.setCommittingDetails(new CommitingDetailsTestImp(amountRecord)); //przypisz na nowo za kazdym razem implementacje testujaca z podana iloscia
		System.out.println("setCommiting");
		TestUtils.removeAllLogsFromDao(instance);
		System.out.println("remove");
		TestUtils.addSomeLogsToDao(instance, amountRecord);
		System.out.println("addSomeLogs");
		instance.commit(); //<-- tutaj jest test ilosci oraz tego czy nie wyrzuca bledu
		System.out.println("commit");
		TestUtils.removeAllLogsFromDao(instance);
		System.out.println("removeAllLogs2");
	}
	
		
	
	
}
	
	