package UsageStatisticClient;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import UsageStatisticClientConfigGenerator.ConfigGenerator;

import junitx.util.PrivateAccessor;

public class TestUtils
{
public static void removeAllLogsFromDao(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	while (!dao.isEmpty())
	{
		dao.clearFirstLog();
	}
}


public static int getLogsAmmount(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	return dao.getLogsAmount();
}

public static void addSomeLogsToDao(UsageStatistic instance, int amountRecord)
{	
	for (int i=0;i<amountRecord;i++)
	{
	instance.used("funkcjonalnosc", "i="+i); 
	}
}

public static DaoTemporaryDatabaseH2 getLocalDao(UsageStatistic instance) throws NoSuchFieldException
{
	return (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
}



public static void corruptFile(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	dropTable(dao);
}

public static LogInformation getExampleLog()
{
	return new LogInformation(new GregorianCalendar().getTime(), "test", "test", "test", "test");
}

public static void dropTable(DaoTemporaryDatabaseH2 dao) throws NoSuchFieldException, SQLException
{
	String sql="DROP TABLE Log";
	Connection conn=(Connection) PrivateAccessor.getField(dao, "conn");
	conn.createStatement().execute(sql);
}

public static void makeConnectionNull(DaoTemporaryDatabaseH2 dao)
{
	dao.conn=null;
}

public static void createExampleConfigFile() throws IOException
{
	ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer","matuszek","password", null);
}

public static void createExampleConfigFileWithTool() throws IOException
{
	ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer","matuszek","password", "tool");
}

public static void createExampleConfigFileWithToolEmpty() throws IOException
{
	ConfigGenerator.createConfigFile("client-config.cfg", "http://localhost:8080/UsageStatisticsServer","matuszek","password", "");
}


}