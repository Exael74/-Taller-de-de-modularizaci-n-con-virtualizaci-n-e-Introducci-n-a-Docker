package co.edu.escuelaing.framework;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final String body;

    public Request(String method, String path, Map<String, String> queryParams, String body) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.body = body;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getQueryParam(String key) { return queryParams.get(key); }
    public String getBody() { return body; }

    public static Request parse(String rawRequest) {
        String[] lines = rawRequest.split("\r\n");
        if (lines.length == 0) return null;

        String[] firstLine = lines[0].split(" ");
        if (firstLine.length < 2) return null;

        String method = firstLine[0];
        String fullPath = firstLine[1];

        String path = fullPath;
        Map<String, String> queryParams = new HashMap<>();

        if (fullPath.contains("?")) {
            String[] parts = fullPath.split("\\?");
            path = parts[0];
            if (parts.length > 1) {
                String[] pairs = parts[1].split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    queryParams.put(kv[0], kv.length > 1 ? kv[1] : "");
                }
            }
        }

        // Simplistic body parsing for now (not used in this workshop)
        StringBuilder body = new StringBuilder();
        boolean isBody = false;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                isBody = true;
                continue;
            }
            if (isBody) {
                body.append(lines[i]);
            }
        }

        return new Request(method, path, queryParams, body.toString());
    }
}
