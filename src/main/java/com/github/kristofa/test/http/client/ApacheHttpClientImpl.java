package com.github.kristofa.test.http.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;

import com.github.kristofa.test.http.FullHttpRequest;
import com.github.kristofa.test.http.HttpMessageHeader;
import com.github.kristofa.test.http.Method;

/**
 * {@link HttpClient} implementation that uses <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HTTP
 * client</a> as 'lower level' library.
 * 
 * @author kristof
 */
public class ApacheHttpClientImpl implements HttpClient {

    public ApacheHttpClientImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpClientResponse<InputStream> execute(final FullHttpRequest request) throws HttpRequestException {

        if (request.getMethod().equals(Method.GET)) {
            final HttpGet httpGet = new HttpGet(request.getUrl());
            populateHeader(request, httpGet);

            try {
                return execute(httpGet);
            } catch (final IOException e1) {
                throw new GetException(e1);
            }
        } else if (request.getMethod().equals(Method.PUT)) {
            final HttpPut httpPut = new HttpPut(request.getUrl());
            populateHeader(request, httpPut);
            try {
                return execute(httpPut, request.getContent());
            } catch (final IOException e1) {
                throw new PutException(e1);
            }
        } else if (request.getMethod().equals(Method.POST)) {
            final HttpPost httpPost = new HttpPost(request.getUrl());
            populateHeader(request, httpPost);
            try {
                return execute(httpPost, request.getContent());
            } catch (final IOException e1) {
                throw new PostException(e1);
            }
        } else if (request.getMethod().equals(Method.DELETE)) {
            final HttpDelete httpDelete = new HttpDelete(request.getUrl());
            populateHeader(request, httpDelete);
            try {
                return execute(httpDelete);
            } catch (final IOException e1) {
                throw new GetException(e1);
            }

        }
        throw new HttpRequestException("Unsupported operation: " + request.getMethod());
    }

    /**
     * Gets a new HTTPClient instance. Introduced to facilitate testing.
     * 
     * @return A new HTTPClient instance.
     */
    /* package */org.apache.http.client.HttpClient getClient() {
        // We use a Custom implementation because we don't want to modify the requests/responses.
        return new CustomHttpClient();
    }

    private void populateHeader(final FullHttpRequest request, final HttpRequestBase apacheRequest) {
        for (final HttpMessageHeader header : request.getHttpMessageHeaders()) {
            apacheRequest.addHeader(header.getName(), header.getValue());
        }
    }

    private HttpClientResponse<InputStream> execute(final HttpEntityEnclosingRequestBase request, final byte[] content)
        throws IOException {

        if (content != null) {
            request.setEntity(new ByteArrayEntity(content));
        }
        return execute(request);
    }

    private HttpClientResponse<InputStream> execute(final HttpRequestBase request) throws IOException {
        final org.apache.http.client.HttpClient client = getClient();
        try {
            final HttpResponse httpResponse = client.execute(request);
            return buildResponse(client, httpResponse);
        } catch (final IOException e) {
            client.getConnectionManager().shutdown(); // In case of exception we should close connection manager here.
            throw e;
        }
    }

    private HttpClientResponse<InputStream> buildResponse(final org.apache.http.client.HttpClient client,
        final HttpResponse response) throws IOException {
        final int status = response.getStatusLine().getStatusCode();
        final ApacheHttpClientResponseImpl<InputStream> httpResponse =
            new ApacheHttpClientResponseImpl<InputStream>(status, client);
        httpResponse.setResponseEntity(response.getEntity().getContent());
        if (response.getEntity().getContentType() != null) {
            httpResponse.setContentType(response.getEntity().getContentType().getValue());
        }
        if (status < 200 || status > 299) {
            // In this case response will be the error message.
            httpResponse.setErrorMessage("Got HTTP return code " + status);
        }
        return httpResponse;
    }
}
