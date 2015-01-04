<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<c:set var="bundleName" scope="request" value="net.formio.demo.domain.Person" />
<fmt:bundle basename="${bundleName}">

<script>
	$(function(){
		$.datepicker.setDefaults(
		  $.extend($.datepicker.regional[''])
		);
	});
</script>

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="simple"/>
</jsp:include>

<p style="margin:0.5em">Welcome in Formio demo application.</p>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>

<form action="<c:url value="/simple.html"/>" method="post" class="form-horizontal" role="form">
	<form:input type="hidden" fieldName="formAuthToken" fields="${form.fields}" />
	<form:input type="hidden" fieldName="personId" fields="${form.fields}" />
	<form:input type="text" fieldName="firstName" fields="${form.fields}" />
	<form:input type="text" fieldName="lastName" fields="${form.fields}" />
	<form:input type="text" fieldName="salary" fields="${form.fields}" />
	<form:input type="checkbox" fieldName="male" fields="${form.fields}" />
	<form:input type="text" fieldName="birthDate" fields="${form.fields}" />
	<script>
		$(function(){
			$('#id-person-birthDate').datepicker({ dateFormat: "d.m.yy" });
		});
	</script>
	<form:input type="text" fieldName="phone" fields="${form.fields}" />
	<form:input type="select" fieldName="nation" fields="${form.fields}" items="${nationItems}" />
	<form:button name="submitted" label="Save" styleClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
