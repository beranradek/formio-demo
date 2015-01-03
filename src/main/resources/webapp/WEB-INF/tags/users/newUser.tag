<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="newUserMapping" required="true" rtexprvalue="true" type="net.formio.FormMapping" %>
<c:set var="bundleName" scope="request" value="net.formio.demo.domain.UserData" />
<hr/>
<fmt:bundle basename="${bundleName}">
	<form:input type="text" fieldName="login" fields="${newUserMapping.fields}" />
	<form:input type="text" fieldName="firstName" fields="${newUserMapping.fields}" />
	<form:input type="text" fieldName="lastName" fields="${newUserMapping.fields}" />
	<form:input type="text" fieldName="email" fields="${newUserMapping.fields}" />
	<form:button name="action_addUser" label="Add user" styleClass="btn"/>
</fmt:bundle>
<hr/>
