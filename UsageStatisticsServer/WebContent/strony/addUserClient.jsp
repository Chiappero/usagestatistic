<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tag" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<LINK rel="stylesheet" href="style.css">
<a href="j_spring_security_logout">Logout</a>
<div>
<form:form method="post" action="addUserClient">
User:<form:input path="user" /> 
Password:<form:input path="password" />
  <input type="submit" value="Add user"/>
  </form:form>
</div>

