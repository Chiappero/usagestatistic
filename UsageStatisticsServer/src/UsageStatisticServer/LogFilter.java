package UsageStatisticServer;

import java.util.ArrayList;

class LogFilter 
{
	java.util.Date datefrom;
	java.util.Date datebefore;
	ArrayList<String> functionality;
	ArrayList<String> user;
	ArrayList<String> tool;
	
	LogFilter(final java.util.Date datefrom,final java.util.Date datebefore,final ArrayList<String> functionality,final ArrayList<String> user,final ArrayList<String> tool)
	{
		this.datefrom=datefrom;
		this.datebefore=datebefore;
		this.functionality=functionality;
		this.user=user;
		this.tool=tool;
	}
	
	
	java.util.Date getDatefrom() {
		return datefrom;
	}
	void setDatefrom(final java.util.Date datefrom) {
		this.datefrom = datefrom;
	}
	java.util.Date getDatebefore() {
		return datebefore;
	}
	void setDatebefore(final java.util.Date datebefore) {
		this.datebefore = datebefore;
	}
	ArrayList<String> getFunctionality() {
		return functionality;
	}
	void setFunctionality(final ArrayList<String> functionality) {
		this.functionality = functionality;
	}
	ArrayList<String> getUser() {
		return user;
	}
	void setUser(final ArrayList<String> user) {
		this.user = user;
	}
	ArrayList<String> getTool() {
		return tool;
	}
	void setTool(final ArrayList<String> tool) {
		this.tool = tool;
	}
	
	
	
	
}
