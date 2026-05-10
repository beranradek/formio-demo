/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.demo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;

import net.formio.demo.controller.AbstractBaseController;
import net.formio.demo.filter.StaticResourceCacheFilter;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
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

public class WebAppContextBuilder {

	private final String appDisplayName;

	public WebAppContextBuilder(String appDisplayName) {
		this.appDisplayName = appDisplayName;
	}

	public String getAppDisplayName() {
		return appDisplayName;
	}

	public WebAppContext build() {
		WebAppContext context = new WebAppContext();
		context.setAttribute("javax.servlet.context.tempdir",
			new File(System.getProperty("java.io.tmpdir"), "jetty-jsp-scratch"));
		// Wrap classloader in URLClassLoader — required for JSP on Java 9+
		// (fixes ClassCastException: AppClassLoader cannot be cast to URLClassLoader)
		context.setClassLoader(new URLClassLoader(new URL[0],
			Thread.currentThread().getContextClassLoader()));
		context.setContextPath("/");

		String baseUrl = getBaseUrl();
		context.setResourceBase(baseUrl);
		context.setDescriptor(WEB_APP_ROOT);
		context.setConfigurations(new Configuration[] {
			new AnnotationConfiguration(), new WebXmlConfiguration(),
			new WebInfConfiguration(),
			new PlusConfiguration(), new MetaInfConfiguration(),
			new FragmentConfiguration(), new EnvConfiguration() });
		// Required for JSTL TLD discovery from taglibs jars on classpath
		context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
			".*/[^/]*taglibs.*\\.jar$|.*/[^/]*jstl.*\\.jar$");
		context.setParentLoaderPriority(true);
		context.setConfigurationDiscovered(true);

		configureApplication(context);
		return context;
	}

	private void configureApplication(WebAppContext context) {
		// Servlet filters
		FilterHolder staticCacheFilter = new FilterHolder(StaticResourceCacheFilter.class);
		context.addFilter(staticCacheFilter, "/bootstrap/*,/javascripts/*,/stylesheets/*",
			EnumSet.of(DispatcherType.REQUEST));

		// JSP servlet — must be named "jsp" per Servlet spec for includes/forwards to work
		ServletHolder jspServlet = new ServletHolder("jsp", JettyJspServlet.class);
		jspServlet.setInitParameter("fork", "false");
		jspServlet.setInitParameter("xpoweredBy", "false");
		jspServlet.setInitParameter("compilerTargetVM", "11");
		jspServlet.setInitParameter("compilerSourceVM", "11");
		jspServlet.setInitOrder(0);
		context.addServlet(jspServlet, "*.jsp");

		// Required for JSP EL expressions
		context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());

		// Scan and register annotated servlets (not under WEB-INF due to Heroku deployment)
		Reflections reflections = new Reflections(AbstractBaseController.class.getPackage().getName());
		Set<Class<?>> annotatedServlets = reflections.getTypesAnnotatedWith(WebServlet.class);
		registerServlets(context, annotatedServlets);

		// Default servlet for static resources — always registered last
		ServletHolder defaultJettyServlet = new ServletHolder("default", DefaultServlet.class);
		defaultJettyServlet.setInitParameter("acceptRanges", "true");
		defaultJettyServlet.setInitParameter("cacheControl", "max-age=604800,public");
		defaultJettyServlet.setInitOrder(1);
		context.addServlet(defaultJettyServlet, "/");

		context.setDisplayName(appDisplayName);
		context.setWelcomeFiles(new String[] { "redirect.jsp" });
		context.setErrorHandler(createErrorHandler());
		// getSessionManager() was removed in Jetty 9.4; timeout set directly on SessionHandler
		context.getSessionHandler().setMaxInactiveInterval(30 * 60);
	}

	private ErrorHandler createErrorHandler() {
		ErrorPageErrorHandler err = new ErrorPageErrorHandler();
		err.addErrorPage(404, "/WEB-INF/jsp/error_pages/missing.jsp");
		err.addErrorPage(401, "/WEB-INF/jsp/error_pages/unauthorized.jsp");
		err.addErrorPage(403, "/WEB-INF/jsp/error_pages/unauthorized.jsp");
		err.addErrorPage(RuntimeException.class, "/WEB-INF/jsp/error_pages/error.jsp");
		return err;
	}

	private String getBaseUrl() {
		URL webInfUrl = HttpServer.class.getClassLoader().getResource(WEB_APP_ROOT);
		if (webInfUrl == null) {
			throw new RuntimeException("Failed to find web application root: " + WEB_APP_ROOT);
		}
		return webInfUrl.toExternalForm();
	}

	private void registerServlets(ServletContextHandler context, Collection<Class<?>> types)
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

	private static final String WEB_APP_ROOT = "webapp";
}
