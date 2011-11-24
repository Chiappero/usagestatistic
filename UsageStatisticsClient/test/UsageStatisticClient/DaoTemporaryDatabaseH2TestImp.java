package usagestatisticsclient;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import usagestatisticsclient.DaoTemporaryDatabaseInterface;
import usagestatisticsclient.LogInformation;

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
