package foo.util;

public class CustomExceptionMessage {
    private String message;
    private String originalException;
    private int status;

    public CustomExceptionMessage(String message, Exception originalException, int status) {
        this.message = message;
        this.originalException = originalException.toString();
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOriginalException() {
        return originalException;
    }

    public void setOriginalException(String originalException) {
        this.originalException = originalException;
    }
}
