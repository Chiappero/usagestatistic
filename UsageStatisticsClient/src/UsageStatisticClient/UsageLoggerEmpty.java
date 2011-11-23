package UsageStatisticClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * @class Empty Implementation of UsageLogger interface
 * 
 *
 */
public class UsageLoggerEmpty implements UsageLogger
{

	@Override
	public final void log(final String functionality, final String parameters)
	{}

	@Override
	public final int getLogsCount()
	{
		return 0;
	}

	@Override
	public final Date getOldestLogDate()
	{
		return Calendar.getInstance().getTime();
	}

	@Override
	public final List<LogInformation> getAllLogs()
	{
		return new ArrayList<LogInformation>();
	}

	@Override
	public Runnable createCommitRunnable(final CommitListener cl)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{}
		};
	}
}
