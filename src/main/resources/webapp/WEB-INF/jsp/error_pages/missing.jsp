<%@page pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<h1>Požadovaný zdroj na serveru neexistuje</h1>
<hr/>
<p>Omlouváme se, požadovaná stránka nebo jiný zdroj informací na serveru neexistuje.</p>
<p>Pomoci Vám může následující:</p>
<ul>
    <li>Pokud jste zapsali adresu do prohlížeče ručně, zkontrolujte překlepy,</li>
    <li>vraťte se na <a href="javascript:history.go(-1)">předchozí stránku</a>,</li>
    <li>přejděte na <a href="<c:url value="/" />">úvodní stránku aplikace</a>, odkud můžete pokračovat dále,</li>
    <li>přejděte na <a href="/">úvodní stránku serveru</a>, odkud můžete pokračovat dále.</li>
</ul>

<p>Error 404 - page not found</p>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />
