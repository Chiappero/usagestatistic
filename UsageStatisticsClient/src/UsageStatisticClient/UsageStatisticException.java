package UsageStatisticClient;

public final class UsageStatisticException extends Exception
{
	static final String CANNOT_GET_INSTANCE="Cannot get instance";
	static final String CANNOT_READ_CONFIGURATION_FILE="Cannot read configuration file";
	static final String INVALID_SERVER_URI="Invalid server URI";
	static final String INVALID_CONFIGURATION="Invalid configuration";
public UsageStatisticException(String error)
{
	super(error);
}
}
