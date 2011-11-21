package UsageStatisticServer;

import java.util.ArrayList;
import java.util.Date;



class Results
{
private String tool;
private String[] functionalities;
private String[] users;
private String dateFrom;
private String dateTill;
private boolean param;

boolean isParam() {
	return param;
}
/*private void setParam(boolean param) {
	this.param = param;
}*/
String getDateTill() {
	return dateTill;
}
/*private void setDateTill(String dateTill) {
	this.dateTill = dateTill;
}*/
String getDateFrom() {
	return dateFrom;
}
/*private void setDateFrom(String dateFrom) {
	this.dateFrom = dateFrom;
}*/
String getTool()
{
	return tool;
}
/*private void setTool(String tool)
{
	this.tool = tool;
}*/
String[] getFunctionalities()
{
	return functionalities;
}
/*private void setFunctionalities(String[] functionalities)
{
	this.functionalities = functionalities;
}*/
String[] getUsers()
{
	return users;
}
/*private void setUsers(String[] users)
{
	this.users = users;
}*/





;
}
