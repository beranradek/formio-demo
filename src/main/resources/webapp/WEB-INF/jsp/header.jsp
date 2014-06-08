<%@page pageEncoding="UTF-8" %>
<%@include file="include.jsp" %>
<!DOCTYPE html>
<html class="no-js">
<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="author" content="Radek Beran"/>
    <meta name="copyright" content="Radek Beran"/>
    <meta name="description" content="Forms demo"/>
    <meta name="keywords" content="formio,forms"/>
	<title>Formio demo</title>
	
	<!-- Twitter Bootstrap is used (http://getbootstrap.com/getting-started/) -->
	<!-- Bootstrap core CSS -->
	<link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css">
	<!-- JQuery UI for datepicker -->
	<link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/stylesheets/jquery-ui-1.10.3.custom.min.css">
	<!-- Custom CSS -->
	<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/stylesheets/app.css">
    
    <!-- JQuery - Bootstrap's dependency -->
    <script src="${pageContext.request.contextPath}/javascripts/jquery-1.7.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/javascripts/jquery-ui.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/javascripts/jquery-ui-i18n.js"></script>
    <!-- Bootstrap core JS -->
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
	
	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="${pageContext.request.contextPath}/bootstrap/js/html5shiv.js"></script>
      <script src="${pageContext.request.contextPath}/bootstrap/js/respond.min.js"></script>
      <script>
            $(document).ready(function(){
            
                $('[placeholder]').focus(function() {
                    var input = $(this);
                    if (input.val() == input.attr('placeholder')) {
                        input.val('');
                        input.removeClass('placeholder');
                    }
                }).blur(function() {
                    var input = $(this);
                    if (input.val() == '' || input.val() == input.attr('placeholder')) {
                        input.addClass('placeholder');
                        input.val(input.attr('placeholder'));
                    }
                }).blur();
                $('[placeholder]').parents('form').submit(function() {
                    $(this).find('[placeholder]').each(function() {
                        var input = $(this);
                        if (input.val() == input.attr('placeholder')) {
                            input.val('');
                        }
                    })
                });
            });
        </script>
    <![endif]-->
</head>
<body>
<div class="container">
<div class="home-link"><a href="http://www.formio.net/">Formio Home</a></div>
<h1>Formio Demo</h1>

