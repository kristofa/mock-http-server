package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class DefaultHttpResponseProviderTest {

    private final static Method METHOD = Method.GET;
    private final static String PATH = "/path/";
    private final static String CONTENT_TYPE = "application/json";
    private final static String QUERY_PARAM = "key";
    private final static String QUERY_PARAM_VALUE = "value";
    private final static int HTTP_CODE = 200;

    private DefaultHttpResponseProvider httpResponseProviderIgnoreAdditionalHeaders;
    private HttpRequestImpl httpRequest;
    private HttpResponseImpl httpResponse;

    @Before
    public void setup() {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);

        httpRequest = new HttpRequestImpl();
        httpRequest.method(METHOD).path(PATH).httpMessageHeader(HttpMessageHeaderField.CONTENTTYPE.getValue(), CONTENT_TYPE)
            .queryParameter(QUERY_PARAM, QUERY_PARAM_VALUE);

        httpResponse = new HttpResponseImpl(HTTP_CODE, null, null);

    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersExactMatch() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestCopy = new HttpRequestImpl(httpRequest);
        assertSame("Should also work for a copy", httpResponse,
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestCopy));

        httpResponseProviderIgnoreAdditionalHeaders.verify(); // Expect no exception.
    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersAddedParameters() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithAdditionalParam = new HttpRequestImpl(httpRequest);
        requestWithAdditionalParam.httpMessageHeader("param2", "value2");

        assertSame("Additional header param should be ignored.", httpResponse,
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithAdditionalParam));

        httpResponseProviderIgnoreAdditionalHeaders.verify(); // Expect no exception.
    }

    @Test
    public void testSetAndGetResponseExactMatchAddedParameters() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(false);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithAdditionalParam = new HttpRequestImpl(httpRequest);
        requestWithAdditionalParam.httpMessageHeader("param2", "value2");

        assertNull("Additional header param should not be ignored.",
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithAdditionalParam));

        expectVerifyToFail(httpResponseProviderIgnoreAdditionalHeaders, httpRequest, requestWithAdditionalParam);
    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersOtherMethod() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithOtherMethod = new HttpRequestImpl(httpRequest);
        requestWithOtherMethod.method(Method.POST);

        assertNull("Other method should not be ignored.",
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithOtherMethod));

        expectVerifyToFail(httpResponseProviderIgnoreAdditionalHeaders, httpRequest, requestWithOtherMethod);
    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersOtherPath() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithOtherPath = new HttpRequestImpl(httpRequest);
        requestWithOtherPath.path("/otherpath");

        assertNull("Other path should not be ignored.",
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithOtherPath));

        expectVerifyToFail(httpResponseProviderIgnoreAdditionalHeaders, httpRequest, requestWithOtherPath);
    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersOtherQueryParamValue() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithOtherQueryParam = new HttpRequestImpl(httpRequest);
        requestWithOtherQueryParam.queryParameter(QUERY_PARAM, "other value");

        assertNull("Other query param value for a query param that was set in original request should not be ignored.",
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithOtherQueryParam));

        expectVerifyToFail(httpResponseProviderIgnoreAdditionalHeaders, httpRequest, requestWithOtherQueryParam);
    }

    @Test
    public void testSetAndGetResponseIgnoreAdditionalHeadersOtherContent() throws UnsatisfiedExpectationException {
        httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(true);
        httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

        final HttpRequestImpl requestWithOtherContent = new HttpRequestImpl(httpRequest);
        requestWithOtherContent.content(new String("content").getBytes());

        assertNull("Other content should not be ignored.",
            httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithOtherContent));

        expectVerifyToFail(httpResponseProviderIgnoreAdditionalHeaders, httpRequest, requestWithOtherContent);
    }
    
    @Test
    public void testReset() throws UnsatisfiedExpectationException {
    	 httpResponseProviderIgnoreAdditionalHeaders = new DefaultHttpResponseProvider(false);
         httpResponseProviderIgnoreAdditionalHeaders.set(httpRequest, httpResponse);

         httpResponseProviderIgnoreAdditionalHeaders.reset();
         
         final HttpRequestImpl requestWithAdditionalParam = new HttpRequestImpl(httpRequest);
         requestWithAdditionalParam.httpMessageHeader("param2", "value2");

         httpResponseProviderIgnoreAdditionalHeaders.set(requestWithAdditionalParam, httpResponse);
         
         assertSame(httpResponse,
             httpResponseProviderIgnoreAdditionalHeaders.getResponse(requestWithAdditionalParam));

         httpResponseProviderIgnoreAdditionalHeaders.verify();

    }

    private void expectVerifyToFail(final DefaultHttpResponseProvider responseProvider, final HttpRequest missingRequest,
        final HttpRequest unexpectedRequest) {
        try {
            responseProvider.verify();
            fail("Expected exception.");
        } catch (final UnsatisfiedExpectationException e) {
            final Collection<HttpRequest> missingHttpRequests = e.getMissingHttpRequests();
            final Collection<HttpRequest> unexpectedHttpRequests = e.getUnexpectedHttpRequests();

            assertEquals(1, missingHttpRequests.size());
            assertEquals(1, unexpectedHttpRequests.size());

            assertEquals(missingRequest, missingHttpRequests.iterator().next());
            assertEquals(unexpectedRequest, unexpectedHttpRequests.iterator().next());

        }
    }

}
