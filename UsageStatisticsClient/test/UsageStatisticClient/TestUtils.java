package UsageStatisticClient;

import java.sql.Connection;
import java.sql.SQLException;

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
	instance.used("funkcjonalnosc", "parametry"); 
	}
}

public static DaoTemporaryDatabaseH2 getLocalDao(UsageStatistic instance) throws NoSuchFieldException
{
	return (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
}



public static void corruptFile(UsageStatistic instance) throws NoSuchFieldException, SQLException
{
	DaoTemporaryDatabaseH2 dao = (DaoTemporaryDatabaseH2) PrivateAccessor.getField(instance, "dao");
	String sql="DROP TABLE Log";
	Connection conn=(Connection) PrivateAccessor.getField(dao, "conn");
	conn.createStatement().execute(sql);
	
}

}