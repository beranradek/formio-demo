# Stack Upgrade (Java 21 + Jetty 9.4 + jQuery 3.7.1) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Upgrade embedded Jetty stack from 9.3/Java8 to Jetty 9.4.57/Java21 so the app compiles, runs on Java 21, serves JSP/JSTL correctly, and deploys on Heroku — with jQuery 3.7.1 verified working in browser.

**Architecture:** Replace the ancient `jsp-2.1-glassfish` JSP library with Jetty 9.4's `apache-jsp`/`apache-jstl`, fix the classloader wrapping in `WebAppContextBuilder`, and update version pins throughout. Stays on Jetty 9.4.x to preserve the `javax.*` namespace (no code-wide import migration needed). The core fix for the `ClassCastException: AppClassLoader cannot be cast to URLClassLoader` is wrapping the context classloader in a `new URLClassLoader(new URL[0], parent)` before setting it on the WebAppContext.

**Tech Stack:**
- Java 21 (runtime on Heroku), compiled to Java 11 bytecode
- Gradle 8.10
- Jetty 9.4.57.v20241219 (last 9.4 release; javax.* namespace preserved)
- `org.eclipse.jetty:apache-jsp:9.4.57.v20241219` — replaces `org.mortbay.jetty:jsp-2.1-glassfish`
- `org.eclipse.jetty:apache-jstl:9.4.57.v20241219` — replaces `javax.servlet:jstl:1.2`
- `org.eclipse.jetty.jsp.JettyJspServlet` — replaces `org.apache.jasper.servlet.JspServlet`
- `org.hibernate:hibernate-validator:6.2.5.Final` — last `javax.validation` series
- SLF4J 2.0.16 + Logback 1.5.15
- Reflections 0.10.2 (Java 21-compatible classpath scanner)

---

### Task 1: Update `build.gradle`

**Files:**
- Modify: `build.gradle`

- [ ] **Step 1: Replace `build.gradle` with updated dependencies**

```groovy
plugins {
  id 'java'
  id 'application'
}

application {
    mainClass = 'net.formio.demo.HttpServer'
    applicationDefaultJvmArgs = [
        '--add-opens', 'java.base/java.lang=ALL-UNNAMED',
        '--add-opens', 'java.base/java.io=ALL-UNNAMED',
        '--add-opens', 'java.base/java.util=ALL-UNNAMED',
        '--add-opens', 'java.base/sun.nio.ch=ALL-UNNAMED'
    ]
}

applicationName = "formio-demo"
mainClassName = "net.formio.demo.HttpServer"

version = '1.4'
group = 'net.formio.demo'

sourceCompatibility = 11
targetCompatibility = 11

repositories {
    mavenLocal()
    mavenCentral()
}

jar {
    manifest {
        attributes(
            'Main-Class': 'net.formio.demo.HttpServer'
        )
    }
}

dependencies {
    def slf4jVersion = '2.0.16'
    def logbackVersion = '1.5.15'
    def jettyVersion = '9.4.57.v20241219'
    def formioVersion = '1.5.1'

    implementation "com.google.guava:guava:32.1.3-jre"
    implementation "org.slf4j:slf4j-api:${slf4jVersion}"
    implementation "org.eclipse.jetty:jetty-server:${jettyVersion}"
    implementation "org.eclipse.jetty:jetty-webapp:${jettyVersion}"
    implementation "org.eclipse.jetty:jetty-servlet:${jettyVersion}"

    // Annotation scanning and JNDI support
    implementation "org.eclipse.jetty:jetty-plus:${jettyVersion}"
    implementation "org.eclipse.jetty:jetty-annotations:${jettyVersion}"
    implementation "org.eclipse.jetty:jetty-jndi:${jettyVersion}"

    // JSP support — replaces org.mortbay.jetty:jsp-2.1-glassfish (fixes Java 9+ ClassCastException)
    implementation "org.eclipse.jetty:apache-jsp:${jettyVersion}"

    // JSTL support — replaces javax.servlet:jstl:1.2
    implementation "org.eclipse.jetty:apache-jstl:${jettyVersion}"

    // Servlet API 3.1 (provided by Jetty at runtime; compile-only here)
    compileOnly "javax.servlet:javax.servlet-api:3.1.0"

    // Java 21-compatible classpath scanner (replaces 0.9.10)
    implementation "org.reflections:reflections:0.10.2"

    // Bean Validation — last series using javax.validation (7.x+ requires jakarta.*)
    implementation "org.hibernate:hibernate-validator:6.2.5.Final"
    implementation "org.glassfish:javax.el:3.0.0"

    implementation "net.formio:formio:${formioVersion}"

    testImplementation 'junit:junit:4.13.2'

    runtimeOnly "ch.qos.logback:logback-classic:${logbackVersion}"
    runtimeOnly "ch.qos.logback:logback-core:${logbackVersion}"
    runtimeOnly "org.slf4j:jcl-over-slf4j:${slf4jVersion}"
    runtimeOnly "org.slf4j:jul-to-slf4j:${slf4jVersion}"
}

// Task stage for Heroku deployment
task stage(dependsOn: ['clean', 'installDist'])
```

