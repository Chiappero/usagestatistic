package UsageStatisticClient;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
	@Override
	public Date getOldestLogDate()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<LogInformation> getAllLogs()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
