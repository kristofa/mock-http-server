package com.github.kristofa.test.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.FileUtils;

/**
 * Is able to write a {@link HttpRequest} to file. A written request can be read in again with
 * {@link HttpRequestFileReaderImpl}.
 * 
 * @see HttpRequestFileReaderImpl
 * @author kristof
 */
class HttpRequestFileWriterImpl implements HttpRequestFileWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final HttpRequest request, final File httpRequestFile, final File httpRequestEntityFile) {
        try {
            writeRequest(request, httpRequestFile);
            writeRequestEntity(request, httpRequestEntityFile);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeRequest(final HttpRequest request, final File requestFile) throws IOException {

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(requestFile), "UTF-8"));

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

    private void writeRequestEntity(final HttpRequest request, final File requestEntityFile) throws IOException {
        if (request.getContent() != null) {
            FileUtils.writeByteArrayToFile(requestEntityFile, request.getContent());
        }
    }

}
