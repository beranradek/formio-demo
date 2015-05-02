<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<ul class="nav nav-tabs">
  <li<c:if test="${param.activeTab == 'simple'}"> class="active"</c:if>><a href="<c:choose><c:when test="${param.activeTab == 'simple'}">#</c:when><c:otherwise><c:url value="/simple.html"/></c:otherwise></c:choose>">Simple</a></li>
  <li<c:if test="${param.activeTab == 'advanced'}"> class="active"</c:if>><a href="<c:choose><c:when test="${param.activeTab == 'advanced'}">#</c:when><c:otherwise><c:url value="/advanced.html"/></c:otherwise></c:choose>">Advanced</a></li>
  <li<c:if test="${param.activeTab == 'ajax'}"> class="active"</c:if>><a href="<c:choose><c:when test="${param.activeTab == 'ajax'}">#</c:when><c:otherwise><c:url value="/ajax.html"/></c:otherwise></c:choose>">AJAX Form</a></li>
  <li<c:if test="${param.activeTab == 'dynamic'}"> class="active"</c:if>><a href="<c:choose><c:when test="${param.activeTab == 'dynamic'}">#</c:when><c:otherwise><c:url value="/dynamic.html"/></c:otherwise></c:choose>">Dynamic</a></li>
</ul>