package com.harlap.test.http;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MockHttpServerBehaviour {

	private static final int PORT = 51234;
	private static final String baseUrl = "http://localhost:" + PORT;
	private MockHttpServer server;
	private HttpClient client;

	@Before
	public void setUp() throws Exception {
		server = new MockHttpServer(PORT);
		server.start();
		client = new DefaultHttpClient();
	}

	@After
	public void tearDown() throws Exception {
		client.getConnectionManager().shutdown();
		server.stop();
	}

	@Test
	public void testShouldHandleGetRequests() throws ClientProtocolException,
			IOException {
		// Given a mock server configured to respond to a GET / with "OK"
		server.expect(MockHttpServer.Method.GET, "/").respondWith(200,
				"text/plain", "OK");

		// When a request for GET / arrives
		HttpGet req = new HttpGet(baseUrl + "/");
		HttpResponse response = client.execute(req);
		String responseBody = IOUtils.toString(response.getEntity().getContent());
		int statusCode = response.getStatusLine().getStatusCode();
		
		// Then the response is "OK"
		assertEquals("OK", responseBody);
		
		// And the status code is 200
		assertEquals(200, statusCode);
	}

	@Test
	public void testShouldHandlePostRequests() throws ClientProtocolException,
			IOException {
		// Given a mock server configured to respond to a POST / with data
		// "Hello World" with an ID
		server.expect(MockHttpServer.Method.POST, "/", "Hello World")
				.respondWith(200, "text/plain", "ABCD1234");

		// When a request for POST / arrives
		HttpPost req = new HttpPost(baseUrl + "/");
		req.setEntity(new StringEntity("Hello World", HTTP.UTF_8));
		ResponseHandler<String> handler = new BasicResponseHandler();
		String responseBody = client.execute(req, handler);

		// Then the response is "ABCD1234"
		assertEquals("ABCD1234", responseBody);
	}
	
	@Test
	public void testShouldHandleDeleteRequests() throws ClientProtocolException,
			IOException {
		// Given a mock server configured to respond to a DELETE /test
		server.expect(MockHttpServer.Method.DELETE, "/test")
				.respondWith(204, "text/plain", "");

		// When a request for DELETE /test arrives
		HttpDelete req = new HttpDelete(baseUrl + "/test");
		HttpResponse response = client.execute(req);

		// Then the response status is 204
		assertEquals(204, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testShouldHandleMultipleRequests()
			throws ClientProtocolException, IOException {
		// Given a mock server configured to respond to a POST / with data
		// "Hello World" with an ID
		// And configured to respond to a GET /test with "Yes sir!"
		server.expect(MockHttpServer.Method.POST, "/", "Hello World")
				.respondWith(200, "text/plain", "ABCD1234");
		server.expect(MockHttpServer.Method.GET, "/test").respondWith(200,
				"text/plain", "Yes sir!");

		// When a request for POST / arrives
		HttpPost req = new HttpPost(baseUrl + "/");
		req.setEntity(new StringEntity("Hello World", HTTP.UTF_8));
		ResponseHandler<String> handler = new BasicResponseHandler();
		String responseBody = client.execute(req, handler);

		// Then the response is "ABCD1234"
		assertEquals("ABCD1234", responseBody);

		// When a request for GET /test arrives
		HttpGet get = new HttpGet(baseUrl + "/test");
		handler = new BasicResponseHandler();
		responseBody = client.execute(get, handler);

		// Then the response is "Yes sir!"
		assertEquals("Yes sir!", responseBody);
	}

}
