package UsageStatisticServer;

import java.util.ArrayList;
import java.util.Date;



public class Results
{
private String tool;
private String[] functionalities;
private String[] users;
private String dateFrom;
private String dateTill;
private boolean param;

public boolean isParam() {
	return param;
}
public void setParam(boolean param) {
	this.param = param;
}
public String getDateTill() {
	return dateTill;
}
public void setDateTill(String dateTill) {
	this.dateTill = dateTill;
}
public String getDateFrom() {
	return dateFrom;
}
public void setDateFrom(String dateFrom) {
	this.dateFrom = dateFrom;
}
public String getTool()
{
	return tool;
}
public void setTool(String tool)
{
	this.tool = tool;
}
public String[] getFunctionalities()
{
	return functionalities;
}
public void setFunctionalities(String[] functionalities)
{
	this.functionalities = functionalities;
}
public String[] getUsers()
{
	return users;
}
public void setUsers(String[] users)
{
	this.users = users;
}





;
}
