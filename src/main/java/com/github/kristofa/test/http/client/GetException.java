package com.github.kristofa.test.http.client;

/**
 * Exception that indicates that a HTTP GET request failed.
 * 
 * @author kristof
 */
public class GetException extends HttpRequestException {

    private static final long serialVersionUID = 6224289105700218852L;

    /**
     * Create a new exception instance.
     * 
     * @param cause Causing exception.
     */
    public GetException(final Throwable cause) {
        super(cause);
    }

}
