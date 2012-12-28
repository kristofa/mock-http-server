package com.github.kristofa.test.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * Indicates there are {@link HttpRequest http requests} that we expected but did not get and/or that we got unexpected
 * {@link HttpRequest http requests}.
 * 
 * @see HttpResponseProvider
 * @author kristof
 */
public class UnsatisfiedExpectationException extends Exception {

    private static final long serialVersionUID = -6003072239642243697L;

    private final List<HttpRequest> missingHttpRequests = new ArrayList<HttpRequest>();
    private final List<HttpRequest> unexpectedHttpRequests = new ArrayList<HttpRequest>();

    /**
     * Creates a new instance.
     * <p>
     * Both collections should not be <code>null</code>. One of both collections can be empty.
     * 
     * @param missingRequests Requests that we expected but did not get.
     * @param unexpectedRequests Requests that we got but did not expect.
     */
    public UnsatisfiedExpectationException(final Collection<HttpRequest> missingRequests,
        final Collection<HttpRequest> unexpectedRequests) {
        super();
        Validate.notNull(missingRequests);
        Validate.notNull(unexpectedRequests);
        Validate.isTrue(!missingRequests.isEmpty() || !unexpectedRequests.isEmpty());
        missingHttpRequests.addAll(missingRequests);
        unexpectedHttpRequests.addAll(unexpectedRequests);
    }

    /**
     * Gets the http requests that we expected but did not get.
     * 
     * @return Collection of http requests that we expected but did not get.
     */
    public Collection<HttpRequest> getMissingHttpRequests() {
        return Collections.unmodifiableCollection(missingHttpRequests);
    }

    /**
     * Gets the http requests that we got but did not expect.
     * 
     * @return Collection of http requests that we got but did not expect.
     */
    public Collection<HttpRequest> getUnexpectedHttpRequests() {
        return Collections.unmodifiableCollection(unexpectedHttpRequests);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final String missingExpectedRequestsString = "Missing expected requests: " + getMissingHttpRequests();
        final String unexpectedReceivedRequestsString = "Unexpected received requests: " + getUnexpectedHttpRequests();

        return missingExpectedRequestsString + "\n" + unexpectedReceivedRequestsString;
    }

}
