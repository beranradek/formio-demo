<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="name" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="label" required="true" rtexprvalue="true" type="String" %>
<%@attribute name="styleClass" required="false" rtexprvalue="true" type="String" %>

<div class="form-group">
	<div class="col-sm-offset-2 col-sm-4">
		<button type="submit" name="${name}" class="${styleClass}">${label}</button>
	</div>
</div>