package com.github.kristofa.test.http.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

import com.github.kristofa.test.http.HttpResponse;
import com.github.kristofa.test.http.HttpResponseImpl;

/**
 * Builds a {@link HttpResponse} for which the content is stored on disk. It can reconstruct a {@link HttpResponse} which was
 * previously stored with {@link HttpResponseFileWriterImpl}.
 * 
 * @see HttpResponseFileWriterImpl
 * @author kristof
 */
class HttpResponseFileReaderImpl implements HttpResponseFileReader {

    private final static String HTTPCODE = "[HttpCode]";
    private final static String CONTENTTYPE = "[ContentType]";

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpResponse read(final File httpResponseFile, final File httpResponseEntityFile) {

        try {
            return readResponse(httpResponseFile, httpResponseEntityFile);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private HttpResponse readResponse(final File httpResponseFile, final File httpResponseEntityFile) throws IOException {
        final BufferedReader reader =
            new BufferedReader(new InputStreamReader(new FileInputStream(httpResponseFile), "UTF-8"));
        try {
            readNextLine(reader, HTTPCODE);
            final int httpCode = Integer.valueOf(reader.readLine());
            readNextLine(reader, CONTENTTYPE);
            final String contentType = reader.readLine();
            byte[] entity = null;
            if (httpResponseEntityFile.exists()) {
                entity = FileUtils.readFileToByteArray(httpResponseEntityFile);
            }
            return new HttpResponseImpl(httpCode, contentType, entity);
        } finally {
            reader.close();
        }
    }

    private String readNextLine(final BufferedReader reader, final String expectedValue) throws IOException {
        final String value = reader.readLine();
        if (!expectedValue.equals(value)) {
            throw new IllegalStateException("Unexpected value. Expected " + expectedValue + " but was " + value);
        }
        return value;
    }

}
