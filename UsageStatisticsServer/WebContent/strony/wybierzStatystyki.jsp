<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<LINK rel="stylesheet" href="style.css">
<div>
<form:form method="post" action="results">
      <table style="border:2px solid grey; ">
          <tr><td>Stan z dnia: <c:out value="${data}"></c:out></td></tr>
          <tr><td>Narzedzia</td><td>       	  
          <form:select path="tool" onchange="getFunsAndUsersWithAjax();" id="tool" name="tool" style="width: 150px;">
		   	<option value="null">---Select---</option>
		   	<c:forEach var="ttt" items="${tools}">
	 		  <option value="${ttt}">${ttt}</option>
	 		</c:forEach>
		  </form:select> 
		  </td></tr>
          <tr><td>Funkcjonalnosci<br />
          <input type="button" onclick="selectAllFuns()" value="Zaznacz/odznacz wszystkie"/></td><td>
          <form:select path="functionalities" id="functionalities" name="functionalities" style="width: 150px; height: 200px;">
	 	 </form:select>
		  </td></tr>
		  <tr><td>Userzy<br/>
		  <input type="button" onclick="selectAllUsers()" value="Zaznacz/odznacz wszystkich"/></td><td>
		  <form:select path="users" id="users" name="users" style="width: 150px; height: 200px;"></form:select>
		  </td></tr> 
      <tr><td>Data od:</td><td><form:input path="dateFrom" id="dateFrom" type="date" /></td></tr>
      <tr><td>Data do:</td><td><form:input path="dateTill" id="dateTill" type="date" /></td></tr>
      <tr><td>Params:</td><td><form:checkbox path="param" /> show params:</td></tr> 
      </table>
        <input type="submit" value="Pokaz logi"/>
  </form:form>
</div>

<script language="javascript"><!--
	var drop1 = document.getElementById("tool");
	var drop2 = document.getElementById("functionalities");		
	var drop3 = document.getElementById("users");
	
	drop1.disabled = false;
	drop2.disabled = true;
	drop3.disabled = true;
	
	getFunsAndUsersWithAjax();
	getUsersWithAjax();
	
	function getFunsAndUsersWithAjax(){
		getFunsWithAjax();
		getUsersWithAjax();
	}
	
	function getUsersWithAjax()
	{
		var xmlhttp;
		if (window.XMLHttpRequest)
		  {// code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		  }
		else
		  {// code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		  }
		xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
                if (xmlhttp.status == 200) {
                	updateUsersDropdown(xmlhttp.responseXML);
                }
            }
        };
		var toolName = document.getElementById("tool").value;
		xmlhttp.open("GET","getusers?tool="+toolName,true);
		emptyUsers();
		xmlhttp.send(null);
		drop3.disabled=true;
	}
	
	function getFunsWithAjax()
	{
		var xmlhttp;
		if (window.XMLHttpRequest)
		  {// code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		  }
		else
		  {// code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		  }
		xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
                if (xmlhttp.status == 200) {
                	updateFunsDropdown(xmlhttp.responseXML);
                }
            }
        };
		var toolName = document.getElementById("tool").value;
		xmlhttp.open("GET","getfuns?tool="+toolName,true);
		emptyFuns();
		xmlhttp.send(null);
		drop2.disabled=true;
	}
	
	function updateUsersDropdown(usersXML){
		var users = usersXML.getElementsByTagName("users")[0];
		 for (var i = 0 ; i < users.childNodes.length; i++) {
			 var user = users.childNodes[i];
			 var name = user.getElementsByTagName("name")[0];
			 var element = document.createElement("option");
			 element.text = name.childNodes[0].nodeValue;
			 element.value = name.childNodes[0].nodeValue;
			 drop3.options.add(element);
		 }
		 drop3.remove(0);
		 drop3.disabled=false;
	}
	
	function updateFunsDropdown(funsXML){
		var funs = funsXML.getElementsByTagName("funs")[0];
		 for (var i = 0 ; i < funs.childNodes.length; i++) {
			 var fun = funs.childNodes[i];
			 var name = fun.getElementsByTagName("name")[0];
			 var element = document.createElement("option");
			 element.text = name.childNodes[0].nodeValue;
			 element.value = name.childNodes[0].nodeValue;
			 drop2.options.add(element);
		 }
		 drop2.remove(0);
		 drop2.disabled=false;
	}
	
	function emptyFuns(){
		for (i=drop2.length-1;i>=0;i--)
		{
		drop2.remove(i);
		}
		var element = document.createElement("option");
		element.text = "Ładowanie...";
		element.value = "null";
		drop2.options.add(element);
	}
	
	function emptyUsers(){
		for (i=drop3.length-1;i>=0;i--)
		{
		drop3.remove(i);
		}
		var element = document.createElement("option");
		element.text = "Ładowanie...";
		element.value = "null";
		drop3.options.add(element);
	}
	
	function change1()
	{
		change(drop1, drop2);
		drop3.selectedIndex=0;
		drop3.disabled=true;
	}
	
	function change2()
	{
		change(drop2, drop3);
	}
	
	function change(dropA, dropB)
	{
		
		if (dropA.selectedIndex==0)
			{
			dropB.selectedIndex=0;
			dropB.disabled=true;
			}
		else
			{
			addRemaining(dropA, dropB);
			dropB.disabled=false;
			}
	}
	
	
	
	
	function addRemaining(dropA, dropB)
	{	
		for (i=dropB.length;i>0;i--)
			{
			dropB.remove(i);
			}
		
		for (i=1;i<dropA.selectedIndex;i++)
		{
		add(dropA, dropB, i);
		}
		
		for (i=dropA.selectedIndex+1;i<dropA.length;i++)
		{
		add(dropA, dropB, i);
		}
		
	}
	
	function add(dropA, dropB, i)
	{
		var opt = document.createElement("option");
		opt.text = dropA[i].text;
		opt.value = dropA[i].value;
		dropB.options.add(opt);
	}
	
	function selectAllFuns(){
		for(i=0; i<drop2.length; i++){
			drop2.options[i].selected=!drop2.options[i].selected;
		}
	}
	
	function selectAllUsers(){
		for(i=0; i<drop3.length; i++){
			drop3.options[i].selected=!drop3.options[i].selected;
		}
	}
</script>