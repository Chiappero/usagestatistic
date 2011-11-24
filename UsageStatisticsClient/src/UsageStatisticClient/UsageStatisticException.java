package usagestatisticsclient;

final class UsageStatisticException extends Exception
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
	
	static final String SERVER_DOESNT_RECEIVE_DATA="Error with server - server does not receive data";
	static final String SERVER_TURNED_OFF="Error with server - server turned off";
	static final String ERROR_WITH_CONNECTION_TO_SERVER="Error with connection to server";
	static final String CANNOT_EXTRACT_RESPONSE="Error with server - server does not respond";
	static final String CANNOT_SAVE_LOG="Cannot save log on database server";
	static final String LOG_WAS_NULL="Log was null";
	static final String ERROR_WITH_CONNECTION_TO_LOCAL_DATABASE="Error with connection to local database";
	static final String FATAL_EXCEPTION="Fatal Exception";
	static final String CANNOT_AUTHENTICATE="Cannot authenticate";
	
public UsageStatisticException(String error)
{
	super(error);
}
}
