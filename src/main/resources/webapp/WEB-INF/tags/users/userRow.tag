<%@include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="userMapping" required="true" rtexprvalue="true" type="net.formio.FormMapping" %>
<%@attribute name="ord" required="true" rtexprvalue="true" type="java.lang.Integer" %>
<c:set var="bundleName" scope="request" value="net.formio.demo.domain.UserData" />
<tr id="user-row-${ord}">
	<td><form:input type="text" fieldName="login" fields="${userMapping.fields}" withoutLabel="true" inputWrapperCss="col-sm-10" /></td>
	<td><form:input type="text" fieldName="firstName" fields="${userMapping.fields}" withoutLabel="true" inputWrapperCss="col-sm-10" /></td>
	<td><form:input type="text" fieldName="lastName" fields="${userMapping.fields}" withoutLabel="true" inputWrapperCss="col-sm-10" /></td>
	<td><form:input type="text" fieldName="email" fields="${userMapping.fields}" withoutLabel="true" inputWrapperCss="col-sm-10" /></td>
	<td><form:button name="action_removeUser_${ord}" label="Remove user" styleClass="btn" wrapperCss="col-sm-10" onclick="return window.confirm('Do you really want to remove this user?');" /></td>
</tr>