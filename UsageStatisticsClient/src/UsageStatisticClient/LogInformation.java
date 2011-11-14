package UsageStatisticClient;

import java.util.Date;





public final class LogInformation{
/**
	 * 
	 */
	private static final long serialVersionUID = -5844130058939487072L;
private Date dateTime; //data tworzona przez nas ma format Sun Oct 09 12:19:06 CEST 2011
			   //data odczytana przez lokalna baze danych ma format 2011-10-09 12:19:06.39
private String functionality;
private String parameters;
private String user;
private String tool;
public String getUser() {
	return user;
}
public void setUser(final String user) {
	this.user = user;
}
public String getTool() {
	return tool;
}
public void setTool(final String tool) {
	this.tool = tool;
}
public String getFunctionality() {
	return functionality;
}

public void setFunctionality(final String functionality) {
	this.functionality = functionality;
}

public Date getDateTime()
{
	return dateTime;
}
public void setDateTime(Date dateTime)
{
	this.dateTime = dateTime;
}

public String getParameters() {
	return parameters;
}
public void setParameters(final String parameters) {
	this.parameters = parameters;
}
@Override
public String toString() {
	return "LogInformation [dateTime=" + dateTime + ", functionality="
			+ functionality + ", parameters=" + parameters + ", user=" + user
			+ ", tool=" + tool + "]";
}

LogInformation()
{}


LogInformation(Date date,String functionality,String user,String tool, String parameters)
{
	this.dateTime=date;
	this.functionality=functionality;
	this.user=user;
	this.tool=tool;
	this.parameters=parameters;
}

static boolean validateLog(LogInformation log)
{
	Date date = log.getDateTime();
	String functionality = log.getFunctionality();
	String tool = log.getTool();
	String user = log.getUser();
	return !(date==null||functionality==null||functionality.equals("")||tool==null||tool.equals("")||user==null||user.equals(""));
}





}
