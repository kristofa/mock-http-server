package com.github.kristofa.test.http.client;

/**
 * Exception that indicates that a HTTP PUT request failed.
 * 
 * @author kristof
 */
public class PutException extends HttpRequestException {

    private static final long serialVersionUID = -3599258268641168904L;

    /**
     * Create a new exception instance.
     * 
     * @param cause Causing exception.
     */
    public PutException(final Throwable cause) {
        super(cause);
    }

}
