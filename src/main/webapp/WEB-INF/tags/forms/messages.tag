<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="fieldName" required="true" rtexprvalue="true" type="java.lang.String" %>

<c:forEach var="message" items="${fieldMsgs[fieldName]}">
	<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
</c:forEach>
