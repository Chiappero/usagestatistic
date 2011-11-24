package usagestatisticsserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import usagestatisticsserver.DaoServerDatabaseH2;
import usagestatisticsserver.LogInformation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:WebContent/WEB-INF/dispatcher-servlet.xml"})
public class LogReceiverTest {

	String MESSAGE;
	String ERROR;
	String OK;
	String CANNOT_AUTHENTICATE;
	
	@Before
	public void setUp() throws Exception
	{
		MESSAGE = 	(String) PrivateAccessor.getField(LogReceiver.class, "MESSAGE");
		ERROR = 	(String) PrivateAccessor.getField(LogReceiver.class, "ERROR");
		OK = 	(String) PrivateAccessor.getField(LogReceiver.class, "OK");
		CANNOT_AUTHENTICATE = 	(String) PrivateAccessor.getField(LogReceiver.class, "CANNOT_AUTHENTICATE");
	}
	
	@Autowired
	LogReceiver logReceiver;
	
	public static LogInformation getExampleLog()
	{
		return new LogInformation(new GregorianCalendar().getTime(), "test", "test", "test", "test");
	}
	
	@Test
	public void addPersonTest() throws NoSuchFieldException
	{
		PairLogInformationAndPassword pair = null;
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR));
		pair = new PairLogInformationAndPassword();
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR));
		LogInformation log = new LogInformation();
		pair.setLogInformation(log);
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR));
		pair.setPassword("test");
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR));
		pair.setPassword(null);
		pair.getLogInformation().setUser("specified");
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR));
		pair.setPassword("specified");
		DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) PrivateAccessor.getField(logReceiver, "dao");
		dao.addUserClient("specified", "specified");
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(ERROR)); //validacja loga
		
		log = getExampleLog();
		log.setUser("specified");
		pair.setLogInformation(log);
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(OK));
		
		pair.setPassword("dont exists");
		pair.getLogInformation().setUser("dont exists");
		Assert.assertTrue(logReceiver.addPerson(pair).getModel().get(MESSAGE).equals(CANNOT_AUTHENTICATE));
	}
	
	
	@Test
	public void AT23_Server_database_offline_or_error_on_save() throws NoSuchFieldException, SQLException 
	{
		DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) PrivateAccessor.getField(logReceiver, "dao");
		LogInformation log=getExampleLog();
		closeDatabase();
		Assert.assertTrue(dao.saveLog(log));
		log.setFunctionality(null);
		Assert.assertFalse(dao.saveLog(log));
		log=null;
		Assert.assertFalse(dao.saveLog(log));
		Connection conn=(Connection) PrivateAccessor.getField(dao, "conn");
		String sql="DROP TABLE Log";
		conn.createStatement().execute(sql);
		log=getExampleLog();
		Assert.assertTrue(dao.saveLog(log));
		PrivateAccessor.setField(dao, "conn", null);
		Assert.assertTrue(dao.saveLog(log));
		

	}

	public void closeDatabase() throws NoSuchFieldException
	{
		DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) PrivateAccessor.getField(logReceiver, "dao");
		Connection conn=(Connection)PrivateAccessor.getField(dao, "conn");

			try {
				if (conn!=null)
				{
					conn.close();
				}
			} catch (SQLException e) {
				try {
					conn.close();
				} catch (SQLException e1) {
				}
			}

	}



}
