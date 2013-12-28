package com.github.kristofa.test.http.file;

import java.io.File;

import org.apache.commons.lang3.Validate;

import com.github.kristofa.test.http.HttpRequest;
import com.github.kristofa.test.http.HttpRequestResponseLogger;
import com.github.kristofa.test.http.HttpResponse;

/**
 * {@link HttpRequestResponseLogger} that logs requests/responses to file. For each request/response that is logged it will
 * generate following files:
 * <ul>
 * <li>&lt;fileName&gt;_request_0000x.txt : Contains http method, headers, path and query parameters. Is readable (text
 * file), UTF-8 encoded. 0000x = sequence number that increments with each request.</li>
 * <li>&lt;fileName&gt;_request_entity_0000x.txt : Contains request entity if entity is available. If no entity is specified
 * file is not written. This is a binary file.</li>
 * <li>&lt;fileName&gt;_response_0000x.txt : Contains http code and Content Type. Is readable (text file), UTF-8 encoded.
 * 0000x = sequence number that increments with each request.</li>
 * <li>&lt;fileName&gt;_response_entity_0000x.txt : Contains response entity if entity is available. If no entity is
 * specified file is not written. This is a binary file.</li>
 * </ul>
 * It use {@link FileNameBuilder} to build these file names.
 * 
 * @see FileNameBuilder
 * @author kristof
 */
class HttpRequestResponseFileLogger implements HttpRequestResponseLogger {

    private final String directory;
    private final String fileName;
    private final int seqNr;
    private final HttpRequestFileWriter requestWriter;
    private final HttpResponseFileWriter responseWriter;

    /**
     * Creates a new instance.
     * 
     * @param directory Target directory in which to store request/responses. Directory should already exist.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension.
     * @param seqNr Sequence number for request / response.
     * @param requestWriter Responsible for writing http request to disk.
     * @param responseWriter Responsible for writing http response to disk.
     */
    public HttpRequestResponseFileLogger(final String directory, final String fileName, final int seqNr,
        final HttpRequestFileWriter requestWriter, final HttpResponseFileWriter responseWriter) {
        Validate.notNull(directory);
        Validate.notBlank(fileName);
        Validate.notNull(requestWriter);
        Validate.notNull(responseWriter);

        this.directory = directory;
        this.fileName = fileName;
        this.seqNr = seqNr;
        this.requestWriter = requestWriter;
        this.responseWriter = responseWriter;
    }

    /**
     * Gets the target directory in which to store request/responses.
     * 
     * @return the target directory in which to store request/responses.
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Gets the base file name.
     * 
     * @return Base file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the seqnr for request/response.
     * 
     * @return Seqnr for request/response.
     */
    public int getSeqNr() {
        return seqNr;
    }

    /**
     * Gets the {@link HttpRequestFileWriter} instance.
     * 
     * @return the {@link HttpRequestFileWriter} instance.
     */
    public HttpRequestFileWriter getRequestFileWriter() {
        return requestWriter;
    }

    /**
     * Gets the {@link HttpResponseFileWriter} instance.
     * 
     * @return the {@link HttpResponseFileWriter} instance.
     */
    public HttpResponseFileWriter getResponseFileWriter() {
        return responseWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(final HttpRequest request) {

        final String requestFileName = FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr);
        final String requestEntityFileName = FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(fileName, seqNr);

        requestWriter.write(request, new File(directory, requestFileName), new File(directory, requestEntityFileName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(final HttpResponse response) {

        final String responseFileName = FileNameBuilder.RESPONSE_FILE_NAME.getFileName(fileName, seqNr);
        final String responseEntityFileName = FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(fileName, seqNr);

        responseWriter.write(response, new File(directory, responseFileName), new File(directory, responseEntityFileName));
    }

}
