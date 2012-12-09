package com.github.kristofa.test.http.client;

/**
 * Exception that indicates that HttpRequest failed.
 * 
 * @author kristof
 */
public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -3214795946755533575L;

    /**
     * Creates a new instance.
     * 
     * @param throwable Causing exception.
     */
    public HttpRequestException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new instance.
     * 
     * @param message Exception message.
     */
    public HttpRequestException(final String message) {
        super(message);
    }

}
