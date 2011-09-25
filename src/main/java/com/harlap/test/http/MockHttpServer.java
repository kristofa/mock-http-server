package com.harlap.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class MockHttpServer {
	public enum Method {
		GET, POST, PUT, DELETE;
	}

	private class ExpectedRequest {
		private Method method = null;
		private String path = null;
		private String data = null;

		public ExpectedRequest(Method method, String path) {
			this.method = method;
			this.path = path;
		}

		public ExpectedRequest(Method method, String path, String data) {
			this.method = method;
			this.path = path;
			this.data = data;
		}

		public Method getMethod() {
			return method;
		}

		public String getPath() {
			return path;
		}

		public String getData() {
			return data;
		}

		@Override
		public String toString() {
			return (method + " " + path + " " + data);
		}
		
		@Override
		public boolean equals(Object obj) {
			ExpectedRequest req = (ExpectedRequest) obj;
			return req.getMethod().equals(method) && req.getPath().equals(path)
					&& (req.getData() == null ? data == null : req.getData().equals(data));
		}

		@Override
		public int hashCode() {
			return (method + " " + path + " " + data).hashCode();
		}
	}

	private class ExpectedResponse {
		private int statusCode;
		private String contentType;
		private String body;

		public ExpectedResponse(int statusCode, String contentType, String body) {
			this.statusCode = statusCode;
			this.contentType = contentType;
			this.body = body;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public String getContentType() {
			return contentType;
		}

		public String getBody() {
			return body;
		}
	}

	public class ExpectationHandler extends AbstractHandler {

		private Map<ExpectedRequest, ExpectedResponse> expectedRequests;
		private ExpectedRequest lastAddedExpectation = null;

		public ExpectationHandler() {
			expectedRequests = new HashMap<MockHttpServer.ExpectedRequest, MockHttpServer.ExpectedResponse>();
		}

		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			String data = null;
			try {
				if(baseRequest.getContentLength() > 0) {
					data = IOUtils.toString(baseRequest.getReader());
				}
			} catch (IOException e) {
			}

			ExpectedRequest expectedRequest = new ExpectedRequest(
					Method.valueOf(baseRequest.getMethod()),
					baseRequest.getRequestURI(), data);
			if (expectedRequests.containsKey(expectedRequest)) {
				ExpectedResponse expectedResponse = expectedRequests
						.get(expectedRequest);
				response.setStatus(expectedResponse.getStatusCode());
				response.setContentType(expectedResponse.getContentType());
				IOUtils.write(expectedResponse.getBody(),
						response.getOutputStream());
				baseRequest.setHandled(true);
			} else {
				throw new ServletException("Received unexpected request " + expectedRequest.toString());
			}
		}

		public void addExpectedRequest(ExpectedRequest request) {
			lastAddedExpectation = request;
		}

		public void addExpectedResponse(ExpectedResponse response) {
			expectedRequests.put(lastAddedExpectation, response);
			lastAddedExpectation = null;
		}
	}

	private Server server;

	private ExpectationHandler handler;

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";

	public MockHttpServer(int port) {
		server = new Server(port);
		handler = new ExpectationHandler();
		server.setHandler(handler);
	}

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public MockHttpServer expect(Method method, String path) {
		handler.addExpectedRequest(new ExpectedRequest(method, path));
		return this;
	}

	public MockHttpServer respondWith(int statusCode, String contentType,
			String body) {
		handler.addExpectedResponse(new ExpectedResponse(statusCode,
				contentType, body));
		return this;
	}

	public MockHttpServer expect(Method method, String path, String data) {
		handler.addExpectedRequest(new ExpectedRequest(method, path, data));
		return this;
	}

}
