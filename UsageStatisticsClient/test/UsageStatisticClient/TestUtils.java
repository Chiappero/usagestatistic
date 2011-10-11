package UsageStatisticClient;

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

public static void addSomeLogsToDao(UsageStatistic instance, int amountRecord)
{	
	for (int i=0;i<amountRecord;i++)
	{
	instance.used("funkcjonalnosc", "parametry"); 
	}
}
}
