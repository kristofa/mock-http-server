/**
 *   Copyright 2011 <jharlap@gitub.com>
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.harlap.test.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class MockHttpServer {
	public enum Method {
		GET, POST, PUT, DELETE;
	}

	private class ExpectedRequest {
		private Method method = null;
		private String path = null;
		private String data = null;
		private boolean satisfied = false;

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
		
		public boolean isSatisfied() {
			return satisfied;
		}
		
		public void passed(){
			satisfied = true;
		}

		@Override
		public String toString() {
			return (method + " " + path + " " + data);
		}

		@Override
		public boolean equals(Object obj) {
			ExpectedRequest req = (ExpectedRequest) obj;
			return req.getMethod().equals(method) && req.getPath().equals(path)
					&& (req.getData() == null || data == null || req.getData().equals(data));
		}

		@Override
		public int hashCode() {
			return (method + " " + path).hashCode();
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

	public class ExpectationHandler implements Container {

		private Map<ExpectedRequest, ExpectedRequest> expectedRequests;
		private Map<ExpectedRequest, ExpectedResponse> responsesForRequests;
		private ExpectedRequest lastAddedExpectation = null;
		
		public ExpectationHandler() {
			responsesForRequests = new HashMap<MockHttpServer.ExpectedRequest, MockHttpServer.ExpectedResponse>();
			expectedRequests = new HashMap<MockHttpServer.ExpectedRequest, MockHttpServer.ExpectedRequest>();
		}

		public void handle(Request req, Response response) {
			String data = null;
			try {
				if(req.getContentLength() > 0) {
					data = req.getContent();
				}
			} catch (IOException e) {
			}
			
			ExpectedRequest expectedRequest = expectedRequests.get(new ExpectedRequest(
					Method.valueOf(req.getMethod()),
					req.getTarget(), data));
			if (responsesForRequests.containsKey(expectedRequest)) {
				ExpectedResponse expectedResponse = responsesForRequests
						.get(expectedRequest);
				response.setCode(expectedResponse.getStatusCode());
				response.set("Content-Type", expectedResponse.getContentType());
				PrintStream body = null;
				try {
					body = response.getPrintStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				body.print(expectedResponse.getBody());
				expectedRequest.passed();
				body.close();
			} else {
				response.setCode(500);
				response.set("Content-Type", "text/plain;charset=utf-8");
				PrintStream body;
				try {
					body = response.getPrintStream();
					body.print("Received unexpected request " + expectedRequest.toString());
					body.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void addExpectedRequest(ExpectedRequest request) {
			lastAddedExpectation = request;
		}

		public void addExpectedResponse(ExpectedResponse response) {
			responsesForRequests.put(lastAddedExpectation, response);
			expectedRequests.put(lastAddedExpectation, lastAddedExpectation);
			lastAddedExpectation = null;
		}

		public void verify() {
			for (ExpectedRequest expectedRequest : responsesForRequests.keySet()) {
				if ( !expectedRequest.isSatisfied()){
					throw new UnsatisfiedExpectationException("Unsatisfied expectation: "+expectedRequest);
				}
			}
			
		}
	}

	private ExpectationHandler handler;

	private int port;

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";

	private Connection connection;

	public MockHttpServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		handler = new ExpectationHandler();
		connection = new SocketConnection(handler);
		SocketAddress address = new InetSocketAddress(port);
		connection.connect(address);
	}

	public void stop() throws Exception {
		connection.close();
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

	public void verify() {
		handler.verify();
	}

}
