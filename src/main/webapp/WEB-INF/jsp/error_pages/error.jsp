<%@page pageEncoding="UTF-8" %>
<%@page isErrorPage="true" %>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<h1>Neočekávaná chyba</h1>

<p>Omlouváme se, v aplikaci nastala neočekávaná chyba. Prosím kontaktujte správce aplikace.
Popis chyby: ${pageContext.exception.message}</p>

<p>Pomoci Vám může následující:</p>
<ul>
    <li>Vraťte se na <a href="javascript:history.go(-1)">předchozí stránku</a>,</li>
    <li>přejděte na <a href="<c:url value="/" />">úvodní stránku aplikace</a>, odkud můžete pokračovat dále,</li>
    <li>přejděte na <a href="/">úvodní stránku serveru</a>, odkud můžete pokračovat dále.</li>
</ul>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />