<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="dynamic"/>
</jsp:include>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>

<form action="<c:url value="/dynamic.html"/>" method="post" class="tdi" role="form">
	<c:out value="${formMarkup}" escapeXml="false" />
	<hr/>
	<c:out value="${addAccessoriesForm}" escapeXml="false" />
	<hr/>
	<form:button name="save" label="Submit" styleClass="btn btn-default btn-primary"/>
</form>

<jsp:include page="footer.jsp" />
