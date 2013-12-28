package com.github.kristofa.test.http.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.FileUtils;

import com.github.kristofa.test.http.HttpResponse;

class HttpResponseFileWriterImpl implements HttpResponseFileWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final HttpResponse response, final File httpResponseFile, final File httpResponseEntityFile) {
        try {
            writeResponse(response, httpResponseFile);
            writeResponseEntity(response, httpResponseEntityFile);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeResponse(final HttpResponse httpResponse, final File httpResponseFile) throws IOException {
        final BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(httpResponseFile), "UTF-8"));
        try {
            writer.write("[HttpCode]");
            writer.newLine();
            writer.write(String.valueOf(httpResponse.getHttpCode()));
            writer.newLine();

            writer.write("[ContentType]");
            writer.newLine();
            writer.write(httpResponse.getContentType());
            writer.newLine();
        } finally {
            writer.close();
        }
    }

    private void writeResponseEntity(final HttpResponse httpResponse, final File httpResponseEntityFile) throws IOException {
        if (httpResponse.getContent() != null) {
            FileUtils.writeByteArrayToFile(httpResponseEntityFile, httpResponse.getContent());
        }
    }

}
