package com.github.kristofa.test.http;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PassthroughLoggingHttpProxyTest {
	private static final int port = 80;
	private static final String targetDomain = "www.externalservice.com";
	private static final int targetPort = 8080;
	private HttpRequestResponseLoggerFactory loggerFactory;

	@Before
	public void setUp() {
		loggerFactory = mock(HttpRequestResponseLoggerFactory.class);
	}
	
	@Test
	public void testConstruction() {
		new PassthroughLoggingHttpProxy(port, targetDomain, targetPort, loggerFactory);
	}
}
