package UsageStatisticClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;
import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Test;

import UsageStatisticClient.UsageStatistic;

public class UsageStatisticTest {

	/*@Test
	public void TestPost() throws URISyntaxException
	{
RestTemplate restTemplate = new RestTemplate();
LogInformation log = new LogInformation();
URI uri  =new URI("http://localhost:8080/UsageStatisticsServer/post");
restTemplate.postForObject(uri, log, String.class);
	}*/
		
	
	/*@Test
	public void methodUsed() throws SQLException, NoSuchFieldException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		Assert.assertTrue(instance.used("funkcjonalnosc", "parametry"));
		TestUtils.removeAllLogsFromDao(instance);
	}*/
	
	/*@Test
	public void methodCommit() throws NoSuchFieldException, SQLException, InterruptedException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		for (int i=0;i<100;i++)
		{
			//System.out.println("ITERACJA BEFORE"+i);
			instance.used("funkcjonalnosc", "parametry");
			//System.out.println("ITERACJA AFTER"+i);
		}
		//System.out.println("COMMIT BEFORE");
		
		instance.setCommittingDetails(new CommitingDetailsTestImp(1));
		instance.commit();
		//System.out.println("COMMIT AFTER");
		
		
	}*/
		
//	@Test
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
	
	@Test
	public void AT22_Server_doesnt_receive_data() throws NoSuchFieldException, SQLException, URISyntaxException
	{
		

		CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
	
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 46);
		PrivateAccessor.setField(instance, "serverURI", new URI("localhost:8123"));
		instance.commit();
		Assert.assertEquals(inter.msg,"Error with connection to server");
		
		PrivateAccessor.setField(instance, "serverURI", new URI("http://fakeaddress.pl"));
		instance.commit();
		Assert.assertEquals(inter.msg,"Error with server - server doesn't receive data");
		TestUtils.addSomeLogsToDao(instance, 70);
		Assert.assertEquals(TestUtils.getLogsAmmount(instance), 46+70);
		
	}
	
	
	
		
	
	
}
	
	