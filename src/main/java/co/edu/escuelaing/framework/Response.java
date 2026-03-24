package co.edu.escuelaing.framework;

public class Response {
    private int status = 200;
    private String contentType = "text/plain";
    private final StringBuilder body = new StringBuilder();

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response appendBody(String text) {
        body.append(text);
        return this;
    }

    public String build() {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };

        return "HTTP/1.1 " + status + " " + statusText + "\r\n" +
               "Content-Type: " + contentType + "\r\n" +
               "Content-Length: " + body.length() + "\r\n" +
               "\r\n" +
               body.toString();
    }
}
