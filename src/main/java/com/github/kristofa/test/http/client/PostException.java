package com.github.kristofa.test.http.client;

/**
 * Exception that indicates that a HTTP POST request failed.
 * 
 * @author kristof
 */
public class PostException extends HttpRequestException {

    private static final long serialVersionUID = 5504826551802767885L;

    /**
     * Creates a new instance.
     * 
     * @param cause Causing exception.
     */
    public PostException(final Throwable cause) {
        super(cause);
    }

}
