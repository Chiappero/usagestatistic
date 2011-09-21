package UsageStatisticClient;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement

final class LogInformation implements Serializable { //TODO problem jezeli dodajesz/usuwasz/zmienasz pola przy deserializacji
/**
	 * 
	 */
	private static final long serialVersionUID = -5844130058939487072L;
Date dateTime;
String functionality;
String parameters;
String user;
String tool;
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getTool() {
	return tool;
}
public void setTool(String tool) {
	this.tool = tool;
}
public Date getDate() {
	return dateTime;
}
public void setDate(Date dateTime) {
	this.dateTime = dateTime;
}
public String getFunctionality() {
	return functionality;
}
public void setFunctionality(String functionality) {
	this.functionality = functionality;
}
public String getParameters() {
	return parameters;
}
public void setParameters(String parameters) {
	this.parameters = parameters;
}
@Override
public String toString() {
	return "LogInformation [dateTime=" + dateTime + ", functionality="
			+ functionality + ", parameters=" + parameters + ", user=" + user
			+ ", tool=" + tool + "]";
}







}
