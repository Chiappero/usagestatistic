package UsageStatisticClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.web.client.RestTemplate;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import UsageStatisticClientConfigGenerator.ConfigGenerator;

public class UsageStatisticTest {
	
	@BeforeClass
	public static void initBeforeClass() 
	{
		FirefoxDriver firefoxDriver = new FirefoxDriver();
		WebDriverCommandProcessor webDriverCommandProcessor = new WebDriverCommandProcessor("http://localhost:8080/UsageStatisticsServer", firefoxDriver);
		Selenium selenium = new DefaultSelenium(webDriverCommandProcessor);
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=user","user");
		selenium.type("name=password","user");
		selenium.click("css=input[type=\"submit\"]");
		selenium.close();
	}
	
	
	@Before
	public void przed() throws IOException
	{
		TestUtils.createExampleConfigFile();
	}
	
	
	@Test
	public void AT21_Proper_commit() throws Throwable
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		instance.log("funkcjonalnosc", "parametry");
		DaoTemporaryDatabaseH2 localDao = TestUtils.getLocalDao(instance);
		Assert.assertEquals(localDao.getLogsAmount(),1);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(localDao.isEmpty());
	}
	
	@Test
	public void AT11_1_Check_credentials() throws Throwable 
	{
		LogInformation log = new LogInformation(new Date(), "funkcjonalnosc", "user", "narzedzie", "parametry");
		PairLogInformationAndPassword pair = new PairLogInformationAndPassword(log, Ciphers.SHA256("user"));
		 RestTemplate restTemplate = new RestTemplate();
		Assert.assertEquals(restTemplate.postForObject("http://localhost:8080/UsageStatisticsServer/post",pair,String.class),"OK");
		pair = new PairLogInformationAndPassword(log, "bledneHaslo");
		Assert.assertEquals(restTemplate.postForObject("http://localhost:8080/UsageStatisticsServer/post",pair,String.class),"CANNOT_AUTHENTICATE");
		log.setUser("blednyUser");
		pair = new PairLogInformationAndPassword(log, "user");
		Assert.assertEquals(restTemplate.postForObject("http://localhost:8080/UsageStatisticsServer/post",pair,String.class),"CANNOT_AUTHENTICATE");
	}
	@Test
	public void AT22_Server_doesnt_receive_data() throws Throwable
	{
		CommitingDetailsTestImp2 inter=new CommitingDetailsTestImp2();
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(inter);
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
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(inter);		
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
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(inter);				
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
		instance.setCommitListener(com);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(com.msg, Errors.LOG_WAS_NULL);
		
		
	}
	
	@Test 
	public void AT26_Empty_Local_Database() throws Throwable
	{
		CommitingDetailsTestImp inter=new CommitingDetailsTestImp(0);
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(inter);
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
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(inter);		
		DaoTemporaryDatabaseInterface daoMock = new DaoTemporaryDatabaseH2TestImp();
		PrivateAccessor.setField(instance, "dao", daoMock);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(inter.msg,Errors.ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE);
	}
	
	
	@Test
	public void AT210_Paralell_Commits() throws Throwable //TODO rzadko bo rzadko, ale czasem tu nie wyrabia
	{
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
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
	public void AT41_Proper_load_configuration_from_file() throws UsageStatisticException, NoSuchFieldException, URISyntaxException, IOException
	{
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "matuszek", "password", "tool");
		//TestUtils.createExampleConfigFile();
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		URI serverURL = (URI) PrivateAccessor.getField(instance, "serverURL");
		Assert.assertEquals(new URI("http://localhost:8080/UsageStatisticsServer/post"), serverURL);
		String user = (String) PrivateAccessor.getField(instance, "user");
		Assert.assertEquals("matuszek", user);
		String password = (String) PrivateAccessor.getField(instance, "password");
		Assert.assertEquals(Ciphers.SHA256("password"), password);
		String tool = (String) PrivateAccessor.getField(instance, "tool");
		Assert.assertEquals("tool", tool);
	}
	
	@Test
	public void AT42ANDAT94_Handle_invalid_load_or_no_configuration_fileANDSuitable_exceptions_thrown_by_each_invalid_case() throws NoSuchFieldException, IOException
	{	
		
		
		File f = new File("client-config.cfg");
		f.delete();
		UsageLogger ul=UsageStatistic.getInstance();
		Assert.assertTrue(ul instanceof UsageLoggerEmpty);
		TestUtils.createExampleConfigFile();
		f = new File("client-config.cfg");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
		out.write("test");
		out.close();
		ul=UsageStatistic.getInstance();
		Assert.assertTrue(ul instanceof UsageLoggerEmpty);
		
		File err = new File("debuglog.txt");
		err.delete();
		ConfigGenerator.createConfigFile("client-config.cfg", "", "", "", "");
		ul=UsageStatistic.getInstance();
		Assert.assertTrue(ul instanceof UsageLoggerEmpty);
		Assert.assertTrue(TestUtils.readLineFromDebugLog().contains(UsageStatisticException.CONFIG_ERROR));
		//jeden typ bledu dla blednej konfiguracji, skoro i tak jest zaszyfrowane a sprawdzanie jest tylko pustosci
		
		
		
		
	}
	

	
	
	@Test
	public void AT51_Proper_usage_of_thread() throws Throwable
	{
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		DaoTemporaryDatabaseH2 localDao = TestUtils.getLocalDao(instance);
		for(int i=0; i<1000; i++){
			instance.log("test"+i, "paremtry"+i);
		}
		instance.commit();
		Assert.assertFalse(localDao.isEmpty());
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(localDao.isEmpty());
		for(int i=0; i<300; i++){
			instance.log("test"+i, "paremtry"+i);
		}
		instance.commit();
		for(int i=0; i<100; i++){
			instance.log("test"+i, "paremtry"+i);
		}
		Assert.assertFalse(localDao.isEmpty());
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertFalse(localDao.isEmpty());
		
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(localDao.isEmpty());
		
	}
	
	
	
	
	
	@Test
	public void AT61_Proper_flow_of_usage_commit_interface() throws Throwable
	{
		CommitingDetailsTestImp4 com = new CommitingDetailsTestImp4();
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(com);
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
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		CommitingDetailsTestImp3 com = new CommitingDetailsTestImp3();
		instance.setCommitListener(com);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertTrue(com.success);
	}
	
	@Test
	public void AT63_Commit_with_failure() throws Throwable
	{
		//Remaining tests of error codes are included in other testcases 
		final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		RestTemplate rest = (RestTemplate) PrivateAccessor.getField(instance, "restTemplate");
		URI serverURL = (URI) PrivateAccessor.getField(instance, "serverURL");
		PairLogInformationAndPassword pair = new PairLogInformationAndPassword(new LogInformation(null, null, "user", null, null), Ciphers.SHA256("user")); //dodane credentiale celowo
		Assert.assertEquals(rest.postForObject(serverURL, pair , String.class),"ERROR");
	}
	
	
	@Test
	public void AT64_Commit_with_invalid_steps() throws Throwable
	{
	final UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
	DaoTemporaryDatabaseInterface localDao=new DaoTemporaryDatabaseH2TestImp2();
	PrivateAccessor.setField(instance, "dao", localDao);
	CommitingDetailsTestImp3 com = new CommitingDetailsTestImp3();
	instance.setCommitListener(com);
	instance.commit();
	PrivateAccessor.invoke(instance, "commitWait", null, null);
	Assert.assertEquals(com.msg, Errors.LOG_WAS_NULL);
	Assert.assertTrue(com.success);
	}


	
	@Test
	public void AT91_Proper_create_local_database_for_each_configuration() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		
		String oryginal = System.getProperty("user.dir");
		
		System.setProperty("user.dir",oryginal+"\\baza1");
		
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance(); //stworz baze w:	 baza1/db.h2.db
		TestUtils.getLocalDao(instance).closeDatabase();
		Assert.assertTrue(new File("db.h2.db").exists());
		
		System.setProperty("user.dir",oryginal+"\\baza2");
		
		instance = (UsageStatistic) UsageStatistic.getInstance(); 				//stworz baze w:	 baza2/db.h2.db
		TestUtils.getLocalDao(instance).closeDatabase();
		Assert.assertTrue(new File("db.h2.db").exists());
		
		System.setProperty("user.dir", oryginal);
		
		Assert.assertTrue(new File("baza1\\db.h2.db").exists());
		Assert.assertTrue(new File("baza2\\db.h2.db").exists());
		Assert.assertTrue(TestUtils.deleteDir(new File("baza1")));
		Assert.assertTrue(TestUtils.deleteDir(new File("baza2")));
		
		Assert.assertFalse(new File("baza1\\db.h2.db").exists());
		Assert.assertFalse(new File("baza2\\db.h2.db").exists());

	}
	
	 
	
	
	@Test
	public void AT92_Proper_load_of_new_configuration_file_when_creating_new_instance() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		Assert.assertEquals("user", (String) PrivateAccessor.getField(instance, "user"));
		Assert.assertEquals(Ciphers.SHA256("user"), (String) PrivateAccessor.getField(instance, "password"));
		Assert.assertEquals("tool", (String) PrivateAccessor.getField(instance, "tool"));
		ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer", "AT92user", "AT92pass", "AT92tool");
		//TestUtils.createExampleConfigFile();
		instance = (UsageStatistic) UsageStatistic.getInstance();
		Assert.assertEquals("AT92tool", (String) PrivateAccessor.getField(instance, "tool"));
	}	
	
	@Test
	public void AT93_Proper_load_of_new_initiation_parameters_Commiting_Details_and_Tool_when_create_new_instance() throws IOException, UsageStatisticException, NoSuchFieldException
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		CommitingDetailsTestImp4 com = new CommitingDetailsTestImp4();
		instance = (UsageStatistic) UsageStatistic.getInstance();
		instance.setCommitListener(com);
		instance = (UsageStatistic) UsageStatistic.getInstance();
		//if interface not changed it should throw "cannot cast" exception and fail the test
		Assert.assertEquals("tool", (String) PrivateAccessor.getField(instance, "tool"));	
		TestUtils.createExampleConfigFile();
		instance = (UsageStatistic) UsageStatistic.getInstance();
		Assert.assertEquals("tool", (String) PrivateAccessor.getField(instance, "tool"));	
		TestUtils.createExampleConfigFile();
		instance = (UsageStatistic) UsageStatistic.getInstance();

	}		
	
	@Test
	public void AT201_Log_Count() throws Throwable
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		Assert.assertEquals(0,instance.getLogsCount());
		TestUtils.addSomeLogsToDao(instance, 100);
		Assert.assertEquals(100,instance.getLogsCount());
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(0,instance.getLogsCount());
	}	
	
	@Test
	public void AT202_Date_of_oldest_commit() throws Throwable
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		Assert.assertNull(instance.getOldestLogDate());
		java.util.Date date=Calendar.getInstance().getTime();
		TestUtils.addSomeLogsToDao(instance, 100);
		Assert.assertEquals(date,instance.getOldestLogDate());
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertNull(instance.getOldestLogDate());
	}	
	
	@Test
	public void AT203_List_of_stored_logs() throws Throwable
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		Assert.assertEquals(0,instance.getAllLogs().size());
		for (int i=0;i<10;i++)
			instance.log(""+i, "");
		List<LogInformation> logs=instance.getAllLogs();
		Assert.assertEquals(10,instance.getAllLogs().size());
		for (int i=0;i<10;i++)		
			Assert.assertEquals(""+i, logs.get(i).getFunctionality());
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(0,instance.getAllLogs().size());
	}	
	
	
	@Test
	public void AT29_Large_Data() throws Throwable
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 1000);
		instance.commit();
		PrivateAccessor.invoke(instance, "commitWait", null, null);
		Assert.assertEquals(0,TestUtils.getLogsAmmount(instance));
	}
	
	
}