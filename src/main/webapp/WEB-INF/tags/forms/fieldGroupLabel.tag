<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="form" tagdir="/WEB-INF/tags/forms" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="formField" required="true" rtexprvalue="true" type="org.twinstone.formio.FormField" %>

<div class="form-group">
	<dl>
		<dt class="control-label col-sm-2"><label><fmt:message key="${formField.labelKey}"/></label><c:if test="${formField.required}">&nbsp;*</c:if>:</dt>
		<dd><form:messages fieldMsgs="${fieldMsgs}" fieldName="${formField.name}" /></dd>
	</dl>
</div>
