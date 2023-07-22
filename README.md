# Formio Demo

Demo of simple/advanced/AJAX Java form using [Formio library](http://www.formio.net "Formio library").

## Live

Live on http://formio-demo.herokuapp.com/

## Gradle build

* Build for Heroku: gradle stage
* Import the project into Eclipse IDE: gradle cleanEclipse eclipse. In the Eclipse, import the generated project structure into the workspace. 

## Run the web application

Web application runs on embedded Jetty servlet container.
See the execution script generated by gradle stage task.

It is recommended to use [HotswapAgent](http://www.hotswapagent.org/) to
dynamically reload classes during development.

Running from Eclipse - in Debug Configurations set these parameters:

* Project: formio-demo (browse to locate the project)
* Main class: net.formio.demo.HttpServer
* VM arguments: -XXaltjvm=dcevm -javaagent:c:/java/hotswap-agent.jar when you are using HotswapAgent
* JRE: Choose correct JDK that includes HotswapAgent installed
* Environment: Add PORT parameter with some value (e.g. 8090) if you don't want to use the default 8080 port of embedded Jetty container. The PORT environment variable is recognized by main class HttpServer. 
* Display in favorites menu: Debug, ... 
* Run the debug configuration and navigate to http://localhost:<your port>/ in the browser to see the running application
