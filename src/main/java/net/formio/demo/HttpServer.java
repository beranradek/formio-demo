package net.formio.demo;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;

import net.formio.demo.controller.AbstractBaseController;
import net.formio.demo.filter.StaticResourceCacheFilter;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.reflections.Reflections;

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
		
		WebAppContext context = new WebAppContext(); // ServletContextHandler.SESSIONS
        context.setAttribute("javax.servlet.context.tempdir", new File("../tmp/webapp"));
        // that classloader is required to set JSP classpath. Without it you will just get NPE
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setContextPath("/");
        
        String baseUrl = getBaseUrl();
        context.setResourceBase(baseUrl);
        // context.setDescriptor(baseUrl + "/WEB-INF/web.xml");
        context.setDescriptor(WEB_APP_ROOT);
        context.setConfigurations(new Configuration[] {
			new AnnotationConfiguration(), new WebXmlConfiguration(),
			new WebInfConfiguration(),
			new PlusConfiguration(), new MetaInfConfiguration(),
			// Enable parsing of jndi-related parts of web.xml and jetty-env.xml:
			new FragmentConfiguration(), new EnvConfiguration() });
        // context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/classes/.*|.*/controller/.*");
        context.setParentLoaderPriority(true);
        context.setConfigurationDiscovered(true);
        
        configureApplication(context);
        
        Server server = new Server(getServerPort());
        server.setHandler(context);
     
        server.start();
        // server.dump(System.err);
        server.join();
    }

	/**
	 * Application specific configuration.
	 * @param context
	 */
	private static void configureApplication(WebAppContext context) {
		// Servlet filters
		FilterHolder staticCacheFilter = new FilterHolder(StaticResourceCacheFilter.class);
		context.addFilter(staticCacheFilter, "/bootstrap/*,/javascripts/*,/stylesheets/*", EnumSet.of(DispatcherType.REQUEST));
		
		// Required JSP support for Jetty
		ServletHolder jspServlet = new ServletHolder("jspServlet", new JspServlet());
		// these two lines are not strictly required - they will keep classes generated from JSP in "${javax.servlet.context.tempdir}/views/generated"
		jspServlet.setInitParameter("keepgenerated", "true");
		jspServlet.setInitParameter("scratchDir", "views/generated");
		context.addServlet(jspServlet, "*.jsp");
		
		// Our compiled servlets are not under WEB-INF/classes or WEB-INF/lib due to Heroku deployment,
        // so we need to scan annotated servlets ourselves:
        Reflections reflections = new Reflections(AbstractBaseController.class.getPackage().getName());
        Set<Class<?>> annotatedServlets = reflections.getTypesAnnotatedWith(WebServlet.class);
        registerServlets(context, annotatedServlets);
        
        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder defaultJettyServlet = new ServletHolder("default", DefaultServlet.class);
        defaultJettyServlet.setInitParameter("acceptRanges", "true");
        defaultJettyServlet.setInitParameter("cacheControl", "max-age=604800,public");
        defaultJettyServlet.setInitOrder(1);
        context.addServlet(defaultJettyServlet, "/");
        
        context.setDisplayName("Formio demo");
        context.setWelcomeFiles(new String[] { "redirect.jsp" });
        context.setErrorHandler(createErrorHandler());
        context.getSessionHandler().getSessionManager().setMaxInactiveInterval(30 * 60); // in seconds
	}
	
	private static ErrorHandler createErrorHandler() {
		ErrorPageErrorHandler err = new ErrorPageErrorHandler();
		err.addErrorPage(404, "/WEB-INF/jsp/error_pages/missing.jsp");
		err.addErrorPage(401, "/WEB-INF/jsp/error_pages/unauthorized.jsp");
		err.addErrorPage(403, "/WEB-INF/jsp/error_pages/unauthorized.jsp");
		err.addErrorPage(RuntimeException.class, "/WEB-INF/jsp/error_pages/error.jsp");
		return err;
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
	
	private static void registerServlets(ServletContextHandler context, Collection<Class<?>> types) 
		throws IllegalArgumentException, SecurityException {
		for (Class<?> type : types) {
			WebServlet servletAnnot = type.getAnnotation(WebServlet.class);
			if (servletAnnot != null) {
				String[] urlPatterns = null;
				if (servletAnnot.urlPatterns() != null && servletAnnot.urlPatterns().length > 0) {
					urlPatterns = servletAnnot.urlPatterns(); 
				} else {
					urlPatterns = servletAnnot.value();
				}
				if (urlPatterns != null && urlPatterns.length > 0) {
					for (String pattern : urlPatterns) {
						context.addServlet(type.getName(), pattern);
					}
				}
			}
		}
	}
}
