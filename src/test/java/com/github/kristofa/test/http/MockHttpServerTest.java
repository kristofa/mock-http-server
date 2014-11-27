/**
 * Copyright 2011 <jharlap@gitub.com> Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MockHttpServerTest {

    private static final String UTF_8 = "UTF-8";
    private static MockHttpServer server;
    private static SimpleHttpResponseProvider responseProvider;
    private HttpClient client;
    private static String baseUrl;
    

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        responseProvider = new SimpleHttpResponseProvider();
        server = new MockHttpServer(0, responseProvider);
        final int port = server.start();
        assertTrue(port != -1);
        baseUrl = "http://localhost:" + server.getPort();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        server.stop();
    }

    @Before
    public void setUp() throws Exception {
        client = new DefaultHttpClient();
        responseProvider.reset();
    }

    @After
    public void tearDown() {
        client.getConnectionManager().shutdown();
    }

    @Test
    public void testShouldHandleGetRequests() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

        // When a request for GET / arrives
        final HttpGet req = new HttpGet(baseUrl + "/");
        final HttpResponse response = client.execute(req);
        final String responseBody = IOUtils.toString(response.getEntity().getContent());
        final int statusCode = response.getStatusLine().getStatusCode();

        // Then the response is "OK"
        assertEquals("OK", responseBody);

        // And the status code is 200
        assertEquals(200, statusCode);
    }

    @Test
    public void testShouldHandlePostRequests() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a POST / with data
        // "Hello World" with an ID
        responseProvider.expect(Method.POST, "/", "text/plain; charset=UTF-8", "Hello World").respondWith(200, "text/plain",
            "ABCD1234");

        // When a request for POST / arrives
        final HttpPost req = new HttpPost(baseUrl + "/");
        req.setEntity(new StringEntity("Hello World", UTF_8));
        final ResponseHandler<String> handler = new BasicResponseHandler();
        final String responseBody = client.execute(req, handler);

        // Then the response is "ABCD1234"
        assertEquals("ABCD1234", responseBody);
    }

    @Test
    public void testShouldHandleDeleteRequests() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a DELETE /test
        responseProvider.expect(Method.DELETE, "/test").respondWith(204, "text/plain", "");

        // When a request for DELETE /test arrives
        final HttpDelete req = new HttpDelete(baseUrl + "/test");
        final HttpResponse response = client.execute(req);

        // Then the response status is 204
        assertEquals(204, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testShouldHandlePutRequests() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a DELETE /test
        responseProvider.expect(Method.PUT, "/test", "text/plain; charset=UTF-8", "Hello World").respondWith(200,
            "text/plain", "Welcome");

        // When a request for DELETE /test arrives
        final HttpPut req = new HttpPut(baseUrl + "/test");
        req.setEntity(new StringEntity("Hello World", UTF_8));
        final HttpResponse response = client.execute(req);

        final String responseBody = IOUtils.toString(response.getEntity().getContent());
        final int statusCode = response.getStatusLine().getStatusCode();

        // Then the response status is 204
        assertEquals(200, statusCode);
        assertEquals("Welcome", responseBody);
    }

    @Test
    public void testShouldNotMatchDataWhenExceptedDataIsNull() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a POST /test with no data
        responseProvider.expect(Method.POST, "/test").respondWith(204, "text/plain", "");

        // When a request for POST /test arrives with parameters
        final HttpPost req = new HttpPost(baseUrl + "/test");
        req.setEntity(new StringEntity("Hello World", UTF_8));

        final HttpResponse response = client.execute(req);

        // Then the response status is 598
        assertEquals(598, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testShouldHandleMultipleRequests() throws ClientProtocolException, IOException {
        // Given a mock server configured to respond to a POST / with data
        // "Hello World" with an ID
        // And configured to respond to a GET /test with "Yes sir!"
        responseProvider.expect(Method.POST, "/", "text/plain; charset=UTF-8", "Hello World").respondWith(200, "text/plain",
            "ABCD1234");
        responseProvider.expect(Method.GET, "/test").respondWith(200, "text/plain", "Yes sir!");

        // When a request for POST / arrives
        final HttpPost req = new HttpPost(baseUrl + "/");
        req.setEntity(new StringEntity("Hello World", UTF_8));
        ResponseHandler<String> handler = new BasicResponseHandler();
        String responseBody = client.execute(req, handler);

        // Then the response is "ABCD1234"
        assertEquals("ABCD1234", responseBody);

        // When a request for GET /test arrives
        final HttpGet get = new HttpGet(baseUrl + "/test");
        handler = new BasicResponseHandler();
        responseBody = client.execute(get, handler);

        // Then the response is "Yes sir!"
        assertEquals("Yes sir!", responseBody);
    }

    @Test(expected = UnsatisfiedExpectationException.class)
    public void testShouldFailWhenGetExpectationNotInvoqued() throws ClientProtocolException, IOException,
        UnsatisfiedExpectationException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

        server.verify();
    }

    @Test
    public void testShouldNotFailWhenGetExpectationIsInvoqued() throws ClientProtocolException, IOException,
        UnsatisfiedExpectationException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

        final HttpGet req = new HttpGet(baseUrl + "/");
        client.execute(req);

        server.verify();
    }

    @Test(expected = UnsatisfiedExpectationException.class)
    public void testShouldFailWhenPostExpectationNotInvoqued() throws ClientProtocolException, IOException,
        UnsatisfiedExpectationException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.POST, "/").respondWith(200, "text/plain", "OK");

        server.verify();
    }

    @Test
    public void testShouldNotFailWhenPostExpectationIsInvoqued() throws ClientProtocolException, IOException,
        UnsatisfiedExpectationException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.POST, "/").respondWith(200, "text/plain", "OK");

        final HttpPost req = new HttpPost(baseUrl + "/");
        client.execute(req);

        server.verify();
    }

    @Test(expected = UnsatisfiedExpectationException.class)
    public void testShouldFailWhenOneOfSeveralGetExpectationsIsNotInvoqued() throws ClientProtocolException, IOException,
        UnsatisfiedExpectationException {
        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");
        responseProvider.expect(Method.GET, "/other").respondWith(200, "text/plain", "OK");

        final HttpGet req = new HttpGet(baseUrl + "/");
        client.execute(req);

        server.verify();
    }

    @Test
    public void testShouldRespondWith598WhenNotMatchingAnyRequestExpectation() throws ClientProtocolException, IOException {
        responseProvider.expect(Method.GET, "/foo").respondWith(200, "text/plain", "OK");

        final HttpGet req = new HttpGet(baseUrl + "/bar");
        final HttpResponse response = client.execute(req);

        assertEquals(598, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testShouldRespondWithCustomResponseCodeWhenNotMatchingAnyRequestExpectation()
        throws ClientProtocolException, IOException {
        server.setNoMatchFoundResponseCode(403);
        try {
            responseProvider.expect(Method.GET, "/foo").respondWith(200, "text/plain", "OK");

            final HttpGet req = new HttpGet(baseUrl + "/bar");
            final HttpResponse response = client.execute(req);

            assertEquals(403, response.getStatusLine().getStatusCode());
        } finally {
            // Set to default value again to make sure potential other tests succeed.
            server.setNoMatchFoundResponseCode(598);
        }
    }

    @Test
    public void testResponseWithNullContentTypeAndContent() throws ClientProtocolException, IOException {
        responseProvider.expect(Method.GET, "/foo").respondWith(200, null, null);
        final HttpGet req = new HttpGet(baseUrl + "/foo");
        final HttpResponse response = client.execute(req);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("", IOUtils.toString(response.getEntity().getContent()));

    }

    @Test
    public void testVerifyDoNothingWhenNoExceptations() throws UnsatisfiedExpectationException {
        server.verify();
    }

    @Test
    public void testQueryParameters() throws ClientProtocolException, IOException {
        responseProvider.expect(Method.GET, "/query?a=b&b=c").respondWith(200, null, null);
        final HttpGet req = new HttpGet(baseUrl + "/query?a=b&b=c");
        final HttpResponse response = client.execute(req);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("", IOUtils.toString(response.getEntity().getContent()));
    }

    @Test
    public void testQueryParametersDifferentOrder() throws ClientProtocolException, IOException {
        responseProvider.expect(Method.GET, "/query?a=b&b=c").respondWith(200, null, null);
        final HttpGet req = new HttpGet(baseUrl + "/query?b=c&a=b");
        final HttpResponse response = client.execute(req);

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("", IOUtils.toString(response.getEntity().getContent()));
    }
    
    @Test
    public void testStartMultipleServers() throws IOException {
    	MockHttpServer server2 = new MockHttpServer(0, responseProvider);
    	MockHttpServer server3 = new MockHttpServer(0, responseProvider);
    	
    	final int server2Port = server2.start();
    	try
    	{
    		final int server3Port = server3.start();
    		try
    		{
    			assertTrue(server2Port != server3Port);
    			assertEquals(server2Port, server2.getPort());
    			assertEquals(server3Port, server3.getPort());
    		}
    		finally
    		{
    			server3.stop();
    		}
    	}
    	finally
    	{
    		server2.stop();
    	}
    }

}
