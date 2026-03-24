package co.edu.escuelaing.framework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private final int port;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running = true;
    private static final String STATIC_DIR = "src/main/resources/public";

    public HttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            // Graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) System.err.println("Accept error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
        }
    }

    public void stop() {
        System.out.println("Shutting down server...");
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            System.out.println("Server shutdown complete.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;

            StringBuilder rawRequest = new StringBuilder(requestLine).append("\r\n");
            String line;
            while (in.ready() && (line = in.readLine()) != null && !line.isEmpty()) {
                rawRequest.append(line).append("\r\n");
            }

            Request req = Request.parse(rawRequest.toString());
            Response res = new Response();

            if (req != null) {
                RouteHandler handler = Router.getHandler(req.getMethod(), req.getPath());
                if (handler != null) {
                    handler.handle(req, res);
                } else if (!handleStaticFile(req, res)) {
                    res.setStatus(404).appendBody("Not Found");
                }
            } else {
                res.setStatus(400).appendBody("Bad Request");
            }

            out.print(res.build());
            out.flush();

        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private boolean handleStaticFile(Request req, Response res) {
        String pathStr = req.getPath().equals("/") ? "/index.html" : req.getPath();
        Path filePath = Paths.get(STATIC_DIR, pathStr);

        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            try {
                String content = Files.readString(filePath);
                String contentType = "text/html";
                if (pathStr.endsWith(".css")) contentType = "text/css";
                else if (pathStr.endsWith(".js")) contentType = "application/javascript";
                
                res.setContentType(contentType).appendBody(content);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
