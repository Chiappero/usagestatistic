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
       	  <form:select path="sortChoose1"  onchange="change1();" id="columns1" name="columns1">
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
		   <form:select path="sortChoose3"  id="columns3" name="columns2">
           <c:forEach var="col" items="${columns}">
	 		  <option value="${col}">${col}</option>
	 		  </c:forEach>
			</form:select>                
		  </td></tr>
      </table>
        <input type="submit" value="Pokaz logi"/>
  </form:form>
</div>

<script language="javascript">
	document.getElementById("columns1").disabled = false;
	document.getElementById("columns2").disabled = true;
	document.getElementById("columns3").disabled = true;
	
	function change1(){
		var none = document.getElementById("columns1")[0].value;
		var drop1 = document.getElementById("columns1");
		var drop2 = document.getElementById("columns2");
		var drop3 = document.getElementById("columns3");
		
		if (drop1.value != none){
			drop2.disabled = false;
			
			drop2.remove(drop1.selectedIndex);
		}
		else{
			drop2.disabled = true;
			drop3.disabled = true;
			reload2();
			reload3();
		}
	}
	
	function change2(){
		var none = document.getElementById("columns2")[0].value;
		var drop1 = document.getElementById("columns1");
		var drop2 = document.getElementById("columns2");
		var drop3 = document.getElementById("columns3");
		
		if (drop2.value != none){
			drop3.disabled = false;
			
			drop3.remove(drop1.selectedIndex);
			drop3.remove(drop2.selectedIndex);
		}
		else{
			drop3.disabled = true;
			reload3();
		}
	}
		
	function reload2(){
		var drop1 = document.getElementById("columns1");
		var drop2 = document.getElementById("columns2");
		var len = drop2.length;
		for(i=0; i<len;i++){
			drop2.remove('0');
		}
		for(i=0; i<drop1.length;i++){
			var opt = document.createElement("option");
			drop2.options.add(opt);
			opt.text = drop1[i].value;
	        opt.value = drop1[i].value;
		}
	}
	
	function reload3(){
		var drop1 = document.getElementById("columns1");
		var drop3 = document.getElementById("columns3");
		var len = drop3.length;
		for(i=0; i<len;i++){
			drop3.remove('0');
		}
		for(i=0; i<drop1.length;i++){
			var opt = document.createElement("option");
			drop3.options.add(opt);
			opt.text = drop1[i].value;
	        opt.value = drop1[i].value;
		}
	}
</script>