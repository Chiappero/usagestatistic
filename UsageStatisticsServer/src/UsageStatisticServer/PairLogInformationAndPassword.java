package UsageStatisticServer;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PairLogInformationAndPassword
{
LogInformation logInformation;
String password;
public LogInformation getLogInformation()
{
	return logInformation;
}
public void setLogInformation(LogInformation logInformation)
{
	this.logInformation = logInformation;
}
public String getPassword()
{
	return password;
}
public void setPassword(String password)
{
	this.password = password;
}
public PairLogInformationAndPassword(LogInformation logInformation,
		String password)
{
	super();
	this.logInformation = logInformation;
	this.password = password;
}
public PairLogInformationAndPassword()
{
}






}