- [ ] **Step 2: Verify it compiles (dependencies resolve)**

```bash
bash gradlew dependencies --configuration compileClasspath 2>&1 | grep -E "apache-jsp|apache-jstl|hibernate-validator|reflections" | head -10
```

Expected: lines showing `org.eclipse.jetty:apache-jsp:9.4.57.v20241219`, `apache-jstl`, `hibernate-validator:6.2.5.Final`.

---

### Task 2: Fix `WebAppContextBuilder.java` for Java 21 + Jetty 9.4

**Files:**
- Modify: `src/main/java/net/formio/demo/WebAppContextBuilder.java`

- [ ] **Step 1: Replace the file with Java-21-compatible version**

Key changes vs original:
1. `context.setClassLoader(new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader()))` — wraps classloader to fix `ClassCastException`
2. `JettyJspServlet` instead of `JspServlet` — correct class for `apache-jsp`
3. JSP servlet named `"jsp"` (spec-required name for includes/forwards)
4. Add `ContainerIncludeJarPattern` so JSTL TLDs are discovered from jars
5. Add `InstanceManager` for JSP EL expression support
6. `context.getSessionHandler().setMaxInactiveInterval(...)` — `getSessionManager()` was removed in Jetty 9.4

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
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
        // getSessionManager() was removed in Jetty 9.4; timeout is set directly on SessionHandler
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
```

- [ ] **Step 2: Compile**

```bash
bash gradlew compileJava 2>&1 | tail -20
```

Expected: `BUILD SUCCESSFUL`

---

### Task 3: Update Heroku and Gradle version pins

**Files:**
- Modify: `system.properties`
- Modify: `gradle/wrapper/gradle-wrapper.properties`

- [ ] **Step 1: Update `system.properties` to Java 21**

Replace content with:
```
java.runtime.version=21
```

- [ ] **Step 2: Update Gradle wrapper to 8.10**

In `gradle/wrapper/gradle-wrapper.properties`, change:
```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10-bin.zip
```

- [ ] **Step 3: Re-run the wrapper to download Gradle 8.10**

```bash
bash gradlew --version 2>&1 | grep "Gradle"
```

Expected: `Gradle 8.10`

---

### Task 4: Full build and smoke test

- [ ] **Step 1: Full clean build**

```bash
bash gradlew clean build 2>&1 | tail -15
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Run the server, verify it starts without 503**

```bash
bash gradlew run &
sleep 8
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/simple.html
```

Expected: `200` (not `503`)

- [ ] **Step 3: Kill the server after verifying**

```bash
kill $(lsof -ti:8080) 2>/dev/null
```

---

### Task 5: Playwright end-to-end tests (simple + ajax forms + jQuery version)

- [ ] **Step 1: Start server in background**

```bash
bash gradlew run &
sleep 10
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/simple.html
```

Expected: `200`

- [ ] **Step 2: Run Playwright tests**

Script at `/tmp/test_formio_e2e.py`:

