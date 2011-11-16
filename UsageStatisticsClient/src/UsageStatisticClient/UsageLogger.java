package UsageStatisticClient;

import java.util.Date;
import java.util.List;

public interface UsageLogger 
{
	 void log(String functionality, String parameters);
	 Runnable createCommitRunnable(final CommitListener cl);
	 int getLogsCount();
	 Date getOldestLogDate();
	 List<LogInformation> getAllLogs();
}
