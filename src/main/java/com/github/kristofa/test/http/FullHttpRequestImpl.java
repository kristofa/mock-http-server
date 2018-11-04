package com.github.kristofa.test.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.apache.http.HttpHeaders.HOST;

/**
 * Represents a HTTP request. Contains following properties:
 * <ul>
 * <li>method: HTTP method (GET, PUT, POST, DELETE, HEAD, ...).</li>
 * <li>contentType: Content type of the message body. Content of Content-Type header.</li>
 * <li>content: Message body.</li>
 * <li>domain: host name part of url.</li>
 * <li>port: port part of url.</li>
 * <li>path: Path part of url.</li>
 * <li>query parameters: query parameters part of url.</li>
 * </ul>
 * 
 * @author kristof
 */
public final class FullHttpRequestImpl implements FullHttpRequest {

    private static final String PORT_SEPARATOR = ":";
    private static final String NOT_SPECIFIED = "null";
    private final HttpRequestImpl httpRequest;

    // Address properties.
    private String domain;
    private Integer port;

    /**
     * Creates a new HTTP request object with no parameters set.
     */
    public FullHttpRequestImpl() {
        httpRequest = new HttpRequestImpl();
    }

    /**
     * Copy constructor.
     * 
     * @param request Will copy properties from this request.
     */
    public FullHttpRequestImpl(final FullHttpRequest request) {
        httpRequest = new HttpRequestImpl(request);
        httpRequest.removeHttpMessageHeaders(HOST);
        domain = request.getDomain();
        port = request.getPort();
    }

    /**
     * Sets method for request.
     * 
     * @param method Method for request.
     * @return This http request.
     */
    public FullHttpRequestImpl method(final Method method) {
        httpRequest.method(method);
        return this;
    }

    /**
     * Adds a Http message header.
     * 
     * @param name header name. Should not be <code>null</code> or blank.
     * @param value header value. Should not be <code>null</code> or blank.
     * @return The http request.
     */
    public FullHttpRequestImpl httpMessageHeader(final String name, final String value) {
        httpRequest.httpMessageHeader(name, value);
        return this;
    }

    /**
     * Sets content of message body for request.
     * 
     * @param content Message body for request.
     * @return This http request.
     */
    public FullHttpRequestImpl content(final byte[] content) {
        httpRequest.content(content);
        return this;
    }

    /**
     * Sets host for request.
     * 
     * @param domain Sets domain / host for request.
     * @return This http request.
     */
    public FullHttpRequestImpl domain(final String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Sets port for request.
     * 
     * @param port Sets port for request.
     * @return This http request.
     */
    public FullHttpRequestImpl port(final Integer port) {
        this.port = port;
        return this;
    }

    /**
     * Sets path for request.
     * 
     * @param path Sets path for request.
     * @return This http request.
     */
    public FullHttpRequestImpl path(final String path) {
        httpRequest.path(path);
        return this;
    }

    /**
     * Adds a query parameter for request.
     * 
     * @param key Parameter key. Should not be empty or <code>null</code>.
     * @param value Parameter value. Should not be empty or <code>null</code>
     * @return This http request.
     */
    public FullHttpRequestImpl queryParameter(final String key, final String value) {
        httpRequest.queryParameter(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getMethod() {
        return httpRequest.getMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent() {
        return httpRequest.getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return httpRequest.getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QueryParameter> getQueryParameters() {
        return httpRequest.getQueryParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMessageHeader> getHttpMessageHeaders() {
        return httpRequest.getHttpMessageHeaders();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<QueryParameter> getQueryParameters(final String key) {
        return httpRequest.getQueryParameters(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<HttpMessageHeader> getHttpMessageHeaders(final String name) {
        return httpRequest.getHttpMessageHeaders(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        try {
            final String queryParamsAsString = getQueryParamsAsString();
            String host = domain == null ? "" : domain;
            if (port != null) {
                host += PORT_SEPARATOR + port;
            }

            final URI uri =
                new URI("http", host, httpRequest.getPath(), queryParamsAsString.isEmpty() ? null : queryParamsAsString,
                    null);
            return uri.toASCIIString();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        final String httpRequestString = httpRequest.toString();
        final String domainString = add("Domain: ", domain);
        final String portString = add("Port: ", port);

        final String[] array = {httpRequestString, domainString, portString};

        return StringUtils.join(array, "\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

    private String add(final String value, final Object object) {
        if (object != null) {
            return value + object;
        }
        return value + NOT_SPECIFIED;
    }

    private String getQueryParamsAsString() {

        String queryParamsAsString = "";
        boolean first = true;
        for (final QueryParameter parameter : httpRequest.getQueryParameters()) {
            if (first) {
                first = false;
            } else {
                queryParamsAsString += "&";
            }
            queryParamsAsString += parameter.toString();
        }
        return queryParamsAsString;
    }

}
