package UsageStatisticServer;

import java.util.ArrayList;

public class UsersXML {

	ArrayList<String> users;
	
	public UsersXML(){
		
	}
	
	public UsersXML(ArrayList<String> funs){
		this.users=funs;
	}
	
	public String toXml(){
		String xml="<users>";
		for(int i=0; i<users.size(); i++){
			xml+="<user>"; 
			xml+="<name>"+users.get(i)+"</name>";
			xml+="</user>";
		}
		xml+="</users>";
		return xml;
	}
}
