package UsageStatisticClient;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @class Required for proper operation of sending logs to the server
 *
 */
@XmlRootElement
public class PairLogInformationAndPassword
{
private LogInformation logInformation;
private String password;
/**
 * returns informations abaout log to sent
 * @return Object LogInformation
 */
public final LogInformation getLogInformation()
{
	return logInformation;
}
/**
 * Assign the specified in parameter value to the field
 * @param logInformation - Object containing information about log to sent
 */
public final void setLogInformation(LogInformation logInformation)
{
	this.logInformation = logInformation;
}
/**
 * Assign the specified in parameter value to the field
 * @param password password required to connect
 */
public final void setPassword(String password)
{
	this.password = password;
}
/**
 * 
 * @return  password defined in object
 */
public final String getPassword()
{
	return password;
}
/**
 * Constructor
 * @param logInformation - Object containing information about log to sent
 * @param password required to connect
 */
public PairLogInformationAndPassword(LogInformation logInformation,
		String password)
{
	super();
	this.logInformation = logInformation;
	this.password = password;
}
/**
 * Method required for proper commit logs
 */
public PairLogInformationAndPassword() //musi byc aby commitowanie zadzialalo!!
{
}








}
