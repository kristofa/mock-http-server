package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

public class FileHttpResponseProviderTest {

    private final static String TEST_FILE_DIRECTORY = "target/test-classes/";

    @Test
    public void testExpectedHttpResponseFileProviderConstructor_NoSingleFileFound() {
        try {
            new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "unexisting");
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals(
                "No saved http request/responses found. File target/test-classes/unexisting_request_00001.txt not found.",
                e.getMessage());
        }
    }

    @Test
    public void testExpectedHttpResponseFileProviderConstructor_NoResponseFileFound() {
        try {
            new FileHttpResponseProvider(TEST_FILE_DIRECTORY, "ExpectedHttpResponseFileProviderTest_No_Response");
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

}
