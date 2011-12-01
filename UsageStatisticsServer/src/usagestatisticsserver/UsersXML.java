package usagestatisticsserver;

import java.util.ArrayList;

class UsersXML {

	private ArrayList<String> users;
	
	
	UsersXML(ArrayList<String> funs){
		this.users=funs;
	}
	
	String toXml(){
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
