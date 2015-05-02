<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="name" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="value" required="false" rtexprvalue="true" type="String" %>
<%@attribute name="label" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="styleClass" required="false" rtexprvalue="true" type="String" %>
<%@attribute name="wrapperCss" required="false" rtexprvalue="true" type="String" %>
<%@attribute name="onclick" required="false" rtexprvalue="true" type="String" %>

<div class="form-group">
	<div class="<c:choose><c:when test="${not empty wrapperCss}">${wrapperCss}</c:when><c:otherwise>col-sm-offset-2 col-sm-4</c:otherwise></c:choose>">
		<button type="submit" name="${name}" value="${value}" class="${styleClass}"<c:if test="${not empty onclick}"> onclick="${onclick}"</c:if>>${label}</button>
	</div>
</div>