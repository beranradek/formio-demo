<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<c:set var="bundleName" value="net.formio.demo.domain.User" />
<fmt:bundle basename="${bundleName}">

<ul class="nav nav-tabs">
  <li><a href="<c:url value="/simple.html"/>">Simple</a></li>
  <li><a href="<c:url value="/advanced.html"/>">Advanced</a></li>
  <li class="active"><a href="#">AJAX Form</a></li>
</ul>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>
<c:set var="fieldMsgs" value="${form.validationResult.fieldMessages}" />

<form action="<c:url value="/ajax.html"/>" method="post" class="form-horizontal" role="form">
	<form:input type="text" fieldName="login" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:button name="submitted" label="Save" styleClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
