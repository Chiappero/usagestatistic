package UsageStatisticClient;

import java.sql.SQLException;

public class DaoTemporaryDatabaseH2TestImp2 implements DaoTemporaryDatabaseInterface
{	
	@Override
	public boolean saveLog(LogInformation log)
	{
		return false;
	}
	@Override
	public void openDatabase()
	{
	}
	
	@Override
	public boolean isEmpty() throws SQLException
	{
		return false;
	}
	
	@Override
	public int getLogsAmount() throws SQLException
	{
		return 1;
	}
	
	@Override
	public LogInformation getFirstLog() throws SQLException
	{
		return null;
	}
	
	@Override
	public void closeDatabase()
	{
	}
	
	@Override
	public void clearFirstLog() throws SQLException
	{
	}
	@Override
	public void resetDatabase()
	{
	}
}
