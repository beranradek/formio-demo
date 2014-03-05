<%@page pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp" />
<h1>Přístup zamítnut</h1>
<hr/>
<p>K provedení akce nemáte dostatečné oprávnění. Pomoci Vám může následující:</p>
<ul>
    <li>Zkuste se <a href="<c:url value="/login.html" />">přihlásit</a> do aplikace,</li>
    <li>vraťte se na <a href="javascript:history.go(-1)">předchozí stránku</a>,</li>
    <li>přejděte na <a href="<c:url value="/" />">úvodní stránku aplikace</a>, odkud můžete pokračovat dále,</li>
    <li>přejděte na <a href="/">úvodní stránku serveru</a>, odkud můžete pokračovat dále.</li>
</ul>

<jsp:include page="/WEB-INF/jsp/footer.jsp" />
