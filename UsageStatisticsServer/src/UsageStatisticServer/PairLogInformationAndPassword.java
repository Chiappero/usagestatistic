package usagestatisticsserver;

import javax.xml.bind.annotation.XmlRootElement;




@XmlRootElement
public class PairLogInformationAndPassword
{
private LogInformation logInformation;
private String password;
public final LogInformation getLogInformation()
{
	return logInformation;
}
public final void setLogInformation(LogInformation logInformation)
{
	this.logInformation = logInformation;
}
public final void setPassword(String password)
{
	this.password = password;
}
public final String getPassword()
{
	return password;
}
public PairLogInformationAndPassword(LogInformation logInformation,
		String password)
{
	super();
	this.logInformation = logInformation;
	this.password = password;
}
public PairLogInformationAndPassword() //musi byc aby commitowanie zadzialalo!!
{
}






}
