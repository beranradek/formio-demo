<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<jsp:include page="header.jsp" />

<jsp:include page="navigation.jsp">
	<jsp:param name="activeTab" value="dynamic"/>
</jsp:include>

<c:if test="${not empty success}">
	<div class="alert alert-success">Data successfully saved.</div>
</c:if>
<form:globalErrors validationResult="${form.validationResult}"/>

<form action="<c:url value="/dynamic.html"/>" method="post" class="tdi form-horizontal" role="form">
	<c:out value="${formMarkup}" escapeXml="false" />
	<hr/>
	<c:out value="${addAccessoriesForm}" escapeXml="false" />
	<form:button name="addAccessories" label="Add" styleClass="btn btn-default"/>
	<hr/>
	<form:button name="save" label="Submit" styleClass="btn btn-default btn-primary"/>
</form>

<form class="form-inline">
  <div class="form-group">
    <label for="exampleInputName2">Name</label>
    <input type="text" class="form-control" id="exampleInputName2" placeholder="Jane Doe">
  </div>
  <div class="form-group">
    <label for="exampleInputEmail2">Email</label>
    <input type="email" class="form-control" id="exampleInputEmail2" placeholder="jane.doe@example.com">
  </div>
  <button type="submit" class="btn btn-default">Send invitation</button>
</form>

<jsp:include page="footer.jsp" />

