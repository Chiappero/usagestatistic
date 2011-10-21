package UsageStatisticServer;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;


public class LogReceiverTest {

	
	
	public static LogInformation getExampleLog()
	{
		return new LogInformation(new GregorianCalendar().getTime(), "test", "test", "test", "test");
	}
	

	
	@Test
	public void AT23_Server_database_offline_or_error_on_save() throws NoSuchFieldException, SQLException 
	{
		ApplicationContext ctx = new FileSystemXmlApplicationContext("WebContent/WEB-INF/dispatcher-servlet.xml"); 
        DaoServerDatabaseH2 dao = (DaoServerDatabaseH2) ctx.getBean("DaoServerDatabaseH2"); 
		LogInformation log=getExampleLog();
		dao.closeDatabase();
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

}
