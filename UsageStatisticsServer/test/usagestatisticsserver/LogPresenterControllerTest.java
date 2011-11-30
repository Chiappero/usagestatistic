package usagestatisticsserver;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:WebContent/WEB-INF/dispatcher-servlet.xml"})
public class LogPresenterControllerTest
{
	
	String TOOLS;
	String FUNCTIONALITIES;
	String USERS;
	String COMMAND;
	String DATA_LOGS_GET;
	String DATA_LOGS_POST;
	String SHOW_PARAMS;
	String DATE_FROM;
	String DATE_TILL;
	String TOOL_POST;
	String LOGS_POST;
	
	
	@Autowired
	LogPresenterController logPresenterController;
	
	
	@Before
	public void setUp() throws Exception
	{
		TOOLS = 	(String) PrivateAccessor.getField(LogPresenterController.class, "TOOLS");
		FUNCTIONALITIES = 	(String) PrivateAccessor.getField(LogPresenterController.class, "FUNCTIONALITIES");
		USERS = 	(String) PrivateAccessor.getField(LogPresenterController.class, "USERS");
		COMMAND = 	(String) PrivateAccessor.getField(LogPresenterController.class, "COMMAND");
		DATA_LOGS_GET = 	(String) PrivateAccessor.getField(LogPresenterController.class, "DATA_LOGS_GET");
		DATA_LOGS_POST = 	(String) PrivateAccessor.getField(LogPresenterController.class, "DATA_LOGS_POST");
		SHOW_PARAMS = 	(String) PrivateAccessor.getField(LogPresenterController.class, "SHOW_PARAMS");
		DATE_FROM = 	(String) PrivateAccessor.getField(LogPresenterController.class, "DATE_FROM");
		DATE_TILL = 	(String) PrivateAccessor.getField(LogPresenterController.class, "DATE_TILL");
		TOOL_POST = 	(String) PrivateAccessor.getField(LogPresenterController.class, "TOOL_POST");
		LOGS_POST = 	(String) PrivateAccessor.getField(LogPresenterController.class, "LOGS_POST");
		
	}

	@Test
	public void testWyswietlFormatke() throws SQLException
	{
		Map<String, Object> model = logPresenterController.wyswietlFormatke(null, null).getModel();
		Assert.assertTrue((model.get(TOOLS) instanceof ArrayList));
		Assert.assertTrue((model.get(FUNCTIONALITIES) instanceof ArrayList));
		Assert.assertTrue((model.get(USERS) instanceof ArrayList));
		Assert.assertTrue((model.get(COMMAND) instanceof Results));
		Assert.assertTrue((model.get(DATA_LOGS_GET) instanceof String));
	}

	@Test
	public void testGetFuns() throws SQLException, UnsupportedEncodingException, NoSuchFieldException
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("tool", "narzedzie");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		logPresenterController.getFuns(request, response); //jezeli bedzie blad pobrania to test nie przejdzie przez SQLException, niepotrzebny Assert
		
		DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) PrivateAccessor.getField(logPresenterController, "dao");
		LogInformation log = new LogInformation(Calendar.getInstance().getTime(),"funkcjonalnoscPewna", "userPewny", "toolPewny", "parametr1Pewny parametr2 Pewny");
		dao.saveLog(log);
		request.setParameter("tool", "toolPewny");
		logPresenterController.getFuns(request, response);
		
	}

	@Test
	public void testGetUsers() throws SQLException, NoSuchFieldException
	{

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("tool", "tool");
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		logPresenterController.getUsers(request, response); //jezeli bedzie blad pobrania to test nie przejdzie przez SQLException, niepotrzebny Assert
		
		DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) PrivateAccessor.getField(logPresenterController, "dao");
		LogInformation log = new LogInformation(Calendar.getInstance().getTime(),"funkcjonalnoscPewna", "userPewny", "toolPewny", "parametr1Pewny parametr2 Pewny");
		dao.saveLog(log);
		request.setParameter("tool", "toolPewny");
		logPresenterController.getUsers(request, response);
	}

	@Test
	public void testWyswietlWyniki()
	{
		Results results = new Results();
		String[] functionalities = {"test","test2","test3"};
		String[] users = {"test","test2","test3"};
		results.setFunctionalities(functionalities);
		results.setParam(true);
		results.setDateFrom("2011-01-01");
		results.setDateTill("2011-01-10");
		results.setTool("narzedzie");
		results.setUsers(users);
		
		Map<String, Object> model = logPresenterController.wyswietlWyniki(results).getModel();
		Assert.assertEquals(((Boolean)model.get(SHOW_PARAMS)),(Boolean)results.isParam());
		Assert.assertTrue((model.get(DATA_LOGS_POST) instanceof String));
		Assert.assertEquals(((String)model.get(DATE_FROM)),results.getDateFrom());
		Assert.assertEquals(((String)model.get(DATE_TILL)),results.getDateTill());
		Assert.assertEquals(((String)model.get(TOOL_POST)),results.getTool());
		Assert.assertTrue((model.get(LOGS_POST) instanceof ArrayList));
	}

}
