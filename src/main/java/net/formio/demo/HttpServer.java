package net.formio.demo;

import java.io.File;
import java.net.URL;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

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
		
		// Required JSP support for Jetty
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
        // context.setWelcomeFiles(new String[] { "simple.html" });
        
        String baseUrl = getBaseUrl();
        context.setResourceBase(baseUrl);
        context.setDescriptor(baseUrl + "/WEB-INF/web.xml");
        // context.setDescriptor(WEB_APP_ROOT);
        context.setConfigurations(new Configuration[] {
			new AnnotationConfiguration(), new WebXmlConfiguration(),
			new WebInfConfiguration(),
			new PlusConfiguration(), new MetaInfConfiguration(),
			// Enable parsing of jndi-related parts of web.xml and jetty-env.xml:
			new FragmentConfiguration(), new EnvConfiguration() });
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/classes/.*|.*/controller/.*");
        context.setParentLoaderPriority(true);
        context.setConfigurationDiscovered(true);
        
        Server server = new Server(getServerPort());
        server.setHandler(context);
     
        server.start();
        // server.dump(System.err);
        server.join();
    }

	private static int getServerPort() {
		String portStr = System.getenv("PORT");
		if (portStr == null || portStr.isEmpty()) portStr = "8080";
		return Integer.valueOf(portStr).intValue();
	}
	
	private static String getBaseUrl() {
		URL webInfUrl = HttpServer.class.getClassLoader().getResource(WEB_APP_ROOT);
			if (webInfUrl == null) {
				throw new RuntimeException("Failed to find web application root: " + WEB_APP_ROOT);
		}
		return webInfUrl.toExternalForm();
	}
}
