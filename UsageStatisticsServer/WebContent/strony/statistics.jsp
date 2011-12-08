<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div>
<a href="j_spring_security_logout">Logout</a><br /><br />
      <table style="border:2px solid grey; ">
          <tr><td> </td><td><c:out value="${data}"></c:out></td></tr>
          <tr><td><br />Logs from:</td><td><br /><c:out value="${dateFrom}"></c:out></td></tr>
          <tr><td>Logs till:</td><td><c:out value="${dateTill}"></c:out></td></tr>
          <tr><td><br />Tool:</td><td><br /><c:out value="${tool}"></c:out></td></tr>
          <tr><td><br />Logs:<br /></td></tr>
          <tr><td>FUNCTIONALITY</td><c:if test="${showParams}"><td>PARAMETERS</td></c:if><td>AMOUNT</td>
          <c:forEach var="log" items="${logs}">
          <tr><td>${log.functionality}</td><c:if test="${showParams}"><td>${log.parameters}</td></c:if><td>${log.count}</td></tr>
          </c:forEach>
      </table>
      <c:if test="${logs.isEmpty()==true}">No logs for specified filter<br></c:if>
      <br /><a href="logs">Back</a>
</div>