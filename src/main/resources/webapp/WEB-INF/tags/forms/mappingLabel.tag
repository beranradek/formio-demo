<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="myfn" uri="/WEB-INF/custom-functions.tld" %>
<%@taglib prefix="form" tagdir="/WEB-INF/tags/forms" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="mapping" required="true" rtexprvalue="true" type="net.formio.FormMapping" %>
<%@attribute name="showLength" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>

<fmt:bundle basename="${bundleName}">
<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[mapping.name])}"/>
<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
	<div class="control-label col-sm-2"><label><fmt:message key="${mapping.labelKey}"/><c:if test="${showLength}"> (${fn:length(mapping.filledObject)})</c:if><c:if test="${mapping.required}">&nbsp;*</c:if>:</label></div>
	<div><form:messages fieldMsgs="${fieldMsgs}" fieldName="${mapping.name}" /></div>
</div>
</fmt:bundle>