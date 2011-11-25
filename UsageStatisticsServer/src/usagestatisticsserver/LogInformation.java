package usagestatisticsserver;
import java.util.Date;





public final class LogInformation 
{
private Date dateTime;
private String functionality;
private String parameters;
private String user;
private String tool;

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

static boolean validateLog(LogInformation log)
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
