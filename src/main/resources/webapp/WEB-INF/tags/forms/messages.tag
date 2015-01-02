<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.List" %>

<c:forEach var="message" items="${fieldMsgs}">
	<div class="${message.severity.styleClass}"><c:out value="${message.text}" /></div>
</c:forEach>
