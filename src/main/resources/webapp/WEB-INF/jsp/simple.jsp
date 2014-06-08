<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<c:set var="bundleName" value="net.formio.demo.domain.Person" />
<fmt:bundle basename="${bundleName}">

<script>
	$(function(){
		$.datepicker.setDefaults(
		  $.extend($.datepicker.regional[''])
		);
		$('#id-person-birthDate').datepicker({ dateFormat: "d.m.yy" });
	});
</script>

<ul class="nav nav-tabs">
  <li class="active"><a href="#">Simple</a></li>
  <li><a href="<c:url value="/advanced.html"/>">Advanced</a></li>
  <%-- <li><a href="<c:url value="/ajax.html"/>">AJAX Form</a></li> --%>
</ul>

<p style="margin:0.5em">Welcome in formio demo application.</p>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>
<c:set var="fieldMsgs" value="${form.validationResult.fieldMessages}" />

<form action="<c:url value="/simple.html"/>" method="post" class="form-horizontal" role="form">
	<form:input type="hidden" fieldName="formAuthToken" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="hidden" fieldName="personId" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="firstName" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="lastName" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="salary" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="checkbox" fieldName="male" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="birthDate" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="phone" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="select" fieldName="nation" fields="${form.fields}" fieldMsgs="${fieldMsgs}" items="${nationItems}" bundleName="${bundleName}" />
	<form:button name="submitted" label="Save" styleClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
