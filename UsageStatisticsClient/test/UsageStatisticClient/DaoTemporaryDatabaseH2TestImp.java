package UsageStatisticClient;

import java.sql.SQLException;

public class DaoTemporaryDatabaseH2TestImp implements DaoTemporaryDatabaseInterface
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
		return 10;
	}
	
	@Override
	public LogInformation getFirstLog() throws SQLException
	{
		return TestUtils.getExampleLog();
	}
	
	@Override
	public void closeDatabase()
	{
		
	}
	
	@Override
	public void clearFirstLog() throws SQLException
	{
		throw new SQLException();
	}
	@Override
	public void resetDatabase()
	{
		
	}
}
