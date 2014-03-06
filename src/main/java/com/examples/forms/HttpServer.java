package com.examples.forms;

import java.io.File;
import java.net.URL;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Main application (embedded Jetty container).
 * @author Radek Beran
 */
public class HttpServer {
	public static final String WEB_APP_ROOT = "webapp"; // that folder has to be just somewhere in classpath
	
	public static void main(String[] args) throws Exception {
		// add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
		// the initialization phase of your application
		// SLF4JBridgeHandler.install();
		
		String portStr = System.getenv("PORT");
		if (portStr == null || portStr.isEmpty()) throw new IllegalStateException("Port not specified, please set environment variable PORT");
		
		ServletHolder jspServletHolder = new ServletHolder("jspServlet", new JspServlet());
		// these two lines are not strictly required - they will keep classes generated from JSP in "${javax.servlet.context.tempdir}/views/generated"
		jspServletHolder.setInitParameter("keepgenerated", "true");
		jspServletHolder.setInitParameter("scratchDir", "views/generated");
		
		WebAppContext context = new WebAppContext(); // ServletContextHandler.SESSIONS
        context.setAttribute("javax.servlet.context.tempdir", new File("../tmp/webapp"));
        // that classloader is required to set JSP classpath. Without it you will just get NPE
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setContextPath("/");
        context.addServlet(jspServletHolder, "*.jsp");
        // taken from web.xml:
        // context.setDisplayName("Formio demo");
        // context.setWelcomeFiles(new String[] { "index.html" });
        // context.addServlet(new ServletHolder(new IndexController()), "/");
        // context.addServlet(new ServletHolder(new AdvancedController()), "/advanced.html");
        context.setResourceBase(getBaseUrl());
        Server server = new Server(Integer.valueOf(portStr).intValue());
        server.setHandler(context);
        server.start();
        server.join();
    }
	
	private static String getBaseUrl() {
		URL webInfUrl = HttpServer.class.getClassLoader().getResource(WEB_APP_ROOT);
			if (webInfUrl == null) {
				throw new RuntimeException("Failed to find web application root: " + WEB_APP_ROOT);
		}
		return webInfUrl.toExternalForm();
	}
}
