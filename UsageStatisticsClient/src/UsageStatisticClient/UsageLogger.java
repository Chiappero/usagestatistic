package UsageStatisticClient;

import java.util.Date;

public interface UsageLogger 
{
	public UsageLogger getInstance();
	public void log(String functionality, String parameters);
	public void commit();
	public void setCommitListener(CommitListener cl);
	public int getLogsCount();
	public Date getOldestLogDate();
}
