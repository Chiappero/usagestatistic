package UsageStatisticServer;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;




@XmlRootElement
@Entity
public
final class LogInformation {
Date dateTime;
String functionality;
String parameters;
String user;
String tool;
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getTool() {
	return tool;
}
public void setTool(String tool) {
	this.tool = tool;
}
public Date getDateTime() {
	return dateTime;
}
public void setDateTime(Date dateTime) {
	this.dateTime = dateTime;
}
public String getFunctionality() {
	return functionality;
}
public void setFunctionality(String functionality) {
	this.functionality = functionality;
}
public String getParameters() {
	return parameters;
}
public void setParameters(String parameters) {
	this.parameters = parameters;
}
@Override
public String toString() {
	return "LogInformation [dateTime=" + dateTime + ", functionality="
			+ functionality + ", parameters=" + parameters + ", user=" + user
			+ ", tool=" + tool + "]";
}

public static boolean validateLog(LogInformation log)
{
	Date date = log.getDateTime();
	String functionality = log.getFunctionality();
	String tool = log.getTool();
	String user = log.getUser();
	return !(date==null||functionality==null||functionality.equals("")||tool==null||tool.equals("")||user==null||user.equals(""));
}

LogInformation(Date date,String functionality,String user,String tool, String parameters)
{
	this.dateTime=date;
	this.functionality=functionality;
	this.user=user;
	this.tool=tool;
	this.parameters=parameters;
}

LogInformation()
{}


}
