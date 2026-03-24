package co.edu.escuelaing.framework;

@FunctionalInterface
public interface RouteHandler {
    void handle(Request req, Response res);
}
