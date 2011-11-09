package UsageStatisticServer;

import java.util.ArrayList;

public class FunsXML {

	ArrayList<String> funs;
	
	public FunsXML(ArrayList<String> funs){
		this.funs=funs;
	}
	
	public String toXml(){
		String xml="<?xml version=\"1.0\"?>";
		xml+="<funs>";
		for(int i=0; i<funs.size(); i++){
			xml+="<fun name="+funs.get(i)+">";
			xml+="</fun>";
		}
		xml+="</funs>";
		return xml;
	}
}
