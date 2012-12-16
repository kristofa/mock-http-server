package com.github.kristofa.test.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

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
public class FileHttpRequestResponseLogger implements HttpRequestResponseLogger {

    private final String directory;
    private final String fileName;
    private final int seqNr;

    /**
     * Creates a new instance.
     * 
     * @param directory Target directory in which to store request/responses. Directory should already exist.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension.
     * @param seqNr Sequence number for request / response.
     */
    public FileHttpRequestResponseLogger(final String directory, final String fileName, final int seqNr) {
        Validate.notNull(directory);
        Validate.notBlank(fileName);

        this.directory = directory;
        this.fileName = fileName;
        this.seqNr = seqNr;
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
     * {@inheritDoc}
     */
    @Override
    public void log(final HttpRequest request) {

        try {
            writeRequest(seqNr, request);
            writeRequestEntity(seqNr, request);

        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(final HttpResponse response) {
        try {
            writeResponse(seqNr, response);
            writeResponseEntity(seqNr, response);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeRequest(final int seqNr, final HttpRequest request) throws IOException {
        final String fullFileName = FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr);

        final BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(directory, fullFileName)), "UTF-8"));

        try {
            writer.write("[Method]");
            writer.newLine();
            writer.write(request.getMethod().toString());
            writer.newLine();

            writer.write("[HttpMessageHeader]");
            writer.newLine();

            for (final HttpMessageHeader header : request.getHttpMessageHeaders()) {
                writer.write(header.getName() + "=" + header.getValue());
                writer.newLine();
            }

            writer.write("[Path]");
            writer.newLine();
            writer.write(request.getPath());
            writer.newLine();

            writer.write("[QueryParameters]");
            writer.newLine();

            for (final QueryParameter parameter : request.getQueryParameters()) {
                writer.write(parameter.getKey() + "=" + parameter.getValue());
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    private void writeRequestEntity(final int seqNr, final HttpRequest request) throws IOException {
        if (request.getContent() != null) {
            final String fullFileName = FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(fileName, seqNr);
            FileUtils.writeByteArrayToFile(new File(directory, fullFileName), request.getContent());
        }
    }

    private void writeResponse(final int seqNr, final HttpResponse response) throws IOException {
        final String fullFileName = FileNameBuilder.RESPONSE_FILE_NAME.getFileName(fileName, seqNr);
        final BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(directory, fullFileName)), "UTF-8"));
        try {
            writer.write("[HttpCode]");
            writer.newLine();
            writer.write(String.valueOf(response.getHttpCode()));
            writer.newLine();

            writer.write("[ContentType]");
            writer.newLine();
            writer.write(response.getContentType());
            writer.newLine();
        } finally {
            writer.close();
        }
    }

    private void writeResponseEntity(final int seqNr, final HttpResponse response) throws IOException {
        if (response.getContent() != null) {
            final String fullFileName = FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(fileName, seqNr);
            FileUtils.writeByteArrayToFile(new File(directory, fullFileName), response.getContent());
        }
    }
}
