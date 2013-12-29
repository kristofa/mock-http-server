package com.github.kristofa.test.http.client;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpProcessor;

/**
 * This is a {@link CustomHttpClient} that removes the default interceptors of {@link DefaultHttpClient}. Reason is that we
 * don't want to modify the original Http requests and responses.
 * 
 * @author kristof
 */
class CustomHttpClient extends DefaultHttpClient {

    @Override
    protected BasicHttpProcessor createHttpProcessor() {
        final BasicHttpProcessor httpproc = new BasicHttpProcessor();

        // We don't add default headers as we don't want to modify original request.
        // httpproc.addInterceptor(new RequestDefaultHeaders());

        // Required protocol interceptors

        // We don't add these interceptors as we don't want to modify original request.
        // httpproc.addInterceptor(new RequestContent());
        // httpproc.addInterceptor(new RequestTargetHost());

        // Recommended protocol interceptors
        // httpproc.addInterceptor(new RequestClientConnControl());
        // httpproc.addInterceptor(new RequestUserAgent());
        // httpproc.addInterceptor(new RequestExpectContinue());
        // HTTP state management interceptors
        // httpproc.addInterceptor(new RequestAddCookies());
        // httpproc.addInterceptor(new ResponseProcessCookies());
        // HTTP authentication interceptors
        // httpproc.addInterceptor(new RequestAuthCache());
        // httpproc.addInterceptor(new RequestTargetAuthentication());
        // httpproc.addInterceptor(new RequestProxyAuthentication());
        return httpproc;
    }

}
