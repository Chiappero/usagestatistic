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

	@Test 
	public void AT24_Connection_Lost() throws NoSuchFieldException, SQLException, URISyntaxException
	{

		final CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		final URI original=(URI) PrivateAccessor.getField(instance, "serverURI");
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 500);		
		Assert.assertEquals(500,TestUtils.getLogsAmmount(instance));	
		Thread t=new Thread()
			{
				@Override
				public void run()
				{
					try {
						Thread.sleep(500);

							PrivateAccessor.setField(instance, "serverURI", new URI("localhost:8123"));

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					 }
					 catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
//					catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			};
		t.start();
		instance.commit();
		Assert.assertEquals("Error with connection to server",inter.msg);
	//	System.out.println(TestUtils.getLogsAmmount(instance));
		Assert.assertTrue(TestUtils.getLogsAmmount(instance)<500&&TestUtils.getLogsAmmount(instance)>0);
			PrivateAccessor.setField(instance, "serverURI", original);
			instance.commit();
			Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
		
		
	}	
	
	@Test 
	public void AT26_Empty_Local_Database() throws NoSuchFieldException, SQLException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.removeAllLogsFromDao(instance);
		instance.commit();
		TestUtils.addSomeLogsToDao(instance, 50);
		TestUtils.corruptFile(instance);
		TestUtils.addSomeLogsToDao(instance, 50);
		Assert.assertEquals(50,TestUtils.getLogsAmmount(instance));
		
	}
	
	@Test
	public void AT29_Large_Data() throws NoSuchFieldException, SQLException
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 10000);
		instance.commit();
		Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
	}
	
	@Test
	public void AT210_Paralell_Commits() throws NoSuchFieldException, SQLException
	{
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 1000);
		Thread[] tg=new Thread[10];
		
		for (int i=0;i<10;i++)
		{
			
			tg[i]=new Thread()
			{
			@Override
			public void run()
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				instance.commit();
				
			}
		};
		}
		for (int i=0;i<10;i++)
			tg[i].start();
		instance.commit();
		Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
		
		
	}

	
	
	
		
	
	
}
	
	