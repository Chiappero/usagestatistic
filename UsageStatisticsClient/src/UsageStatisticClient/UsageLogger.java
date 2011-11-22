package UsageStatisticClient;

import java.util.Date;
import java.util.List;

public interface UsageLogger 
{
	/**
	 * Method writes the log to the local database
	 * @param functionality name of used functionality
	 * @param parameters parameters of used functionality
	 */
	 void log(String functionality, String parameters);
	 /**
	  * Method returns thread to commit logs into server database. To implement commit progress parameter cl mustn't be null
	  * @param cl Object responsible for the implementation of the transfer progress
	  * @return Thread to commit logs into server database
	  */
	 Runnable createCommitRunnable(final CommitListener cl);
	 /**
	  * 
	  * @return Amount of logs in local database
	  */
	 int getLogsCount();
	 /**
	  * Method returns date of first log in local database if it's exist, null in anther case.
	  * @return Date of first log in local database
	  */
	 Date getOldestLogDate();
	 /**
	  * 
	  * @return List of logs presented as objects of class LogInformation
	  */
	 List<LogInformation> getAllLogs();
}
