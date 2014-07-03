<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@taglib prefix="userTags" tagdir="/WEB-INF/tags/users" %>
<%@attribute name="userMappings" required="true" rtexprvalue="true" type="java.util.List" %>
<table class="table">
<tr>
	<th>Login</th>
	<th>First name</th>
	<th>Last name</th>
	<th>E-mail</th>
	<th>Action</th>
</tr>
<c:choose>
<c:when test="${not empty userMappings}">
	<c:forEach var="userMapping" items="${userMappings}" varStatus="loop">
		<userTags:userRow userMapping="${userMapping}" ord="${loop.index}"/>
	</c:forEach>
</c:when>
<c:otherwise>
	<tr id="no-records">
		<userTags:noRecords/>
	</tr>
</c:otherwise>
</c:choose>
</table>