<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="myfn" uri="/WEB-INF/custom-functions.tld" %>
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
<%@attribute name="localize" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@attribute name="itemId" required="false" rtexprvalue="true" type="java.lang.Object" %>
<%@attribute name="itemLabel" required="false" rtexprvalue="true" type="java.lang.Object" %>

<fmt:bundle basename="${bundleName}">
<c:set var="field" value="${fields[fieldName]}" />
<c:choose>
	<c:when test="${not empty itemId && (type == 'checkbox' || type == 'radio')}">
		<c:set var="field" value="${fields[fieldName]}" />
		<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[field.name])}"/>
		<div class="form-group compact<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
			<div class="col-sm-offset-2 col-sm-4">
				<div class="checkbox">
					<c:if test="${not withoutLabel}">
						<label>
					</c:if>
					<input type="${type}" name="${field.name}[]" id="id-${field.name}-${itemId}"<c:if test="${myfn:contains(field.filledObjects, itemId)}"> checked="checked"</c:if> value="${itemId}" />
					<c:choose>
						<c:when test="${localize}"><fmt:message key="${itemLabel}"/></c:when>
						<c:otherwise>${itemLabel}</c:otherwise>
					</c:choose>
					<c:if test="${not withoutLabel}">
						</label>
					</c:if>
				</div>
			</div>
		</div>
	</c:when>
	<c:when test="${type == 'checkbox' || type == 'radio'}">
		<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[field.name])}"/>
		<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
			<div class="col-sm-offset-2 col-sm-4">
				<div class="checkbox">
					<c:if test="${not withoutLabel}">
						<label>
					</c:if>
					<input type="${type}" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>"<c:if test="${field.value}"> checked="checked"</c:if> value="1" />
					<c:if test="${not withoutLabel}">
						<fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if>
						</label>
					</c:if>
					<c:if test="${not multi}">
						<c:forEach var="message" items="${fieldMsgs[field.name]}">
							<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
	</c:when>
	<c:when test="${type == 'select'}">
		<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[field.name])}"/>
		<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
			<c:if test="${not withoutLabel}">
				<label class="control-label col-sm-2" for="id-${field.name}"><fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if>:</label>
			</c:if>
			<div class="col-sm-4">
				<select class="input-sm form-control" name="${field.name}" id="id-${field.name}">
					<c:forEach var="item" items="${items}">
						<option value="${item.key}"<c:if test="${field.value == item.key}"> selected="selected"</c:if>><fmt:message key="${item.value}"/></option>
					</c:forEach>
				</select>
				<c:forEach var="message" items="${fieldMsgs[field.name]}">
					<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
				</c:forEach>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<%-- text, password, hidden, file, textarea: --%>
		<c:set var="maxSev" value="${myfn:maxSeverity(fieldMsgs[field.name])}"/>
		<div class="form-group<c:if test="${not empty maxSev}"> has-${maxSev}</c:if>">
			<c:if test="${not withoutLabel && type != 'hidden'}">
				<label class="control-label col-sm-2" for="id-${field.name}<c:if test="${multi}">[]</c:if>"><fmt:message key="${field.labelKey}"/><c:if test="${field.required}">&nbsp;*</c:if>:</label>
			</c:if>
			<div class="<c:if test="${withoutLabel}">col-sm-offset-2 </c:if>col-sm-4">
				<c:choose>
					<c:when test="${type == 'textarea'}">
						<textarea class="input-sm form-control<c:if test="${not empty inputCss}"> ${inputCss}</c:if>" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>">${field.value}</textarea>
					</c:when>
					<c:otherwise>
						<input class="<c:if test="${type != 'file' && type != 'hidden'}">input-sm form-control</c:if><c:if test="${not empty inputCss}"> ${inputCss}</c:if>" type="${type}" name="${field.name}<c:if test="${multi}">[]</c:if>" id="id-${field.name}<c:if test="${multi}">[]</c:if>" value="${field.value}" />
					</c:otherwise>
				</c:choose>
				<c:if test="${not empty postfix}">${postfix}</c:if>
				<c:forEach var="message" items="${fieldMsgs[field.name]}">
					<div class="${message.severity.cssClass}"><c:out value="${message.text}" /></div>
				</c:forEach>
			</div>
		</div>
	</c:otherwise>
</c:choose>
</fmt:bundle>