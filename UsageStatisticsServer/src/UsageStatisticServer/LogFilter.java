package UsageStatisticServer;

import java.util.ArrayList;

class LogFilter 
{
	java.util.Date datefrom;
	java.util.Date datebefore;
	ArrayList<String> functionality;
	ArrayList<String> user;
	ArrayList<String> tool;
	
	public LogFilter(final java.util.Date datefrom,final java.util.Date datebefore,final ArrayList<String> functionality,final ArrayList<String> user,final ArrayList<String> tool)
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
	public void setDatefrom(final java.util.Date datefrom) {
		this.datefrom = datefrom;
	}
	public java.util.Date getDatebefore() {
		return datebefore;
	}
	public void setDatebefore(final java.util.Date datebefore) {
		this.datebefore = datebefore;
	}
	public ArrayList<String> getFunctionality() {
		return functionality;
	}
	public void setFunctionality(final ArrayList<String> functionality) {
		this.functionality = functionality;
	}
	public ArrayList<String> getUser() {
		return user;
	}
	public void setUser(final ArrayList<String> user) {
		this.user = user;
	}
	public ArrayList<String> getTool() {
		return tool;
	}
	public void setTool(final ArrayList<String> tool) {
		this.tool = tool;
	}
	
	
	
	
}
