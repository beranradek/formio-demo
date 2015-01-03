<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<c:set var="bundleName" scope="request" value="net.formio.demo.domain.Registration" />
<%-- The fmt:bundle tag will make the specified bundle available to all fmt:message tags that occur between fmt:bundle tags. --%>
<fmt:bundle basename="${bundleName}">

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="advanced"/>
</jsp:include>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>

<form action="<c:url value="/advanced.html"/>" method="post" enctype="multipart/form-data" class="form-horizontal" role="form">

<form:input type="hidden" fieldName="formAuthToken" fields="${form.fields}" />
<form:input type="file" fieldName="cv" fields="${form.fields}" postfix="${form.fields['cv'].filledObject.fileName}" />

<c:set var="certificatesMapping" value="${form.nested['certificates']}"/>
<form:mappingLabel mapping="${certificatesMapping}" />
<c:choose>
<c:when test="${empty certificatesMapping.filledObject}">
	<div class="form-group">
		<div class="control-label col-sm-2"></div>
		<div class="col-sm-4">
			Please state your certificates.
		</div>
	</div>
</c:when>
<c:otherwise>
	<c:forEach var="mapping" items="${certificatesMapping.list}" varStatus="status">
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-4 remove-button">
				<c:if test="${not empty mapping.filledObject.file.fileName}">
					<a href="<c:url value="/advanced.html?removeCertificate=${status.index}"/>"><span class="glyphicon glyphicon-remove" title="Remove"></span></a>
				</c:if>
			</div>
		</div>
		<form:input type="file" fieldName="file" fields="${mapping.fields}" postfix="${mapping.filledObject.file.fileName}" withoutLabel="true" />
	</c:forEach>
</c:otherwise>
</c:choose>

<form:fieldGroupLabel formField="${form.fields['interests']}" />
<c:forEach var="interest" items="${interests}">
	<form:input type="checkbox" fieldName="interests" fields="${form.fields}" itemId="${interest.interestId}" itemLabel="${interest.name}" />
</c:forEach>

<form:fieldGroupLabel formField="${form.fields['attendanceReasons']}" />
<c:forEach var="attendanceReason" items="${attendanceReasons}">
	<form:input type="checkbox" fieldName="attendanceReasons" fields="${form.fields}" itemId="${attendanceReason}" itemLabel="${attendanceReason}" localize="true" />
</c:forEach>

<form:input type="text" fieldName="email" fields="${form.fields}" />

<c:set var="contactAddressMapping" value="${form.nested['contactAddress']}"/>
<form:messages fieldMsgs="${contactAddressMapping.validationMessages}" />
<c:if test="${not empty contactAddressMapping.filledObject}">
	<form:mappingLabel mapping="${contactAddressMapping}" />
	<c:set var="contactAddrFields" value="${contactAddressMapping.fields}" />
	<form:input type="text" fieldName="street" fields="${contactAddrFields}" />
	<form:input type="text" fieldName="city" fields="${contactAddrFields}" />
	<form:input type="text" fieldName="zipCode" fields="${contactAddrFields}" />
</c:if>

<c:set var="colleguesMapping" value="${form.nested['collegues']}"/>
<form:mappingLabel mapping="${colleguesMapping}" showLength="true" />
<c:choose>
<c:when test="${empty colleguesMapping.filledObject}">
	<div class="form-group">
		<div class="control-label col-sm-2"></div>
		<div class="col-sm-4">
			Please state your collegues.
		</div>
	</div>
</c:when>
<c:otherwise>
	<c:forEach var="mapping" items="${colleguesMapping.list}" varStatus="status">
		<div class="form-group">
			<div class="control-label col-sm-2"><fmt:message key="registration-collegue"/>:</div>
			<div class="col-sm-4 remove-button">
				<a href="<c:url value="/advanced.html?removeCollegue=${status.index}"/>"><span class="glyphicon glyphicon-remove" title="Remove"></span></a>
			</div>
		</div>
		<form:input type="text" fieldName="name" fields="${mapping.fields}" />
		<form:input type="text" fieldName="email" fields="${mapping.fields}" />
		<c:set var="regDateFields" value="${mapping.nested['regDate'].fields}" />
		<form:input type="text" fieldName="month" fields="${regDateFields}" />
		<form:input type="text" fieldName="year" fields="${regDateFields}" />
	</c:forEach>
</c:otherwise>
</c:choose>

<hr/>
<c:set var="newCollegueMapping" value="${form.nested['newCollegue']}"/>
<form:mappingLabel mapping="${newCollegueMapping}" />
<c:set var="newCollegueFields" value="${newCollegueMapping.fields}" />
<form:input type="text" fieldName="name" fields="${newCollegueFields}" />
<form:input type="text" fieldName="email" fields="${newCollegueFields}" />
<c:set var="regDateFields" value="${newCollegueMapping.nested['regDate'].fields}" />
<form:input type="text" fieldName="month" fields="${regDateFields}" />
<form:input type="text" fieldName="year" fields="${regDateFields}" />
<form:button name="newCollegue" label="Add collegue" styleClass="btn btn-info"/>
<hr/>	
<form:button name="submitted" label="Confirm registration" styleClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
