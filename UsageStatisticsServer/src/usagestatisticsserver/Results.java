package usagestatisticsserver;



public class Results
{
private String tool;
private String[] functionalities;
private String[] users;
private String dateFrom;
private String dateTill;
private boolean param;

public final boolean isParam() {
	return param;
}
public final void setParam(boolean param) {
	this.param = param;
}
public final String getDateTill() {
	return dateTill;
}
public final void setDateTill(String dateTill) {
	this.dateTill = dateTill;
}
public final String getDateFrom() {
	return dateFrom;
}
public final void setDateFrom(String dateFrom) {
	this.dateFrom = dateFrom;
}
public final String getTool()
{
	return tool;
}
public final void setTool(String tool)
{
	this.tool = tool;
}
public final String[] getFunctionalities()
{
	return functionalities;
}
public final void setFunctionalities(String[] functionalities)
{
	this.functionalities = functionalities;
}
public final String[] getUsers()
{
	return users;
}
public final void setUsers(String[] users)
{
	this.users = users;
}





;
}
