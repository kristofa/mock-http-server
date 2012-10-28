package com.harlap.test.http.client;

public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -3214795946755533575L;

    public HttpRequestException(final Throwable throwable) {
        super(throwable);
    }

    public HttpRequestException(final String message) {
        super(message);
    }

}
