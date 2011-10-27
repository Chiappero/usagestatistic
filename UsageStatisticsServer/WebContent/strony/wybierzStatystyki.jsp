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