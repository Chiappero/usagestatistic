package usagestatisticsclient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Random;

import junitx.util.PrivateAccessor;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.firefox.FirefoxDriver;

import usagestatisticsclient.DaoTemporaryDatabaseH2;
import usagestatisticsclient.UsageStatistic;

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
		selenium.open("/logs");
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
		selenium.open("/logs");
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
		Assert.assertTrue(selenium.isTextPresent("User added"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=user","newuser2");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");		
		Assert.assertTrue(selenium.isTextPresent("All fields should be filled"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=password","newuser2");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");	
		Assert.assertTrue(selenium.isTextPresent("All fields should be filled"));
		selenium.open("/addUserClient");
		selenium.waitForPageToLoad("3000");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");	
		Assert.assertTrue(selenium.isTextPresent("All fields should be filled"));
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
		
		selenium.open("/logs");
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
		do
		{
		Thread.sleep(1000);
		funkcjonalnosci=selenium.getSelectOptions("name=users");
		}
		while (funkcjonalnosci[0].equals("£adowanie..."));
		selenium.select("name=users", "user");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");
		Assert.assertTrue(selenium.isTextPresent("Thread"+x));
		Assert.assertTrue(selenium.isTextPresent("500"));
		Assert.assertTrue(localDao.isEmpty());
		TestUtils.addSomeLogsToDao(instance, 100);
		t.run();
		TestUtils.addSomeLogsToDao(instance, 100);
		t1.run();
		
		t.join();
		t1.join();
		Assert.assertTrue(localDao.isEmpty());
		selenium.close();
	}
	
	private boolean isLogged()
	{

		return selenium.isTextPresent("Tool");

	
	}
	String msg="Incorrect date format. please enter date format yyyy-mm-dd format";
	String mmsg="Incorrect month value";
	String dmsg="Incorrect day value";
	String ymsg="Acceptable dates from 1902 to "+Calendar.getInstance().get(Calendar.YEAR);
	
	@Test
	public void AT184_Validate_date() throws NoSuchFieldException, SQLException
	{
		UsageStatistic instance = (UsageStatistic) UsageStatistic.getInstance();
		TestUtils.removeAllLogsFromDao(instance);
		TestUtils.addSomeLogsToDao(instance, 20);
		selenium.open("/logs");
		selenium.waitForPageToLoad("3000");
		selenium.type("name=j_username","nokia");
		selenium.type("name=j_password", "nokia");
		selenium.click("name=submit");
		selenium.waitForPageToLoad("3000");
		selenium.waitForPageToLoad("3000");
		selenium.select("name=tool", "tool");		
		selenium.type("id=dateFrom", "1999-12-10");
		selenium.type("id=dateTill", "1999-12-11");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");
		selenium.isTextPresent("1999-12-10");
		selenium.isTextPresent("1999-12-11");
		selenium.open("/logs");
		selenium.waitForPageToLoad("3000");
		selenium.type("id=dateFrom", "1999-12-101");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "1999-12a-10");
		selenium.click("css=input[type=\"submit\"]");
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "1999-12_10");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "199912a10");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "10-10-1999");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "data");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());
		selenium.type("id=dateFrom", "1999-0-19");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(msg,selenium.getAlert());		
		selenium.type("id=dateFrom", "2010-13-19");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(mmsg,selenium.getAlert());	
		selenium.type("id=dateFrom", "2020-11-19");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(ymsg,selenium.getAlert());			
		selenium.type("id=dateFrom", "2011-02-29");
		selenium.click("css=input[type=\"submit\"]");	
		Assert.assertEquals(dmsg,selenium.getAlert());
		selenium.type("id=dateFrom", "2012-02-29");
		selenium.click("css=input[type=\"submit\"]");	
		selenium.waitForPageToLoad("3000");
		selenium.close();
	}
	
	
}
