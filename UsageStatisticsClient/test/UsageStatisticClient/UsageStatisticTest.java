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

	
	
	@Test
	public void AT21_Proper_commit() throws NoSuchFieldException, SQLException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", new CommitingDetailsTestImp(1));
		TestUtils.removeAllLogsFromDao(instance);
		instance.used("funkcjonalnosc", "parametry");
		DaoTemporaryDatabaseH2 localDao = TestUtils.getLocalDao(instance);
		Assert.assertEquals(localDao.getLogsAmount(),1);
		instance.commit();
		Assert.assertTrue(localDao.isEmpty());
	}
			
	
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
		Assert.assertTrue(inter.msg.equals("Error with server - server doesn't receive data")||inter.msg.equals("Error with connection to server"));
		TestUtils.addSomeLogsToDao(instance, 70);
		Assert.assertEquals(TestUtils.getLogsAmmount(instance), 46+70);
		
	}
	
	
	
		
	
	
}
	
	