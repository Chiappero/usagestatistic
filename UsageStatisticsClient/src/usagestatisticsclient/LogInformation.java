package usagestatisticsclient;

import java.util.Date;




/**
 * 
 * @class Contains information about single log
 *
 */
public final class LogInformation{

private Date dateTime; //data tworzona przez nas ma format Sun Oct 09 12:19:06 CEST 2011
			   //data odczytana przez lokalna baze danych ma format 2011-10-09 12:19:06.39
private String functionality;
private String parameters;
private String user;
private String tool;
/**
 * Returns user defined in log
 * @return user defined in log
 */
public String getUser() {
	return user;
}
/**
 * Assign the specified in parameter value to the field
 * @param user - unique user name
 */
public void setUser(final String user) {
	this.user = user;
}
/**
 * Returns tool defined in log
 * @return tool defined in log
 */
public String getTool() {
	return tool;
}
/**
 * Assign the specified in parameter value to the field
 * @param tool tool defined in log
 */
public void setTool(final String tool) {
	this.tool = tool;
}
/**
 * Returns functionality defined in log
 * @return functionality defined in log
 */
public String getFunctionality() {
	return functionality;
}
/**
 * Assign the specified in parameter value to the field
 * @param functionality functionality defined in log
 */
public void setFunctionality(final String functionality) {
	this.functionality = functionality;
}
/**
 * Returns date defined in log
 * @return date defined in log
 */
public Date getDateTime()
{
	return dateTime;
}
/**
 * Assign the specified in parameter value to the field
 * @param dateTime date defined in log
 */
public void setDateTime(Date dateTime)
{
	this.dateTime = dateTime;
}
/**
 * Returns parameters defined in log
 * @return parameters defined in log
 */
public String getParameters() {
	return parameters;
}
/**
 * Assign the specified in parameter value to the field
 * @param parameters parameters defined in log
 */
public void setParameters(final String parameters) {
	this.parameters = parameters;
}
/**
 * Default constructor
 */
LogInformation(){
	super();
}
/**
 * Constructor with parameters
 * @param date date defined in log
 * @param functionality functionality in log
 * @param user user in log
 * @param tool tool in log
 * @param parameters parameters in log
 */
LogInformation(Date date,String functionality,String user,String tool, String parameters)
{
	this.dateTime=date;
	this.functionality=functionality;
	this.user=user;
	this.tool=tool;
	this.parameters=parameters;
}
/**
 * Checks if fields aren't empty
 * @param log LogInformation object to check
 * @return true if log is correct, false in another case
 */
public static boolean validateLog(LogInformation log)
{
	Date date = log.getDateTime();
	String functionality = log.getFunctionality();
	String tool = log.getTool();
	String user = log.getUser();
	return !(date==null||functionality==null||functionality.equals("")||tool==null||tool.equals("")||user==null||user.equals(""));
}





}
