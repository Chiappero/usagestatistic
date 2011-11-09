package UsageStatisticClient;

import java.util.Date;
import java.util.List;

public class UsageLoggerEmpty implements UsageLogger{

	@Override
	public void log(String functionality, String parameters) {}

	@Override
	public void commit() {}

	@Override
	public void setCommitListener(CommitListener cl) {}

	@Override
	public int getLogsCount() {
		return 0;
	}

	@Override
	public Date getOldestLogDate() {
		return null;
	}

	@Override
	public List<LogInformation> getAllLogs()
	{
		return null;
	}

}
