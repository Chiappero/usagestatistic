package UsageStatisticClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import junitx.util.PrivateAccessor;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.DefaultSelenium;




public class InterfaceSeleniumTest
{
	static DefaultSelenium selenium;
	
	@Before
	public void initBeforeClass() throws NoSuchFieldException 
	{
		FirefoxDriver firefoxDriver = new FirefoxDriver();
		WebDriverCommandProcessor webDriverCommandProcessor = new WebDriverCommandProcessor("http://localhost:8080/UsageStatisticsServer", firefoxDriver);
		selenium = new DefaultSelenium(webDriverCommandProcessor);

	}
	
	@Test
	public void AT12_1_Authentication() 
	{
		selenium.open("/results");
		selenium.waitForPageToLoad("3000");
		Assert.assertFalse(isLogged());		
		selenium.type("name=j_username","login");
		selenium.type("name=j_password", "haslo");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		Assert.assertFalse(isLogged());
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "haslo");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		Assert.assertFalse(isLogged());
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		Assert.assertTrue(isLogged());
		selenium.open("j_spring_security_logout");
		selenium.waitForPageToLoad("3000");
		Assert.assertFalse(isLogged());		
		selenium.close();

	}
	
	@Test
	public void AT18_1_Proper_show_of_Ajax_view_per_tool() throws Throwable{
		selenium.open("/addUserClient");
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=user","user");
		selenium.type("name=password","user");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("3000");
		TestUtils.createExampleConfigFile();
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 20);
		Random r=new Random();
		final int x=r.nextInt()%10000;
		instance.log("fun"+x, "parameters");
		TestUtils.CommitAndWait(instance.createCommitRunnable(null));
		selenium.open("/results");
		selenium.waitForPageToLoad("3000");
		selenium.select("name=tool", "tool");
		String[] funkcjonalnosci;
		do
		{
		Thread.sleep(1000);
		funkcjonalnosci=selenium.getSelectOptions("name=functionalities");
		}
		while (funkcjonalnosci[0].equals("£adowanie..."));
		boolean flag=false;
		for (int i=0;i<funkcjonalnosci.length&&!flag;i++)
		{
			flag=(funkcjonalnosci[i].equals("fun"+x));
		}
		Assert.assertTrue(flag);
		selenium.close();		
	}
	
	@Test
	public void AT11_2_Proper_and_Invalid_Create_of_credentials() 	
	{	
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=user","newuser");
		selenium.type("name=password","password");
		selenium.click("css=input[type=\"submit\"]");
		selenium.waitForPageToLoad("3000");
		Assert.assertTrue(selenium.isTextPresent("Udalo sie dodac uzytkownika"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=user","newuser2");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");		
		Assert.assertTrue(selenium.isTextPresent("Nie wypelniono wszystkich pol"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=password","newuser2");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");	
		Assert.assertTrue(selenium.isTextPresent("Nie wypelniono wszystkich pol"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");	
		Assert.assertTrue(selenium.isTextPresent("Nie wypelniono wszystkich pol"));
		selenium.close();
	}
	
	@Test
	public void AT182_Pararell_Commit_Threads() throws InterruptedException, SQLException, NoSuchFieldException, IOException{
		TestUtils.createExampleConfigFile();
		PrivateAccessor.setField(UsageStatistic.class, "instance", null);
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		DaoTemporaryDatabaseH2 localDao = TestUtils.getLocalDao(instance);
		TestUtils.addSomeLogsToDao(instance, 100);
		Thread t = new Thread( instance.createCommitRunnable(null));
		t.run();
		t.join();
		Assert.assertTrue(localDao.isEmpty());
		
		TestUtils.addSomeLogsToDao(instance, 100);
		t.run();
		t.join();
		Assert.assertTrue(localDao.isEmpty());
		
		TestUtils.addSomeLogsToDao(instance, 100);
		Thread t1 = new Thread( instance.createCommitRunnable(null));
		t1.run();
		t1.join();
		Assert.assertTrue(localDao.isEmpty());
		
		Random r=new Random();
		final int x=r.nextInt()%10000;
		
		TestUtils.addSomeLogsToDao(instance, 500);
		for(int i=0; i<500; i++){
			instance.log("Thread"+x, "one, two");
		}
		t.run();
		t1.run();
		
		t.join();
		t1.join();
		
		selenium.open("/results");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		
		selenium.select("name=tool", "tool");
		String[] funkcjonalnosci;
		do
		{
		Thread.sleep(1000);
		funkcjonalnosci=selenium.getSelectOptions("name=functionalities");
		}
		while (funkcjonalnosci[0].equals("£adowanie..."));
		selenium.select("name=functionalities", "Thread"+x);
		selenium.select("name=users", "user");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");
		Assert.assertTrue(selenium.isTextPresent("Thread"+x));
		Assert.assertTrue(selenium.isTextPresent("500"));
		Assert.assertTrue(localDao.isEmpty());
		//Thread t2 = new Thread( instance.createCommitRunnable(new CommitingDetails()));
		TestUtils.addSomeLogsToDao(instance, 100);
		t.run();
		TestUtils.addSomeLogsToDao(instance, 100);
		t1.run();
		
		t.join();
		t1.join();
		Assert.assertTrue(localDao.isEmpty());
	}
	
	private boolean isLogged()
	{

		return selenium.isTextPresent("Stan z dnia");

	
	}
}
