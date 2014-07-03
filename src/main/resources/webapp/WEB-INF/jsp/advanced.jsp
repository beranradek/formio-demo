<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />
<c:set var="bundleName" value="net.formio.demo.domain.Registration" />
<fmt:bundle basename="${bundleName}">

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="advanced"/>
</jsp:include>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>
<c:set var="fieldMsgs" value="${form.validationResult.fieldMessages}" />

<form action="<c:url value="/advanced.html"/>" method="post" enctype="multipart/form-data" class="form-horizontal" role="form">

<form:input type="hidden" fieldName="formAuthToken" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
<form:input type="file" fieldName="cv" fields="${form.fields}" fieldMsgs="${fieldMsgs}" postfix="${form.fields['cv'].filledObject.fileName}" bundleName="${bundleName}" />

<c:set var="certificatesMapping" value="${form.nested['certificates']}"/>
<form:mappingLabel fieldMsgs="${fieldMsgs}" mapping="${certificatesMapping}" bundleName="${bundleName}" />
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
		<form:input type="file" fieldName="file" fields="${mapping.fields}" fieldMsgs="${fieldMsgs}" postfix="${mapping.filledObject.file.fileName}" withoutLabel="true" bundleName="${bundleName}" />
	</c:forEach>
</c:otherwise>
</c:choose>

<form:fieldGroupLabel fieldMsgs="${fieldMsgs}" formField="${form.fields['interests']}" bundleName="${bundleName}"/>
<c:forEach var="interest" items="${interests}">
	<form:input type="checkbox" fieldName="interests" fields="${form.fields}" fieldMsgs="${fieldMsgs}" itemId="${interest.interestId}" itemLabel="${interest.name}" bundleName="${bundleName}"/>
</c:forEach>

<form:fieldGroupLabel fieldMsgs="${fieldMsgs}" formField="${form.fields['attendanceReasons']}" bundleName="${bundleName}"/>
<c:forEach var="attendanceReason" items="${attendanceReasons}">
	<form:input type="checkbox" fieldName="attendanceReasons" fields="${form.fields}" fieldMsgs="${fieldMsgs}" itemId="${attendanceReason}" itemLabel="${attendanceReason}" localize="true" bundleName="${bundleName}"/>
</c:forEach>

<form:input type="text" fieldName="email" fields="${form.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />

<form:messages fieldMsgs="${fieldMsgs}" fieldName="registration-contactAddress" />
<c:set var="contactAddressMapping" value="${form.nested['contactAddress']}"/>
<c:if test="${not empty contactAddressMapping.filledObject}">
	<form:mappingLabel fieldMsgs="${fieldMsgs}" mapping="${contactAddressMapping}" bundleName="${bundleName}" />
	<c:set var="contactAddrFields" value="${contactAddressMapping.fields}" />
	<form:input type="text" fieldName="street" fields="${contactAddrFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="city" fields="${contactAddrFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="zipCode" fields="${contactAddrFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
</c:if>

<c:set var="colleguesMapping" value="${form.nested['collegues']}"/>
<form:mappingLabel fieldMsgs="${fieldMsgs}" mapping="${colleguesMapping}" showLength="true" bundleName="${bundleName}" />
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
		<form:input type="text" fieldName="name" fields="${mapping.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
		<form:input type="text" fieldName="email" fields="${mapping.fields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
		<c:set var="regDateFields" value="${mapping.nested['regDate'].fields}" />
		<form:input type="text" fieldName="month" fields="${regDateFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
		<form:input type="text" fieldName="year" fields="${regDateFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
	</c:forEach>
</c:otherwise>
</c:choose>

<hr/>
<c:set var="newCollegueMapping" value="${form.nested['newCollegue']}"/>
<form:mappingLabel fieldMsgs="${fieldMsgs}" mapping="${newCollegueMapping}" bundleName="${bundleName}" />
<c:set var="newCollegueFields" value="${newCollegueMapping.fields}" />
<form:input type="text" fieldName="name" fields="${newCollegueFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
<form:input type="text" fieldName="email" fields="${newCollegueFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
<c:set var="regDateFields" value="${newCollegueMapping.nested['regDate'].fields}" />
<form:input type="text" fieldName="month" fields="${regDateFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
<form:input type="text" fieldName="year" fields="${regDateFields}" fieldMsgs="${fieldMsgs}" bundleName="${bundleName}" />
<form:button name="newCollegue" label="Add collegue" styleClass="btn btn-info"/>
<hr/>	
<form:button name="submitted" label="Confirm registration" styleClass="btn btn-default btn-primary"/>
</form>

</fmt:bundle>
<jsp:include page="footer.jsp" />
