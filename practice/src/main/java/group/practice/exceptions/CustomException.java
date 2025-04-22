package group.practice.exceptions;

public abstract class CustomException extends RuntimeException {

    private final ExceptionStatus status;

    public CustomException(ExceptionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return status.getMessage();
    }

    public int getStatusCode() {
        return status.getStatusCode();
    }

    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }
}
