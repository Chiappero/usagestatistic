package UsageStatisticClient;

public final class UsageStatisticException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String CANNOT_GET_INSTANCE="Cannot get instance";
	static final String CANNOT_READ_CONFIGURATION_FILE="Cannot read configuration file";
	static final String INVALID_SERVER_URL="Invalid server URI";
	static final String INVALID_CONFIGURATION_USERNAME="Invalid configuration: user not specified";
	static final String INVALID_CONFIGURATION_PASSWORD="Invalid configuration: password not specified";
	static final String INVALID_CONFIGURATION_TOOL="Invalid configuration: tool not specified";
	static final String CONFIG_ERROR="Invalid configuration: File corrupted";
	static final String CIPHER_ERROR="Cannot decode configutation file";
public UsageStatisticException(String error)
{
	super(error);
}
}
