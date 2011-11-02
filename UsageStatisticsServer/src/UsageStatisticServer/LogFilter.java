package UsageStatisticServer;

import java.util.ArrayList;

class LogFilter 
{
	java.util.Date datefrom;
	java.util.Date datebefore;
	ArrayList<String> functionality;
	ArrayList<String> user;
	ArrayList<String> tool;
	
	public LogFilter(	java.util.Date datefrom,java.util.Date datebefore,ArrayList<String> functionality,ArrayList<String> user,ArrayList<String> tool)
	{
		this.datefrom=datefrom;
		this.datebefore=datebefore;
		this.functionality=functionality;
		this.user=user;
		this.tool=tool;
	}
	
	
	public java.util.Date getDatefrom() {
		return datefrom;
	}
	public void setDatefrom(java.util.Date datefrom) {
		this.datefrom = datefrom;
	}
	public java.util.Date getDatebefore() {
		return datebefore;
	}
	public void setDatebefore(java.util.Date datebefore) {
		this.datebefore = datebefore;
	}
	public ArrayList<String> getFunctionality() {
		return functionality;
	}
	public void setFunctionality(ArrayList<String> functionality) {
		this.functionality = functionality;
	}
	public ArrayList<String> getUser() {
		return user;
	}
	public void setUser(ArrayList<String> user) {
		this.user = user;
	}
	public ArrayList<String> getTool() {
		return tool;
	}
	public void setTool(ArrayList<String> tool) {
		this.tool = tool;
	}
	
	
	
	
}
