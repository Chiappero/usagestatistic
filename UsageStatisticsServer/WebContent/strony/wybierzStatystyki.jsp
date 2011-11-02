<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<LINK rel="stylesheet" href="style.css">
<div>
<form:form method="post" action="results">
      <table style="border:2px solid grey; ">
          <tr><td>Stan z dnia: <c:out value="${data}"></c:out></td></tr>
          <tr><td>Narzedzia</td><td><form:checkboxes path="tools" items="${tools}"/></td></tr>
          <tr><td>Userzy</td><td><form:checkboxes path="users" items="${users}" /></td></tr>
          <tr><td>Funkcjonalnosci</td><td><form:checkboxes path="functionalities" items="${functionalities}"/></td></tr>
          <tr><td>Sortowanie1</td><td>
       	  <form:select path="sortChoose1"  onchange="ajax();" id="columns1" name="columns1">
		   	<c:forEach var="col" items="${columns}">
	 		  <option value="${col}">${col}</option>
	 		</c:forEach>
		  </form:select>     
		  </td></tr>
		  <tr><td>Sortowanie2</td><td>
		  <form:select path="sortChoose2"  onchange="change2();" id="columns2" name="columns2">
         	  <c:forEach var="col" items="${columns}">
	 		  <option value="${col}">${col}</option>
	 		  </c:forEach>
	 		</form:select>        
		  </td></tr>
		  <tr><td>Sortowanie3</td><td>
		   <form:select path="sortChoose3" id="columns3" name="columns2">
           <c:forEach var="col" items="${columns}">
	 		  <option value="${col}">${col}</option>
	 		  </c:forEach>
			</form:select>                
		  </td></tr>
      </table>
        <input type="submit" value="Pokaz logi"/>
  </form:form>
</div>

<script language="javascript"><!--
	var drop1 = document.getElementById("columns1");
	var drop2 = document.getElementById("columns2");	
	var drop3 = document.getElementById("columns3");	
	drop1.selectedIndex=0;
	drop2.selectedIndex=0;
	drop3.selectedIndex=0;


	drop1.disabled = false;
	drop2.disabled = true;
	drop3.disabled = true;
	
	function ajax()
	{
		alert('start');
		var xmlhttp;
		if (window.XMLHttpRequest)
		  {// code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		  }
		else
		  {// code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		  }
		var handlerFunction = getReadyStateHandler(xmlhttp, updateDropdown);
		xmlhttp.onreadystatechange = handlerFunction;
		var toolName = document.getElementById("columns1").value;
		xmlhttp.open("GET","ajax?tool="+toolName,true);
		xmlhttp.send();
		alert('koniec');
	}
	
	function updateDropdown(funsXML){
		 var funs = funsXML.getElementsByTagName("funs")[0];
		 var items = funs.getElementsByTagName("fun");
		 for (var I = 0 ; I < items.length ; I++) {
			 var item = items[I];
			 var name = item.getAttribute("name");
			 drop2.options.add(name);
		 }
	}
	
	function getReadyStateHandler(req, responseXmlHandler) {

		  // Return an anonymous function that listens to the 
		  // XMLHttpRequest instance
		  return function () {

		    // If the request's status is "complete"
		    if (req.readyState == 4) {
		      
		      // Check that a successful server response was received
		      if (req.status == 200) {

		        // Pass the XML payload of the response to the 
		        // handler function
		        responseXmlHandler(req.responseXML);

		      } else {

		        // An HTTP problem has occurred
		        alert("HTTP error: "+req.status);
		      }
		    }
		  }
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
	
	
	
	
</script>