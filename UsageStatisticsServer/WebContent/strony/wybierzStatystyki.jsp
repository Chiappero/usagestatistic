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
          
      
      </table>
        <input type="submit" value="Pokaz logi"/>
  </form:form>
</div>