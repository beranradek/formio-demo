<?xml version="1.0"?>
<%@page pageEncoding="UTF-8" contentType="text/xml; charset=UTF-8" %>
<%@include file="../include.jsp" %>
<%@taglib prefix="userTags" tagdir="/WEB-INF/tags/users" %>
<response>
    <status>OK</status>
    <%-- redirect with success parameter: --%>
    <redirect href="<c:url value="/ajax.html?success=1"/>"></redirect>
</response>