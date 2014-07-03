<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="newUserMapping" required="true" rtexprvalue="true" type="net.formio.FormMapping" %>
<c:set var="bundleName" value="net.formio.demo.domain.UserData" />
<hr/>
<fmt:bundle basename="${bundleName}">
	<c:set var="newUserMsgs" value="${newUserMapping.validationResult.fieldMessages}" />
	<form:input type="text" fieldName="login" fields="${newUserMapping.fields}" fieldMsgs="${newUserMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="firstName" fields="${newUserMapping.fields}" fieldMsgs="${newUserMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="lastName" fields="${newUserMapping.fields}" fieldMsgs="${newUserMsgs}" bundleName="${bundleName}" />
	<form:input type="text" fieldName="email" fields="${newUserMapping.fields}" fieldMsgs="${newUserMsgs}" bundleName="${bundleName}" />
	<form:button name="action_addUser" label="Add user" styleClass="btn"/>
</fmt:bundle>
<hr/>
