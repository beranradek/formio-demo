<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="form" tagdir="/WEB-INF/tags/forms" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="mapping" required="true" rtexprvalue="true" type="org.twinstone.formio.FormMapping" %>
<%@attribute name="showLength" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>

<fmt:bundle basename="${bundleName}">
<div class="form-group">
	<dl>
		<dt class="control-label col-sm-2"><label><fmt:message key="${mapping.labelKey}"/><c:if test="${showLength}"> (${fn:length(mapping.filledObject)})</c:if></label><c:if test="${mapping.required}">&nbsp;*</c:if>:</dt>
		<dd><form:messages fieldMsgs="${fieldMsgs}" fieldName="${mapping.name}" /></dd>
	</dl>
</div>
</fmt:bundle>