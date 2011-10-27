package UsageStatisticClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import UsageStatisticClient.UsageStatistic;
import UsageStatisticClientConfigGenerator.ConfigGenerator;

public class UsageStatisticTest {

	
	
	@Test
	public void AT21_Proper_commit() throws Throwable
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", new CommitingDetailsTestImp(1));
		TestUtils.removeAllLogsFromDao(instance);
		instance.used("funkcjonalnosc", "parametry");
		DaoTemporaryDatabaseH2 localDao = TestUtils.getLocalDao(instance);
		Assert.assertEquals(localDao.getLogsAmount(),1);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(localDao.isEmpty());
	}
			
	
//	@Test
	public void methodCommit() throws Throwable
	{
		commitLogs(99); 
		
	}
	
	private void commitLogs(int amountRecord) throws Throwable
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
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		System.out.println("commit");
		TestUtils.removeAllLogsFromDao(instance);
		System.out.println("removeAllLogs2");
	}
	
	@Test
	public void AT22_Server_doesnt_receive_data() throws Throwable
	{
		

		CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 46);
		PrivateAccessor.setField(instance, "serverURL", new URI("localhost:8123"));
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(inter.msg,Errors.ERROR_WITH_CONNECTION_TO_SERVER);
		
		PrivateAccessor.setField(instance, "serverURL", new URI("http://fakeaddress.pl"));
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(inter.msg.equals(Errors.SERVER_DOESNT_RECEIVE_DATA)||inter.msg.equals(Errors.ERROR_WITH_CONNECTION_TO_SERVER)||inter.msg.equals(Errors.CANNOT_EXTRACT_RESPONSE));
		TestUtils.addSomeLogsToDao(instance, 70);
		Assert.assertEquals(TestUtils.getLogsAmmount(instance), 46+70);
		
	}

	@Test 
	public void AT24_Connection_Lost() throws Throwable
	{

		final CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		final URI original=(URI) PrivateAccessor.getField(instance, "serverURL");
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

							PrivateAccessor.setField(instance, "serverURL", new URI("localhost:8123"));

					} catch (InterruptedException e) {
						Assert.fail();
					}
					 catch (NoSuchFieldException e) {
						 Assert.fail();
					 }
					 catch (URISyntaxException e) {
						 Assert.fail();
					} 
				}
			};
		t.start();
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(Errors.ERROR_WITH_CONNECTION_TO_SERVER,inter.msg);
		Assert.assertTrue(TestUtils.getLogsAmmount(instance)<500&&TestUtils.getLogsAmmount(instance)>0);
			PrivateAccessor.setField(instance, "serverURL", original);
			instance.commit();
			PrivateAccessor.invoke(instance, "commitWait", null, null);
			Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
		
		
	}	
	
	
	@Test
	public void AT25_Invalid_readout_log() throws Throwable
	{
		CommitingDetailsTestImp inter = new CommitingDetailsTestImp(0);
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		TestUtils.removeAllLogsFromDao(instance);//0
		DaoTemporaryDatabaseInterface localDao = TestUtils.getLocalDao(instance);
		Assert.assertTrue(localDao.isEmpty());
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(inter.success);
		
		localDao.saveLog(TestUtils.getExampleLog());//1
		
		localDao.closeDatabase();
		Assert.assertNotNull(localDao.getFirstLog());//GETFIRSTLOG ratuje
		Assert.assertEquals(localDao.getLogsAmount(),1);
		
		TestUtils.dropTable((DaoTemporaryDatabaseH2) localDao); //0
		Assert.assertNull(localDao.getFirstLog());//GETFIRSTLOG ratuje
		Assert.assertTrue(localDao.isEmpty());
		
		localDao.saveLog(TestUtils.getExampleLog());//1
		TestUtils.makeConnectionNull((DaoTemporaryDatabaseH2) localDao);
		Assert.assertNotNull(localDao.getFirstLog());//GETFIRSTLOG ratuje
		Assert.assertEquals(localDao.getLogsAmount(),1);//1
		
		localDao.closeDatabase();
		Assert.assertFalse(localDao.isEmpty()); //ISEMPTY ratuje
		
		TestUtils.dropTable((DaoTemporaryDatabaseH2) localDao); //0
		Assert.assertTrue(localDao.isEmpty());//ISEMPTY ratuje
		
		TestUtils.makeConnectionNull((DaoTemporaryDatabaseH2) localDao);
		Assert.assertTrue(localDao.isEmpty());//ISEMPTY ratuje
		
		localDao.closeDatabase();
		Assert.assertEquals(localDao.getLogsAmount(),0); //GETLOGSAMOUNT ratuje
		localDao.saveLog(TestUtils.getExampleLog());//1
		
		TestUtils.dropTable((DaoTemporaryDatabaseH2) localDao); //0
		Assert.assertEquals(localDao.getLogsAmount(),0); //GETLOGSAMOUNT ratuje
		
		localDao.saveLog(TestUtils.getExampleLog());//1
		TestUtils.makeConnectionNull((DaoTemporaryDatabaseH2) localDao);
		Assert.assertEquals(localDao.getLogsAmount(),1); //GETLOGSAMOUNT ratuje
		
		
		localDao=new DaoTemporaryDatabaseH2TestImp2();
		PrivateAccessor.setField(instance, "dao", localDao);
		CommitingDetailsTestImp3 com = new CommitingDetailsTestImp3();
		instance.setCommittingDetails(com);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(com.msg, Errors.LOG_WAS_NULL);
		
		
	}
	
	@Test 
	public void AT26_Empty_Local_Database() throws Throwable
	{
		CommitingDetailsTestImp inter=new CommitingDetailsTestImp(0);
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		TestUtils.removeAllLogsFromDao(instance);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(inter.success);
		TestUtils.addSomeLogsToDao(instance, 50);
		TestUtils.corruptFile(instance);
		TestUtils.addSomeLogsToDao(instance, 50);
		Assert.assertEquals(50,TestUtils.getLogsAmmount(instance));
		
	}
	
	@Test
	public void AT28_Cannot_delete_log_from_local_database() throws Throwable
	{
		CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", inter);
		DaoTemporaryDatabaseInterface daoMock = new DaoTemporaryDatabaseH2TestImp();
		PrivateAccessor.setField(instance, "dao", daoMock);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(inter.msg,Errors.ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE);
	}

	
	
	
	@Test
	public void AT210_Paralell_Commits() throws Throwable
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
				}
				instance.commit();
				try
				{
					PrivateAccessor.invoke(instance, "commitWait", null, null);
				} catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		}
		for (int i=0;i<10;i++)
			tg[i].start();
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
		
		
	}
	
	@Test
	public void AT41_Proper_load_configuration_from_file() throws UsageStatisticException, NoSuchFieldException, URISyntaxException
	{
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		URI serverURL = (URI) PrivateAccessor.getField(instance, "serverURL");
		Assert.assertEquals(new URI("http://localhost:8080/UsageStatisticsServer/post"), serverURL);
		String user = (String) PrivateAccessor.getField(instance, "user");
		Assert.assertEquals("matuszek", user);
		String password = (String) PrivateAccessor.getField(instance, "password");
		Assert.assertEquals("password", password);
	}
	
	@Test
	public void AT42ANDAT94_Handle_invalid_load_or_no_configuration_fileANDSuitable_exceptions_thrown_by_each_invalid_case() throws NoSuchFieldException, IOException
	{	//TODO kiedy jest zwracany UsageStatisticException.CANNOT_GET_INSTANCE
		
		TestUtils.createExampleConfigFile();
		//FileCopy.copy("client-config.cfg", "kopia.cfg");
		File f = new File("client-config.cfg");
		f.delete();
		try
		{
			final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.CANNOT_READ_CONFIGURATION_FILE);
		}
		
		TestUtils.createExampleConfigFile();
		f = new File("client-config.cfg");
		 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
		 out.write("test");
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertTrue(e.getMessage().equals(UsageStatisticException.CANNOT_READ_CONFIGURATION_FILE)||e.getMessage().equals(UsageStatisticException.INVALID_CONFIGURATION)); //TODO czy da sie to zrobic porzadnie;)
		}
		out.close();
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "", "matuszek", "password", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_SERVER_URL);
		}
		
		
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", null, "matuszek", "password", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_SERVER_URL);
		}
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", null, "password", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_CONFIGURATION);
		}
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "matuszek", null, null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_CONFIGURATION);
		}
		
		
		
		
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "", "matuszek", "password", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_SERVER_URL);
		}
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "", "password", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_CONFIGURATION);
		}
		
		f = new File("client-config.cfg");
		f.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "matuszek", "", null);
		try
		{
			UsageStatistic.getInstance("aplikacja", null);
			Assert.fail();
		} catch (UsageStatisticException e)
		{
			Assert.assertEquals(e.getMessage(),UsageStatisticException.INVALID_CONFIGURATION);
		}
		TestUtils.createExampleConfigFile();
	}
	
	@Test
	public void AT43_Override_configuration() throws UsageStatisticException, NoSuchFieldException, SQLException, IOException
	{
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithTool(); // Konfiguracja - TAK, instancja - TAK ==> instancja
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		String tool = (String) PrivateAccessor.getField(instance, "tool");
		Assert.assertEquals(tool,"aplikacja");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithTool(); // Konfiguracja - TAK, instancja - NIE ==> konfiguracja
		final UsageStatistic instance2 = UsageStatistic.getInstance(null, null);
		tool = (String) PrivateAccessor.getField(instance2, "tool");
		Assert.assertEquals(tool,"tool");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithTool(); // Konfiguracja - TAK, instancja - NIE ==> konfiguracja
		final UsageStatistic instance3 = UsageStatistic.getInstance("", null);
		tool = (String) PrivateAccessor.getField(instance3, "tool");
		Assert.assertEquals(tool,"tool");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFile(); // Konfiguracja - NIE, instancja - NIE ==> Default Application
		final UsageStatistic instance4 = UsageStatistic.getInstance(null, null);
		tool = (String) PrivateAccessor.getField(instance4, "tool");
		Assert.assertEquals(tool,"Default Application");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFile(); // Konfiguracja - NIE, instancja - NIE ==> Default Application
		final UsageStatistic instance5 = UsageStatistic.getInstance("", null);
		tool = (String) PrivateAccessor.getField(instance5, "tool");
		Assert.assertEquals(tool,"Default Application");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithToolEmpty(); // Konfiguracja - NIE, instancja - NIE ==> Default Application
		final UsageStatistic instance6 = UsageStatistic.getInstance(null, null);
		tool = (String) PrivateAccessor.getField(instance6, "tool");
		Assert.assertEquals(tool,"Default Application");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithToolEmpty(); // Konfiguracja - NIE, instancja - NIE ==> Default Application
		final UsageStatistic instance7 = UsageStatistic.getInstance("", null);
		tool = (String) PrivateAccessor.getField(instance7, "tool");
		Assert.assertEquals(tool,"Default Application");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFile(); // Konfiguracja - NIE, instancja - TAK ==> instancja
		final UsageStatistic instance8 = UsageStatistic.getInstance("aplikacja", null);
		tool = (String) PrivateAccessor.getField(instance8, "tool");
		Assert.assertEquals(tool,"aplikacja");
		
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		TestUtils.createExampleConfigFileWithToolEmpty(); // Konfiguracja - NIE, instancja - TAK ==> instancja
		final UsageStatistic instance9 = UsageStatistic.getInstance("aplikacja", null);
		tool = (String) PrivateAccessor.getField(instance9, "tool");
		Assert.assertEquals(tool,"aplikacja");
	}
	
	@Test
	public void AT52_Handle_all_exception_thrown_by_each_public_method() throws Throwable
	{
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		PrivateAccessor.setField(instance, "dao", null);
		try
		{
		instance.used("test", "test");
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		}
		catch (Exception e)
		{
			Assert.fail();
		}
		//TODO nie skonczone, nie wiem jak przetestowac getInstance, setCommitingDetails
		
		
		
		
	}
	
	@Test
	public void AT61_Proper_flow_of_usage_commit_interface() throws Throwable
	{
		CommitingDetailsTestImp4 com = new CommitingDetailsTestImp4();
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", com);
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 5);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(com.licznik, 10);
		Assert.assertEquals(com.amount, 5);
		Assert.assertTrue(com.commitingFinishedSuccesful);
		Assert.assertTrue(com.commitingStart);
	}
	
	@Test
	public void AT62_Empty_commit() throws Throwable
	{
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.removeAllLogsFromDao(instance);
		CommitingDetailsTestImp3 com = new CommitingDetailsTestImp3();
		instance.setCommittingDetails(com);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(com.success);
	}
	
	@Test
	public void AT63_Commit_with_failure() throws Throwable
	{
		//Remaining tests of error codes are included in other testcases 
		final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		RestTemplate rest = (RestTemplate) PrivateAccessor.getField(instance, "restTemplate");
		URI serverURL = (URI) PrivateAccessor.getField(instance, "serverURL");
		Assert.assertEquals(rest.postForObject(serverURL, new LogInformation(null, null, null, null, null), String.class),"ERROR");
	}
	
	
	
	@Test
	public void AT64_Commit_with_invalid_steps() throws Throwable
	{
	final UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
	DaoTemporaryDatabaseInterface localDao=new DaoTemporaryDatabaseH2TestImp2();
	PrivateAccessor.setField(instance, "dao", localDao);
	CommitingDetailsTestImp3 com = new CommitingDetailsTestImp3();
	instance.setCommittingDetails(com);
	instance.commit();
	PrivateAccessor.invoke(instance, "commitWait", null, null);
	Assert.assertEquals(com.msg, Errors.LOG_WAS_NULL);
	Assert.assertTrue(com.success);
	}
	
	@Test
	public void AT91_Proper_create_local_database_for_each_configuration() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		System.setProperty("user.dir","D:\\JAVA\\Repo\\UsageStatisticsClient\\baza1");
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.getLocalDao(instance).closeDatabase();
		System.setProperty("user.dir","D:\\JAVA\\Repo\\UsageStatisticsClient\\baza2");
		instance = UsageStatistic.getInstance("aplikacja", null); //TODO dalej wczytuje z UsageStatisticClient zamiast z BazaX client-configa..
		TestUtils.getLocalDao(instance).closeDatabase();
		Assert.assertTrue(new File("baza1/db.h2.db").exists());
		Assert.assertTrue(new File("baza2/db.h2.db").exists());
		Assert.assertTrue(TestUtils.deleteDir(new File("baza1")));
		Assert.assertTrue(TestUtils.deleteDir(new File("baza2")));

	}
	
	 
	
	
	@Test
	public void AT92_Proper_load_of_new_configuration_file_when_creating_new_instance() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		TestUtils.createExampleConfigFile();
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		Assert.assertEquals("matuszek", (String) PrivateAccessor.getField(instance, "user"));
		Assert.assertEquals("password", (String) PrivateAccessor.getField(instance, "password"));
		Assert.assertEquals("aplikacja", (String) PrivateAccessor.getField(instance, "tool"));
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "AT92user", "AT92pass", "AT92tool");
		instance = UsageStatistic.getInstance("tool92", null);
		Assert.assertEquals("tool92", (String) PrivateAccessor.getField(instance, "tool"));		
		instance = UsageStatistic.getInstance();
		Assert.assertEquals("AT92tool", (String) PrivateAccessor.getField(instance, "tool"));
	}	
	
	@Test
	public void AT93_Proper_load_of_new_initiation_parameters_Commiting_Details_and_Tool_when_create_new_instance() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		TestUtils.createExampleConfigFile();
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		CommitingDetailsEmpty empty=(CommitingDetailsEmpty)PrivateAccessor.getField(instance, "committingDetails");
		CommitingDetailsTestImp4 com = new CommitingDetailsTestImp4();
		instance = UsageStatistic.getInstance("aplikacja", com);
		CommitingDetailsTestImp4 notempty=(CommitingDetailsTestImp4)PrivateAccessor.getField(instance, "committingDetails");
		instance = UsageStatistic.getInstance("aplikacja");
		empty=(CommitingDetailsEmpty)PrivateAccessor.getField(instance, "committingDetails");
		//if interface not changed it should throw "cannot cast" exception and fail the test
		Assert.assertEquals("aplikacja", (String) PrivateAccessor.getField(instance, "tool"));	
		TestUtils.createExampleConfigFileWithTool();
		instance = UsageStatistic.getInstance();
		Assert.assertEquals("tool", (String) PrivateAccessor.getField(instance, "tool"));	
		instance = UsageStatistic.getInstance("tool2");
		Assert.assertEquals("tool2", (String) PrivateAccessor.getField(instance, "tool"));
		TestUtils.createExampleConfigFile();
		instance = UsageStatistic.getInstance();
		Assert.assertEquals("Default Application", (String) PrivateAccessor.getField(instance, "tool"));
	}		
	
	@Test
	public void AT29_Large_Data() throws Throwable
	{
		UsageStatistic instance = UsageStatistic.getInstance("aplikacja", null);
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 10000);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
	}
	
	
}
	
	