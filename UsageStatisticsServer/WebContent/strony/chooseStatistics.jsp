<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<LINK rel="stylesheet" href="style.css">
<div>
<form:form method="post" action="logs">
      <table style="border:2px solid grey; ">
          <tr><td> <c:out value="${data}"></c:out></td></tr>
          <tr><td>Tool</td><td>       	  
          <form:select path="tool" onchange="checkTool();getFunsAndUsersWithAjax();" id="tool" name="tool" style="min-width: 150px;">
		   	<option value="null">---Select---</option>
		   	<c:forEach var="ttt" items="${tools}">
	 		  <option value="${ttt}">${ttt}</option>
	 		</c:forEach>
		  </form:select> 
		  </td></tr>
          <tr><td>Functionalities<br />
          <input type="button" onclick="selectAllFuns()" value="Invert selection"/></td><td>
          <form:select path="functionalities" id="functionalities" name="functionalities" style="min-width: 150px; height: 200px;">
	 	 </form:select>
		  </td></tr>
		  <tr><td>Users<br/>
		  <input type="button" onclick="selectAllUsers()" value="Invert selection"/></td><td>
		  <form:select path="users" id="users" name="users" style="min-width: 150px; height: 200px;"></form:select>
		  </td></tr> 
      <form:errors path="dateFrom" />
      <tr><td>Date from:</td><td>
      <form:input path="dateFrom" id="dateFrom" type="date" /></td></tr>
      <tr><td>Date till:</td><td><form:input path="dateTill" id="dateTill" type="date" /></td></tr>
      <tr><td>Parameters:</td><td><form:checkbox path="param" /> show parameters</td></tr> 
      </table>
        <input id="submit" type="submit" onclick="checkDates(this);return false;" value="Show logs"/>
  </form:form>
</div>

<script language="javascript">
	if(document.URL.charAt(document.URL.length-1)=='/'){
		window.location=document.URL.substring(0, document.URL.length-1);
	}
	
	var drop1 = document.getElementById("tool");
	var drop2 = document.getElementById("functionalities");		
	var drop3 = document.getElementById("users");
	
	drop1.disabled = false;
	drop2.disabled = true;
	drop3.disabled = true;
	
	getFunsAndUsersWithAjax();
	getUsersWithAjax();
	checkTool();
	
	function checkTool(){
		if(drop1.value=="null"){
			document.getElementById("submit").disabled=true;
		}
		else{
			document.getElementById("submit").disabled=false;
		}
	}
	
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
		var url=document.URL;
		if(url.charAt(url.length-1)=='\\' || url.charAt(url.length-1)=='/'){
			url=url.substring(0, url.length-1);
		}
		if(url.substring(url.length-4, url.length)=="logs"){
			url=url=url.substring(0, url.length-4);
		}
		xmlhttp.open("GET",url+"getusers?tool="+toolName,true);
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
		var url=document.URL;
		if(url.charAt(url.length-1)=='\\' || url.charAt(url.length-1)=='/'){
			url=url.substring(0, url.length-1);
		}
		if(url.substring(url.length-4, url.length)=="logs"){
			url=url=url.substring(0, url.length-4);
		}
		xmlhttp.open("GET",url+"getfuns?tool="+toolName,true);
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
	
	function liczbaDniLuty (year){
	    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
	}
	
	function checkDates(form)
	  {
		startdate=document.getElementById("dateFrom");
		enddate=document.getElementById("dateTill");
		
		// regular expression to match required date format
	    re = /^\d{4}\-\d{2}\-\d{2}$/;

		if(startdate.value != '') {
			if(regs = startdate.value.match(re)) {
		        var y=startdate.value.substring(0,4);
		        var m=startdate.value.substring(5,7);
		        var d=startdate.value.substring(8,10);
				if(y < 1902 || y > (new Date()).getFullYear()) {
		          alert("Acceptable dates from 1902 to " + (new Date()).getFullYear());
		          startdate.focus();
		          startdate.select();
		          return false;
		        }
		        if(m < 1 || m > 12) {
		          alert("Incorrect month value");
		          startdate.focus();
		          startdate.select();
		          return false;
		        }
		        if(d < 1 || d > 31) {
		          alert("Incorrect day value");
		          startdate.focus();
		          startdate.select();
		          return false;
		        }
		      //luty
		        if(m=="02") {
			          if(d>liczbaDniLuty(y)){
			        	  alert("Incorrect day value");
			        	  startdate.focus();
			        	  startdate.select();
				          return false;
			          }
			    }		        
		        //miesiace 30 dniowe
		        if((m=="04"|| m=="06" || m=="09" || m=="11") && d > 30) {
			          if(d>30){
			        	  alert("Incorrect day value");
			        	  startdate.focus();
			        	  startdate.select();
				          return false;
			          }
			    }
	      } else {
	        alert("Incorrect date format. please enter date format yyyy-mm-dd format");
	        startdate.focus();
	        startdate.select();
	        return false;
	      }
	    }
		if(enddate.value != '') {
			if(regs = enddate.value.match(re)) {
				var y=enddate.value.substring(0,4);
		        var m=enddate.value.substring(5,7);
		        var d=enddate.value.substring(8,10);
				if(y < 1902 || y > (new Date()).getFullYear()) {
		          alert("Acceptable dates from 1902 to " + (new Date()).getFullYear());
		          enddate.focus();
		          enddate.select();
		          return false;
		        }
		        if(m < 1 || m > 12) {
		          alert("Incorrect month value");
		          enddate.focus();
		          enddate.select();
		          return false;
		        }
		        if(d < 1 || d > 31) {
		          alert("Incorrect day value");
		          enddate.focus();
		          enddate.select();
		          return false;
		        }
		        //luty
		        if(m=="02") {
			          if(d>liczbaDniLuty(y)){
			        	  alert("Incorrect day value");
				          enddate.focus();
				          enddate.select();
				          return false;
			          }
			    }		        
		        //miesiace 30 dniowe
		        if((m=="04"|| m=="06" || m=="09" || m=="11") && d > 30) {
			          if(d>30){
			        	  alert("Incorrect day value");
				          enddate.focus();
				          enddate.select();
				          return false;
			          }
			    }
	      } else {
	        alert("Incorrect date format. please enter date format yyyy-mm-dd format");
	        enddate.focus();
	        enddate.select();
	        return false;
	      }
	    }
	    form.submit();
	    return false;
	  }

</script>