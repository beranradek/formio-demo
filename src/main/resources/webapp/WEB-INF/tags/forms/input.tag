<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@attribute name="fields" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="fieldMsgs" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="fieldName" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="type" required="true" rtexprvalue="true" %>
<%@attribute name="bundleName" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="items" required="false" rtexprvalue="true" type="java.util.Map" %>
<%@attribute name="multi" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="withoutLabel" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="inputCss" required="false" rtexprvalue="true" type="String" %>
<%@attribute name="postfix" required="false" rtexprvalue="true" type="java.lang.Object" %>

<fmt:bundle basename="${bundleName}">
<c:set var="field" value="${fields[fieldName]}" />
<c:choose>
	<c:when test="${type == 'checkbox' || type == 'radio'}">
		<div class="form-group">
			<dl>
				<dt class="control-label col-sm-2"></dt>
				<dd class="col-sm-4">
					<input type="${type}" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>"<c:if test="${field.value}"> checked="checked"</c:if> value="1" />
					<c:if test="${not withoutLabel}">
						<label for="id-${field.name}"><fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if></label>
					</c:if>
					<c:if test="${not multi}">
						<c:forEach var="message" items="${fieldMsgs[field.name]}">
							<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
						</c:forEach>
					</c:if>
				</dd>
			</dl>
		</div>
	</c:when>
	<c:when test="${type == 'select'}">
		<div class="form-group">
			<dl>
				<dt class="control-label col-sm-2">
					<c:if test="${not withoutLabel}">
						<label for="id-${field.name}"><fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if></label>:
					</c:if>
				</dt>
				<dd class="col-sm-4">
					<select class="form-control" name="${field.name}" id="id-${field.name}">
						<c:forEach var="item" items="${items}">
							<option value="${item.key}"<c:if test="${field.value == item.key}"> selected="selected"</c:if>><fmt:message key="${item.value}"/></option>
						</c:forEach>
					</select>
					<c:forEach var="message" items="${fieldMsgs[field.name]}">
						<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
					</c:forEach>
				</dd>
			</dl>
		</div>
	</c:when>
	<c:otherwise>
		<%-- text, password, hidden, file, textarea: --%>
		<div class="form-group">
			<dl>
				<dt class="control-label col-sm-2">
					<c:if test="${not withoutLabel && type != 'hidden'}">
						<label for="id-${field.name}<c:if test="${multi}">[]</c:if>"><fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if></label>:
					</c:if>
				</dt>
				<dd class="col-sm-4">
					<c:choose>
						<c:when test="${type == 'textarea'}">
							<textarea class="form-control<c:if test="${not empty inputCss}"> ${inputCss}</c:if>" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>">${field.value}</textarea>
						</c:when>
						<c:otherwise>
							<input class="<c:if test="${type != 'file' && type != 'hidden'}">form-control</c:if><c:if test="${not empty inputCss}"> ${inputCss}</c:if>" type="${type}" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>" value="${field.value}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${not empty postfix}">${postfix}</c:if>
					<c:forEach var="message" items="${fieldMsgs[field.name]}">
						<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
					</c:forEach>
				</dd>
			</dl>
		</div>
	</c:otherwise>
</c:choose>
</fmt:bundle>