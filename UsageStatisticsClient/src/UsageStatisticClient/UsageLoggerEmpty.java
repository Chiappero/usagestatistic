package UsageStatisticClient;

import java.util.Date;
import java.util.List;

public class UsageLoggerEmpty implements UsageLogger{

	@Override
	public final void log(final String functionality,final String parameters) {}

	
	@Override
	public final int getLogsCount() {
		return 0;
	}

	@Override
	public final Date getOldestLogDate() {
		return null;
	}

	@Override
	public final List<LogInformation> getAllLogs()
	{
		return null;
	}


	@Override
	public Runnable createCommitRunnable(final CommitListener cl)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
			}
		};
	}
}
