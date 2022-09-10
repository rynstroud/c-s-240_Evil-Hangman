package hangman;

public class InvalidGuessException extends Exception {
    public InvalidGuessException() {
        super();
    }

    public InvalidGuessException(String message) {
        super(message);
    }

    public InvalidGuessException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGuessException(Throwable cause) {
        super(cause);
    }

    protected InvalidGuessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
