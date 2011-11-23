package UsageStatisticClient;

/**
 * 
 * The interface defines the methods needed to implement the process of sending logs to submit progress
 *
 */
public interface CommitListener 
{
/**
 * Defines what should happen when you start sending logs
 */
void commitingStart();
/**
 * Defines what should happen after the successful completion of sending logs
 */
void commitingFinishedSuccesful();
/**
 * Defines what should happen when it fails to send the logs
 * @param error message with information about error
 */
void commitingFailureWithError(final String error);
/**
 * Defines what should happen after send a single log
 */
void step();
/**
 * Defines what should happen after failure to send a single log
 * @param reason message with information about error
 */
void stepInvalid(final String reason);
/**
 * Sets the amount of logs to be sent
 * @param amount amount of logs to sent
 */
void setLogsAmount(final int amount);
}
