package co.edu.escuelaing.framework;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private static final Map<String, RouteHandler> postRoutes = new HashMap<>();

    public static void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    public static void post(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
    }

    public static RouteHandler getHandler(String method, String path) {
        if ("GET".equalsIgnoreCase(method)) return getRoutes.get(path);
        if ("POST".equalsIgnoreCase(method)) return postRoutes.get(path);
        return null;
    }
}
