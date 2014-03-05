<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@attribute name="validationResult" required="true" rtexprvalue="true" type="org.twinstone.formio.validation.ValidationResult" %>

<c:if test="${not empty validationResult and not validationResult.success}">
	<div class="alert alert-danger">
		<div>Form contains validation errors.</div>
		<c:forEach var="message" items="${validationResult.globalMessages}">
			<div><c:out value="${message.text}" /></div>
		</c:forEach>
	</div>
</c:if>