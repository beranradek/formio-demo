package net.formio.demo;

import org.eclipse.jetty.server.Server;

/**
 * Main entry point - HTTP server using embedded Jetty container.
 * @author Radek Beran
 */
public class HttpServer {
	
	public static void main(String[] args) throws Exception {
        Server server = new Server(getServerPort());
        server.setHandler(new WebAppContextBuilder("Formio demo").build());
     
        server.start();
        // server.dump(System.err);
        server.join();
    }
	
	private static int getServerPort() {
		String portStr = System.getenv("PORT");
		if (portStr == null || portStr.isEmpty()) portStr = "8080";
		return Integer.valueOf(portStr).intValue();
	}
	
}
