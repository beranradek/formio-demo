<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="form" tagdir="/WEB-INF/tags/forms" %>
<%@taglib prefix="myfn" uri="/WEB-INF/custom-functions.tld" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="formField" required="true" rtexprvalue="true" type="org.twinstone.formio.FormField" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>

<fmt:bundle basename="${bundleName}">
<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[formField.name])}"/>
<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
	<label class="control-label col-sm-2"><fmt:message key="${formField.labelKey}"/><c:if test="${formField.required}">&nbsp;*</c:if>:</label>
	<div><form:messages fieldMsgs="${fieldMsgs}" fieldName="${formField.name}" /></div>
</div>
</fmt:bundle>