package connection;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@Entity
public
final class LogInformation implements Serializable {
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
