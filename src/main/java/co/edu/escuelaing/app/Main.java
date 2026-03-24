package co.edu.escuelaing.app;

import co.edu.escuelaing.framework.HttpServer;
import co.edu.escuelaing.framework.Router;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        // Define routes
        Router.get("/greeting", (req, res) -> {
            String name = req.getQueryParam("name");
            if (name == null) name = "World";
            
            JSONObject json = new JSONObject();
            json.put("message", "Hello, " + name + "!");
            
            res.setContentType("application/json")
               .appendBody(json.toString());
        });

        Router.get("/health", (req, res) -> {
            res.setContentType("application/json")
               .appendBody("{\"status\":\"UP\"}");
        });

        // Get port from ENV or default
        int port = getPort();
        HttpServer server = new HttpServer(port);
        server.start();
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000;
    }
}
