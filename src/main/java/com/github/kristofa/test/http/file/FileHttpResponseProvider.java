package com.github.kristofa.test.http.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.github.kristofa.test.http.DefaultHttpRequestMatcher;
import com.github.kristofa.test.http.HttpRequest;
import com.github.kristofa.test.http.HttpRequestMatcher;
import com.github.kristofa.test.http.HttpResponse;
import com.github.kristofa.test.http.HttpResponseProvider;
import com.github.kristofa.test.http.LoggingHttpProxy;
import com.github.kristofa.test.http.UnsatisfiedExpectationException;

/**
 * {@link HttpResponseProvider} that is able to serve responses for requests/responses previously saved by
 * {@link HttpRequestResponseFileLogger}.
 * <p>
 * It supports submitting same request multiple times with different return result.
 * <p>
 * It reads all http requests on construction and keeps them in memory. It does not keep responses in memory. They are read
 * from disk on request and not cached.
 * 
 * @see HttpRequestResponseFileLogger
 * @see LoggingHttpProxy
 * @author kristof
 */
public class FileHttpResponseProvider implements HttpResponseProvider {

    private final String directory;
    private final String fileName;
    private final List<Pair<HttpRequestMatcher, List<ResponseProxy>>> requestMatchers =
        new ArrayList<Pair<HttpRequestMatcher, List<ResponseProxy>>>();
    private final HttpRequestFileReader httpRequestFileReader;
    private final HttpResponseFileReader httpResponseFileReader;
    private final List<HttpRequest> unexpectedRequests = new ArrayList<HttpRequest>();
    private boolean initialized = false;

    /**
     * Creates a new instance. Will try to find request/response files and will throw unchecked exception in case:
     * <ul>
     * <li>We can not find at least 1 request/response for given directory and file name.
     * <li>We found a request without persisted response
     * </ul>
     * 
     * @param directory Directory from which to read files.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension. Should be same as used in {@link HttpRequestResponseFileLogger}.
     */
    public FileHttpResponseProvider(final String directory, final String fileName) {
        this(directory, fileName, new HttpRequestFileReaderImpl(), new HttpResponseFileReaderImpl());
    }

    /**
     * Creates a new instance. Will try to find request/response files and will throw unchecked exception in case:
     * <ul>
     * <li>We can not find at least 1 request/response for given directory and file name.
     * <li>We found a request without persisted response
     * </ul>
     * 
     * @param directory Directory from which to read files.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension. Should be same as used in {@link HttpRequestResponseFileLogger}.
     * @param requestFileReader HTTP request file reader.
     * @param responseFileReader HTTP response file reader.
     */
    public FileHttpResponseProvider(final String directory, final String fileName,
        final HttpRequestFileReader requestFileReader, final HttpResponseFileReader responseFileReader) {
        this.directory = directory;
        this.fileName = fileName;
        httpRequestFileReader = requestFileReader;
        httpResponseFileReader = responseFileReader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized HttpResponse getResponse(final HttpRequest request) {
        if (!initialized) {
            init();
            initialized = true;
        }

        for (final Pair<HttpRequestMatcher, List<ResponseProxy>> pair : requestMatchers) {
            if (pair.getLeft().match(request)) {
                final List<ResponseProxy> proxyList = pair.getRight();
                final int size = proxyList.size();
                for (int index = 0; index < size; index++) {
                    final ResponseProxy responseProxy = proxyList.get(index);
                    if (responseProxy.requested == false) {
                        final File responseFile =
                            new File(directory,
                                FileNameBuilder.RESPONSE_FILE_NAME.getFileName(fileName, responseProxy.seqNr));
                        final File responseEntityFile =
                            new File(directory, FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(fileName,
                                responseProxy.seqNr));
                        responseProxy.requested = true;
                        final HttpResponse originalResponse = httpResponseFileReader.read(responseFile, responseEntityFile);
                        return pair.getLeft().getResponse(request, originalResponse);
                    }
                }
            }
        }
        unexpectedRequests.add(request);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMatcher(final HttpRequestMatcher matcher) {
        final List<ResponseProxy> proxyList = new ArrayList<ResponseProxy>();
        final Pair<HttpRequestMatcher, List<ResponseProxy>> of = Pair.of(matcher, proxyList);
        requestMatchers.add(0, of);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verify() throws UnsatisfiedExpectationException {
        final Collection<HttpRequest> missingRequests = new ArrayList<HttpRequest>();
        for (final Pair<HttpRequestMatcher, List<ResponseProxy>> pair : requestMatchers) {
            final List<ResponseProxy> responseProxies = pair.getRight();
            for (final ResponseProxy responseProxy : responseProxies) {
                if (!responseProxy.requested) {
                    missingRequests.add(responseProxy.request);
                }
            }
        }

        if (!unexpectedRequests.isEmpty() || !missingRequests.isEmpty()) {
            throw new UnsatisfiedExpectationException(missingRequests, unexpectedRequests);
        }

    }

    private void init() {
        int seqNr = 1;

        File requestFile = new File(directory, FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr));
        if (!requestFile.exists()) {
            throw new IllegalStateException("No saved http request/responses found. File " + requestFile + " not found.");
        }

        while (requestFile.exists()) {
            final File responseFile = new File(directory, FileNameBuilder.RESPONSE_FILE_NAME.getFileName(fileName, seqNr));
            if (!responseFile.exists()) {
                throw new IllegalStateException("Found request file (" + requestFile + ") but no matching response file: "
                    + responseFile);
            }
            cacheRequest(fileName, seqNr);
            seqNr++;
            requestFile = new File(directory, FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr));
        }

    }

    private void cacheRequest(final String fileName, final int seqNr) {

        final File requestFile = new File(directory, FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr));
        final File requestEntityFile =
            new File(directory, FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(fileName, seqNr));
        final HttpRequest request = httpRequestFileReader.read(requestFile, requestEntityFile);
        boolean matched = false;
        final ResponseProxy responseProxy = new ResponseProxy();
        responseProxy.request = request;
        responseProxy.seqNr = seqNr;
        responseProxy.requested = false;
        for (final Pair<HttpRequestMatcher, List<ResponseProxy>> pair : requestMatchers) {
            if (pair.getLeft().match(request)) {
                pair.getRight().add(responseProxy);
                matched = true;
                break;
            }
        }
        if (!matched) {
            final HttpRequestMatcher matcher = new DefaultHttpRequestMatcher(request);
            final List<ResponseProxy> proxyList = new ArrayList<ResponseProxy>();
            proxyList.add(responseProxy);
            requestMatchers.add(Pair.of(matcher, proxyList));

        }

    }

    private class ResponseProxy {

        public HttpRequest request;
        public int seqNr;
        public boolean requested;

    }

}
