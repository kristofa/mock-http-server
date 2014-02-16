package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Test;

import com.github.kristofa.test.http.HttpRequest;
import com.github.kristofa.test.http.HttpRequestImpl;
import com.github.kristofa.test.http.HttpRequestMatcher;
import com.github.kristofa.test.http.HttpResponse;
import com.github.kristofa.test.http.HttpResponseImpl;
import com.github.kristofa.test.http.Method;
import com.github.kristofa.test.http.UnsatisfiedExpectationException;

public class FileHttpResponseProviderTest {

    private final static String TEST_FILE_DIRECTORY = "target/test-classes/";

    @Test
    public void testFileHttpResponseProvider_NoSingleFileFound() {
        try {
            final FileHttpResponseProvider fileHttpResponseProvider =
                new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "unexisting");
            final HttpRequest mockRequest = mock(HttpRequest.class);
            fileHttpResponseProvider.getResponse(mockRequest);
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals(
                "No saved http request/responses found. File target/test-classes/unexisting_request_00001.txt not found.",
                e.getMessage());
        }
    }

    @Test
    public void testFileHttpResponseProvider_NoResponseFileFound() {
        try {
            final FileHttpResponseProvider fileHttpResponseProvider =
                new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "ExpectedHttpResponseFileProviderTest_No_Response");
            final HttpRequest mockRequest = mock(HttpRequest.class);
            fileHttpResponseProvider.getResponse(mockRequest);
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals(
                "Found request file (target/test-classes/ExpectedHttpResponseFileProviderTest_No_Response_request_00001.txt) but no matching response file: target/test-classes/ExpectedHttpResponseFileProviderTest_No_Response_response_00001.txt",
                e.getMessage());
        }
    }

    @Test
    public void testGetResponseSucces() throws UnsatisfiedExpectationException {
        final FileHttpResponseProvider expectedHttpResponseFileProvider =
            new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "ExpectedHttpResponseFileProviderTest");

        final HttpRequestImpl request1 = new HttpRequestImpl();
        request1.method(Method.GET).httpMessageHeader("Content-Type", "application/json").queryParameter("a", "b")
            .path("/a/b");
        final HttpResponse response1 = expectedHttpResponseFileProvider.getResponse(request1);
        assertEquals(200, response1.getHttpCode());
        assertEquals("application/json", response1.getContentType());
        assertNull(response1.getContent());

        final HttpRequestImpl request2 = new HttpRequestImpl();
        request2.method(Method.POST).httpMessageHeader("Content-Type", "application/json").queryParameter("b", "c")
            .path("/b/c");
        final HttpResponse response2 = expectedHttpResponseFileProvider.getResponse(request2);
        assertEquals(200, response2.getHttpCode());
        assertEquals("application/json", response2.getContentType());
        assertNull(response2.getContent());

        // Same request as request 1 but expect different return value.
        final HttpRequestImpl request3 = new HttpRequestImpl();
        request3.method(Method.GET).httpMessageHeader("Content-Type", "application/json").queryParameter("a", "b")
            .path("/a/b");
        final HttpResponse response3 = expectedHttpResponseFileProvider.getResponse(request3);
        assertEquals(401, response3.getHttpCode());
        assertEquals("application/json", response3.getContentType());
        assertNull(response3.getContent());

        // Should not throw exceptions.
        expectedHttpResponseFileProvider.verify();
    }

    @Test
    public void testGetResponseUnExpectedRequest() {
        final FileHttpResponseProvider expectedHttpResponseFileProvider =
            new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "ExpectedHttpResponseFileProviderTest");

        final HttpRequestImpl request1 = new HttpRequestImpl();
        request1.method(Method.GET).httpMessageHeader("Content-Type", "application/json").queryParameter("b", "c")
            .path("/a/b");
        assertNull(expectedHttpResponseFileProvider.getResponse(request1));

        try {
            expectedHttpResponseFileProvider.verify();
            fail("Expected exception.");
        } catch (final UnsatisfiedExpectationException e) {
            final Collection<HttpRequest> unexpectedHttpRequests = e.getUnexpectedHttpRequests();
            assertEquals(1, unexpectedHttpRequests.size());
            assertTrue(unexpectedHttpRequests.contains(request1));

            final Collection<HttpRequest> missingHttpRequests = e.getMissingHttpRequests();
            assertEquals("", 3, missingHttpRequests.size());

            final HttpRequestImpl expectedRequest1 = new HttpRequestImpl();
            expectedRequest1.method(Method.GET).httpMessageHeader("Content-Type", "application/json")
                .queryParameter("a", "b").path("/a/b");
            assertTrue(missingHttpRequests.contains(expectedRequest1));

            final HttpRequestImpl expectedRequest2 = new HttpRequestImpl();
            expectedRequest2.method(Method.POST).httpMessageHeader("Content-Type", "application/json")
                .queryParameter("b", "c").path("/b/c");
            assertTrue(missingHttpRequests.contains(expectedRequest2));

            // expectedRequest1 is expected to be submitted twice so it should be twice in collection.
            int count = 0;
            for (final HttpRequest request : missingHttpRequests) {
                if (expectedRequest1.equals(request)) {
                    count++;
                }
            }
            assertEquals("expectedRequest1 is expected to be submitted twice so it should be twice in collection.", 2, count);

        }
    }

    @Test
    public void testCustomMatcher() {
        final FileHttpResponseProvider responseProvider =
            new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "FileHttpResponseProviderTest");
        final HttpRequestMatcher matcher = new HttpRequestMatcher() {

            @Override
            public boolean match(final HttpRequest originalRequest, final HttpRequest otherRequest) {

                if (originalRequest.getPath().equals("/a/b") && otherRequest.getPath().equals("/a/b")) {
                    return true;
                }
                return false;
            }

            @Override
            public HttpResponse getResponse(final HttpRequest originalRequest, final HttpResponse originalResponse,
                final HttpRequest matchingRequest) {
                return new HttpResponseImpl(201, "application/json", null);
            }

        };
        responseProvider.addMatcher(matcher);

        final HttpRequestImpl request1 = new HttpRequestImpl();
        request1.path("/a/b");
        request1.httpMessageHeader("custom", "value");

        final HttpResponse response1 = responseProvider.getResponse(request1);
        assertNotNull("We expect matcher to be kicked in and provided response.", response1);
        assertEquals("We expect custom response.", 201, response1.getHttpCode());

        final HttpRequestImpl request2 = new HttpRequestImpl();
        request2.path("/a/c");
        request2.httpMessageHeader("custom", "value");
        assertNull("for /a/c we need exact match.", responseProvider.getResponse(request2));

        final HttpRequestImpl request3 = new HttpRequestImpl();
        request3.method(Method.GET);
        request3.path("/a/c");
        request3.httpMessageHeader("Content-Type", "application/json");
        request3.queryParameter("a", "1");

        final HttpResponse response3 = responseProvider.getResponse(request3);
        assertNotNull("We expected exact match to have worked.", response3);
        assertEquals(200, response3.getHttpCode());

    }
}
