package com.github.kristofa.test.http;

import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpHeaders.HOST;
import static org.junit.Assert.assertEquals;

public class PassthroughForwardHttpRequestBuilderTest {
	private String targetDomain = "www.externalservice.com";
	private int targetPort = 80;
	private PassthroughForwardHttpRequestBuilder requestBuilder;
	private byte[] data = "hello world".getBytes();

	@Before
	public void setUp() {
		this.requestBuilder = new PassthroughForwardHttpRequestBuilder(targetDomain, targetPort);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetForwardRequest_NullDomain() {
		new PassthroughForwardHttpRequestBuilder("", targetPort);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetForwardRequest_BadPort() {
		new PassthroughForwardHttpRequestBuilder(targetDomain, Integer.MAX_VALUE);
	}
	
	@Test
	public void testGetForwardRequest() {
		FullHttpRequestImpl inputRequest = new FullHttpRequestImpl();
		inputRequest.domain("www.testservice.com");
		inputRequest.port(8080);
		inputRequest.path("/resource");
		inputRequest.content(data);
		inputRequest.method(Method.POST);
		
		FullHttpRequestImpl expectedRequest = new FullHttpRequestImpl();
		expectedRequest.domain(targetDomain);
		expectedRequest.port(targetPort);
		expectedRequest.path("/resource");
		expectedRequest.content(data);
		expectedRequest.method(Method.POST);
		expectedRequest.httpMessageHeader(HOST, targetDomain+':'+targetPort);
		
		FullHttpRequest passthroughRequest = requestBuilder.getForwardRequest(inputRequest);

		assertEquals(expectedRequest, passthroughRequest);
	}
}
