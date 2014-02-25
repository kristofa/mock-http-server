package com.github.kristofa.test.http;

/**
 * Exception that indicates that an unexpected value for content was submitted.
 * 
 * @see ContentMatcher
 * @author kristof
 */
public class UnexpectedContentException extends Exception {

    private static final long serialVersionUID = -2327449873409595237L;

    /**
     * Create a new exception instance.
     * 
     * @param message Exception message.
     */
    public UnexpectedContentException(final String message) {
        super(message);
    }

    /**
     * Create a new exception instance.
     * 
     * @param cause Causing exception.
     */
    public UnexpectedContentException(final Throwable cause) {
        super(cause);
    }

}
