<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<%@taglib prefix="userTags" tagdir="/WEB-INF/tags/users" %>
<jsp:include page="header.jsp" />

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="ajax"/>
</jsp:include>

<p style="margin:0.5em">AJAX form powered by Formio &amp; <a href="https://twinstone.org/project/TDI">Twinstone TDI</a></p>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>

<form:globalErrors validationResult="${form.validationResult}"/>

<%-- Class tdi invokes AJAX request when the form is submitted. --%>
<form action="<c:url value="/ajax.html"/>" method="post" class="tdi form-horizontal" role="form">

<c:set var="userMappings" value="${form.nested['users'].list}" />
<div id="user-list">
	<userTags:userTable userMappings="${userMappings}"/>
</div>

<c:set var="newUserMapping" value="${form.nested['newUser']}" />
<div id="user-new">
	<userTags:newUser newUserMapping="${newUserMapping}"/>
</div>

<form:button name="action_saveChanges" label="Save changes" styleClass="btn btn-default btn-primary"/>
</form>

<jsp:include page="footer.jsp" />
