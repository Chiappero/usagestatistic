<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<div>

      <table style="border:2px solid grey; ">
          <tr><td>Stan z dnia: <c:out value="${data}"></c:out></td></tr>
          <tr><td>Logi:</td></tr>
          <tr><td>NARZEDZIE</td><td>FUNKCJONALNOSC</td><td>PARAMETRY</td><td>DATA</td><td>UZYTKOWNIK</td>
          <c:forEach var="log" items="${logi}">
          <tr><td>${log.tool}</td><td>${log.functionality}</td><td>${log.parameters}</td><td>${log.dateTime}</td><td>${log.user}</td></tr>
          </c:forEach>
      </table>
      <c:if test="${logi.isEmpty()==true}">Niestety nie znaleziono zadnych wynikow dla zadanego kryterium<br></c:if>
      <a href="results">Wroc</a>
       

</div>