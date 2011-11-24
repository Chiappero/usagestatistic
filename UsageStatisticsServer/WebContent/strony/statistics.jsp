<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div>
<a href="j_spring_security_logout">Logout</a><br /><br />
      <table style="border:2px solid grey; ">
          <tr><td>Stan z dnia:</td><td><c:out value="${data}"></c:out></td></tr>
          <tr><td><br />Logi od:</td><td><br /><c:out value="${dateFrom}"></c:out></td></tr>
          <tr><td>Logi do:</td><td><c:out value="${dateTill}"></c:out></td></tr>
          <tr><td><br />Tool:</td><td><br /><c:out value="${tool}"></c:out></td></tr>
          <tr><td><br />Logi:<br /></td></tr>
          <tr><td>FUNKCJONALNOSC</td><c:if test="${showParams}"><td>PARAMETRY</td></c:if><td>ILOŚĆ</td>
          <c:forEach var="log" items="${logi}">
          <tr><td>${log.functionality}</td><c:if test="${showParams}"><td>${log.parameters}</td></c:if><td>${log.count}</td></tr>
          </c:forEach>
      </table>
      <c:if test="${logi.isEmpty()==true}">Niestety nie znaleziono zadnych wynikow dla zadanego kryterium<br></c:if>
      <br /><a href="logs">Wroc</a>
</div>