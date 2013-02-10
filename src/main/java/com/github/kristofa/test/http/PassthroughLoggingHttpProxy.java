package com.github.kristofa.test.http;

import java.util.Collections;

/**
 * A {@link LoggingHttpProxy} that will behave as a pass-through proxy between the system-under-test and the external service.
 * @author dominiek
 *
 */
public class PassthroughLoggingHttpProxy extends LoggingHttpProxy {

	/**
	 * Construct a new instance.
	 * @param port the local port.
	 * @param targetDomain the target domain.
	 * @param targetPort the target port.
	 * @param loggerFactory the {@link HttpRequestResponseLoggerFactory}.
	 */
	public PassthroughLoggingHttpProxy(int port, String targetDomain, int targetPort, HttpRequestResponseLoggerFactory loggerFactory) {
		super(port, Collections.<ForwardHttpRequestBuilder>singleton(new PassthroughForwardHttpRequestBuilder(targetDomain, targetPort)), loggerFactory);
	}

}
