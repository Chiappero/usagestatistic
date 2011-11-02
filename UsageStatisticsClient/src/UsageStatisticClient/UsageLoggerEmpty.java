package UsageStatisticClient;

import java.util.Date;

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

}
