<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<fmt:bundle basename="com.examples.forms.domain.Person">

<script>
	$(function(){
		$.datepicker.setDefaults(
		  $.extend($.datepicker.regional[''])
		);
		$('#id-person-birthDate').datepicker({ dateFormat: "d.m.yy" });
	});
</script>

<h1>Formio demo</h1>
<ul class="nav nav-tabs">
  <li class="active"><a href="#">Simple</a></li>
  <li><a href="advanced.html">Advanced</a></li>
</ul>

<p style="margin:0.5em">Welcome in formio demo application.</p>

<c:if test="${form.validationResult.success or not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>
<c:set var="fieldMsgs" value="${form.validationResult.fieldMessages}" />

<form action="<c:url value="/index.html"/>" method="post" class="form-horizontal" role="form">
	<form:input type="hidden" fieldName="personId" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="text" fieldName="firstName" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="text" fieldName="lastName" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="text" fieldName="salary" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="checkbox" fieldName="male" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="text" fieldName="birthDate" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="text" fieldName="phone" fields="${form.fields}" fieldMsgs="${fieldMsgs}" />
	<form:input type="select" fieldName="nation" fields="${form.fields}" fieldMsgs="${fieldMsgs}" items="${nationItems}" />
	<form:button name="submitted" label="Save" cssClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
