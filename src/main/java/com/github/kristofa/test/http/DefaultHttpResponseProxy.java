package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;

public class DefaultHttpResponseProxy implements HttpResponseProxy {

    private final HttpResponse response;
    private boolean isConsumed = false;

    public DefaultHttpResponseProxy(final HttpResponse response) {
        Validate.notNull(response);
        this.response = response;
    }

    @Override
    public boolean consumed() {
        return isConsumed;
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public HttpResponse consume() {
        isConsumed = true;
        return response;
    }

}
