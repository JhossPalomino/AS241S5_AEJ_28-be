package vallegrande.exception;

public class YoutubeApiProcessingException extends RuntimeException {
    public YoutubeApiProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}