<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="mapping" required="true" rtexprvalue="true" type="net.formio.FormMapping" %>
<%@attribute name="showLength" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>

<fmt:bundle basename="${bundleName}">
<c:set var="maxSev" value="${myfn:maxSeverity(mapping.validationMessages)}"/>
<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
	<div class="control-label col-sm-2"><label><fmt:message key="${mapping.labelKey}"/><c:if test="${showLength}"> (${fn:length(mapping.filledObject)})</c:if><c:if test="${mapping.required}">&nbsp;*</c:if>:</label></div>
	<div><form:messages fieldMsgs="${mapping.validationMessages}" /></div>
</div>
</fmt:bundle>