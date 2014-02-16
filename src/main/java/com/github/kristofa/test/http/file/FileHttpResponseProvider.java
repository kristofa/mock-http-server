package com.github.kristofa.test.http.file;

import java.io.File;

import com.github.kristofa.test.http.AbstractHttpResponseProvider;
import com.github.kristofa.test.http.HttpRequest;
import com.github.kristofa.test.http.HttpResponseProvider;
import com.github.kristofa.test.http.LoggingHttpProxy;

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
public class FileHttpResponseProvider extends AbstractHttpResponseProvider {

    private final String directory;
    private final String fileName;
    private final HttpRequestFileReader httpRequestFileReader;
    private final HttpResponseFileReader httpResponseFileReader;

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
    protected void lazyInitializeExpectedRequestsAndResponses() {
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
            submitRequest(fileName, seqNr);
            seqNr++;
            requestFile = new File(directory, FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr));
        }
    }

    private void submitRequest(final String fileName, final int seqNr) {

        final File requestFile = new File(directory, FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr));
        final File requestEntityFile =
            new File(directory, FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(fileName, seqNr));
        final HttpRequest request = httpRequestFileReader.read(requestFile, requestEntityFile);
        final FileHttpResponseProxy responseProxy =
            new FileHttpResponseProxy(directory, fileName, seqNr, httpResponseFileReader);
        addExpected(request, responseProxy);
    }

}
