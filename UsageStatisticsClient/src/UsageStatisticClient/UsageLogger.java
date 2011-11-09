package UsageStatisticClient;

import java.util.Date;
import java.util.List;

public interface UsageLogger 
{
	public void log(String functionality, String parameters);
	public void commit();
	public void setCommitListener(CommitListener cl);
	public int getLogsCount();
	public Date getOldestLogDate();
	public List<LogInformation> getAllLogs();
}
