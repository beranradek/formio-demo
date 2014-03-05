<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="myfn" uri="/WEB-INF/custom-functions.tld" %>
<%@attribute name="fields" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="fieldName" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="type" required="true" rtexprvalue="true" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="itemId" required="true" rtexprvalue="true" type="java.lang.Object" %>
<%@attribute name="itemLabel" required="true" rtexprvalue="true" type="java.lang.Object" %>
<%@attribute name="localize" required="false" rtexprvalue="true" type="java.lang.Boolean" %>

<fmt:bundle basename="${bundleName}">
<c:set var="field" value="${fields[fieldName]}" />
<div class="form-group compact">
	<dl>
		<dt class="control-label col-sm-2"></dt>
		<dd class="col-sm-4">
			<input type="${type}" name="${field.name}[]" id="id-${field.name}-${itemId}"<c:if test="${myfn:contains(field.filledObjects, itemId)}"> checked="checked"</c:if> value="${itemId}" />
			<label for="id-${field.name}-${itemId}">
				<c:choose>
					<c:when test="${localize}"><fmt:message key="${itemLabel}"/></c:when>
					<c:otherwise>${itemLabel}</c:otherwise>
				</c:choose>
			</label>
		</dd>
	</dl>
</div>
</fmt:bundle>