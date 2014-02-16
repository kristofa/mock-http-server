package com.github.kristofa.test.http;

public class DefaultHttpResponseProxy implements HttpResponseProxy {

    private final HttpResponse response;
    private boolean requested = false;

    public DefaultHttpResponseProxy(final HttpResponse response) {
        this.response = response;
    }

    @Override
    public boolean alreadyRequested() {
        return requested;
    }

    @Override
    public HttpResponse getResponse() {
        requested = true;
        return response;
    }

}
