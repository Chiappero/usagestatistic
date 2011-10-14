package UsageStatisticClient;

public final class UsageStatisticException extends Exception
{
	static final String CANNOT_GET_INSTANCE="Cannot get instance";
	static final String CANNOT_READ_CONFIGURATION_FILE="Cannot read configuration file";
	static final String INVALID_SERVER_URI="Invalid server URI";
public UsageStatisticException(String error)
{
	super(error);
}
}
