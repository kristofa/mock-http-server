package com.github.kristofa.test.http;

import org.apache.commons.lang3.Validate;

import static org.apache.http.HttpHeaders.HOST;

/**
 * An implementation of {@link ForwardHttpRequestBuilder} that constructs {@link FullHttpRequest}s that redirects
 * the request to the external service by changing the domain and port of the request.
 * 
 * @author dominiek
 *
 */
public class PassthroughForwardHttpRequestBuilder implements ForwardHttpRequestBuilder {

	private static final int MAXPORT = 65535;
	private String targetDomain;
	private int targetPort;

	/**
	 * Construct a new instance, incoming requests will be passed through to <code>targetDomain</code>:<code>targetPort</code> of the external service.
	 * @param targetDomain the domain of the external service.
	 * @param targetPort the port of the external service (between 1 and 65535).
	 */
	public PassthroughForwardHttpRequestBuilder(String targetDomain, int targetPort) {
		Validate.notBlank(targetDomain);
		Validate.inclusiveBetween(1, MAXPORT, targetPort);
		this.targetDomain = targetDomain;
		this.targetPort = targetPort;
	}
	
	@Override
	public FullHttpRequest getForwardRequest(FullHttpRequest request) {
		// Use the copy-constructor and adjust domain and port.
		FullHttpRequestImpl result= new FullHttpRequestImpl(request);
		result.domain(targetDomain);
		result.port(targetPort);
		result.httpMessageHeader(HOST, targetDomain+':'+targetPort);
		return result;
	}

}
