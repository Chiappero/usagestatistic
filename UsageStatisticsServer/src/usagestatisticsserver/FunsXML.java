package usagestatisticsserver;

import java.util.ArrayList;

class FunsXML {

	private ArrayList<String> funs;
	
	
	FunsXML(final ArrayList<String> funs){
		this.funs=funs;
	}
	
	String toXml(){
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
