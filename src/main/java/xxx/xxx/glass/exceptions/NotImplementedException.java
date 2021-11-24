package xxx.xxx.glass.exceptions;

/**
 * An exception that indicates that the method that threw it isn't (fully) implemented (yet).
 */

public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {

    }

    public NotImplementedException(final String message) {

        super(message);

    }

}
