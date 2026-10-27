package exception;

public class InvalidTransactionException extends IllegalArgumentException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
