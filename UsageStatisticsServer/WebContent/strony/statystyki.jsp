<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div>
<a href="j_spring_security_logout">Logout</a>
      <table style="border:2px solid grey; ">
          <tr><td>Stan z dnia: <c:out value="${data}"></c:out></td></tr>
          <tr><td>Tool: <c:out value="${tool}"></c:out></td></tr>
          <tr><td>Logi:</td></tr>
          <tr><td>FUNKCJONALNOSC</td><td>DATA</td><td>ILOŚĆ</td>
          <c:forEach var="log" items="${logi}">
          <tr><td>${log.functionality}</td><td>${log.date}</td><td>${log.count}</td></tr>
          </c:forEach>
      </table>
      <c:if test="${logi.isEmpty()==true}">Niestety nie znaleziono zadnych wynikow dla zadanego kryterium<br></c:if>
      <a href="results">Wroc</a>
       

</div>