```python
from playwright.sync_api import sync_playwright
import sys

results = []

def check(label, condition, detail=""):
    status = "PASS" if condition else "FAIL"
    msg = f"[{status}] {label}"
    if detail:
        msg += f" — {detail}"
    results.append((status, msg))
    print(msg)

with sync_playwright() as p:
    browser = p.chromium.launch(headless=True)
    page = browser.new_page()

    console_errors = []
    page.on("console", lambda msg: console_errors.append(msg.text) if msg.type == "error" else None)
    page.on("pageerror", lambda err: console_errors.append(f"PAGE ERROR: {err}"))

    # --- jQuery version check ---
    page.goto("http://localhost:8080/simple.html")
    page.wait_for_load_state("networkidle")
    page.screenshot(path="/tmp/simple_form.png", full_page=True)
    jquery_version = page.evaluate("() => typeof jQuery !== 'undefined' ? jQuery.fn.jquery : 'NOT LOADED'")
    check("jQuery 3.7.1 loaded", jquery_version == "3.7.1", f"found={jquery_version}")
    check("No JS errors on simple form load", len(console_errors) == 0,
          str(console_errors) if console_errors else "")

    # --- Simple form: render ---
    h1 = page.locator("h1,h2").first.inner_text()
    check("Simple form heading visible", len(h1) > 0, f"heading='{h1}'")
    inputs = page.locator("input[type=text], input[type=email], input[type=number], select, textarea").all()
    check("Simple form has input fields", len(inputs) > 0, f"count={len(inputs)}")

    # --- Simple form: submit with data ---
    console_errors.clear()
    name_input = page.locator("input[name*='firstName'], input[name*='first'], input[name*='name']").first
    if name_input.count() > 0:
        name_input.fill("TestUser")
    submit = page.locator("input[type=submit], button[type=submit]").first
    submit.click()
    page.wait_for_load_state("networkidle")
    page.screenshot(path="/tmp/simple_form_submitted.png", full_page=True)
    check("Simple form submit — no JS errors", len(console_errors) == 0,
          str(console_errors) if console_errors else "")
    check("Simple form submit — no server error", "503" not in page.content() and "500" not in page.title(),
          f"title='{page.title()}'")

    # --- Ajax form ---
    console_errors.clear()
    page.goto("http://localhost:8080/ajax.html")
    page.wait_for_load_state("networkidle")
    page.screenshot(path="/tmp/ajax_form.png", full_page=True)
    jquery_version2 = page.evaluate("() => typeof jQuery !== 'undefined' ? jQuery.fn.jquery : 'NOT LOADED'")
    check("jQuery 3.7.1 on ajax form", jquery_version2 == "3.7.1", f"found={jquery_version2}")
    check("No JS errors on ajax form load", len(console_errors) == 0,
          str(console_errors) if console_errors else "")
    ajax_inputs = page.locator("input[type=text], input[type=email], input[type=number], select, textarea").all()
    check("Ajax form has input fields", len(ajax_inputs) > 0, f"count={len(ajax_inputs)}")

    # --- Ajax form: submit ---
    console_errors.clear()
    ajax_submit = page.locator("input[type=submit], button[type=submit]").first
    ajax_submit.click()
    page.wait_for_timeout(2000)
    page.screenshot(path="/tmp/ajax_form_submitted.png", full_page=True)
    check("Ajax form submit — no JS errors", len(console_errors) == 0,
          str(console_errors) if console_errors else "")

    browser.close()

print("\n--- Summary ---")
passed = sum(1 for s, _ in results if s == "PASS")
failed = sum(1 for s, _ in results if s == "FAIL")
print(f"PASSED: {passed}, FAILED: {failed}")
sys.exit(0 if failed == 0 else 1)
```

Run: `python /tmp/test_formio_e2e.py`

Expected: all PASS

- [ ] **Step 3: Kill server**

```bash
kill $(lsof -ti:8080) 2>/dev/null
```

---

### Task 6: Commit and push

- [ ] **Step 1: Stage all changed files**

```bash
git add build.gradle gradle/wrapper/gradle-wrapper.properties system.properties \
    src/main/java/net/formio/demo/WebAppContextBuilder.java \
    docs/superpowers/plans/2026-05-10-stack-upgrade-java21-jetty94.md
```

- [ ] **Step 2: Commit**

```bash
git commit -m "Upgrade stack to Jetty 9.4.57 / Java 21 for Java 9+ compatibility

- Replace jsp-2.1-glassfish with apache-jsp/apache-jstl 9.4.57.v20241219
- Fix ClassCastException: wrap context classloader in URLClassLoader
- Switch JSP servlet to JettyJspServlet (spec-correct name 'jsp')
- Add ContainerIncludeJarPattern for JSTL TLD discovery
- Add InstanceManager for JSP EL support
- Fix removed getSessionManager() API (use SessionHandler directly)
- Upgrade hibernate-validator to 6.2.5.Final (javax.validation)
- Upgrade SLF4J 2.0.16 + Logback 1.5.15
- Upgrade Reflections to 0.10.2 (Java 21-compatible)
- Update Gradle wrapper to 8.10, system.properties to java 21
- jQuery 3.7.1 verified working in browser via Playwright tests

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

- [ ] **Step 3: Push**

```bash
git push origin f/1-cross-site-scripting-xss-issue-in-jquery
```
