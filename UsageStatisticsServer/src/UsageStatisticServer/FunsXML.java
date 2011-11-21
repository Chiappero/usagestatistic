package UsageStatisticServer;

import java.util.ArrayList;

public class FunsXML {

	ArrayList<String> funs;
	
	public FunsXML(){
		
	}
	
	public FunsXML(final ArrayList<String> funs){
		this.funs=funs;
	}
	
	public String toXml(){
		String xml="<funs>";
		for(int i=0; i<funs.size(); i++){
			xml+="<fun>"; 
			xml+="<name>"+funs.get(i)+"</name>";
			xml+="</fun>";
		}
		xml+="</funs>";
		return xml;
	}
}
