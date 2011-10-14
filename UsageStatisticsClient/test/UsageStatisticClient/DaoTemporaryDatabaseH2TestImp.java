package UsageStatisticClient;

import java.sql.SQLException;

public class DaoTemporaryDatabaseH2TestImp implements DaoTemporaryDatabaseInterface
{	
	@Override
	public boolean saveLog(LogInformation log)
	{
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void openDatabase()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isEmpty() throws SQLException
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clearFirstLog() throws SQLException
	{
		throw new SQLException();
	}
	@Override
	public void resetDatabase()
	{
		// TODO Auto-generated method stub
		
	}
}
