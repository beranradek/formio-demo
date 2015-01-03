<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="formField" required="true" rtexprvalue="true" type="net.formio.FormField" %>
<%@attribute name="bundleName" required="false" rtexprvalue="true" type="String" %>

<fmt:bundle basename="${bundleName}">
<c:set var="maxSev" value="${myfn:maxSeverity(formField.validationMessages)}"/>
<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
	<label class="control-label col-sm-2"><fmt:message key="${formField.labelKey}"/><c:if test="${formField.required}">&nbsp;*</c:if>:</label>
	<div><form:messages fieldMsgs="${formField.validationMessages}" /></div>
</div>
</fmt:bundle>