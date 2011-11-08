package UsageStatisticClient;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;



public class InterfaceSeleniumTests
{
	static DefaultSelenium selenium;
	
	@Before
	public static void initBeforeClass() 
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
	
	private boolean isLogged()
	{
		try
		{
		selenium.click("name=tools");
		return true;
		}
		catch (SeleniumException e)
		{
			return false;
		}		
	}
}
