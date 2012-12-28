package com.github.kristofa.test.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Builds a HttpRequest for which the content is stored on disk. It can reconstruct a HttpRequest which was previously stored
 * with {@link HttpRequestFileWriterImpl}.
 * 
 * @see HttpRequestFileWriterImpl
 * @author kristof
 */
class HttpRequestFileReaderImpl implements HttpRequestFileReader {

    private static final String QUERY_PARAMETERS = "[QueryParameters]";
    private static final String PATH = "[Path]";
    private static final String HTTP_MESSAGE_HEADER = "[HttpMessageHeader]";
    private static final String METHOD = "[Method]";

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest read(final File httpRequestFile, final File httpRequestEntityFile) {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        try {
            readRequest(httpRequestImpl, httpRequestFile, httpRequestEntityFile);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return httpRequestImpl;
    }

    private void readRequest(final HttpRequestImpl request, final File requestFile, final File requestEntityFile)
        throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(requestFile), "UTF-8"));
        try {
            readNextLine(reader, METHOD);
            final String method = reader.readLine();
            request.method(Method.valueOf(method));
            readNextLine(reader, HTTP_MESSAGE_HEADER);
            final List<KeyValuePair> httpMessageHeaderValues = new ArrayList<KeyValuePair>();
            final String nextSection = readKeyValuePairs(reader, httpMessageHeaderValues);
            if (!PATH.equals(nextSection)) {
                throw new IllegalStateException("Expected " + PATH + " after " + HTTP_MESSAGE_HEADER + " but got "
                    + nextSection);
            }
            for (final KeyValuePair keyValuePair : httpMessageHeaderValues) {
                request.httpMessageHeader(keyValuePair.key, keyValuePair.value);
            }
            final String path = reader.readLine();
            request.path(path);
            readNextLine(reader, QUERY_PARAMETERS);
            final List<KeyValuePair> queryParams = new ArrayList<KeyValuePair>();
            final String nextSection2 = readKeyValuePairs(reader, queryParams);
            if (nextSection2 != null) {
                throw new IllegalStateException("Expected nothing after " + QUERY_PARAMETERS + " but got " + nextSection2);
            }
            for (final KeyValuePair keyValuePair : queryParams) {
                request.queryParameter(keyValuePair.key, keyValuePair.value);
            }

            if (requestEntityFile.exists()) {
                final byte[] entity = FileUtils.readFileToByteArray(requestEntityFile);
                request.content(entity);
            }

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

    private String readKeyValuePairs(final BufferedReader reader, final List<KeyValuePair> properties) throws IOException {
        String newLine = null;
        while ((newLine = reader.readLine()) != null) {
            if (newLine.indexOf("=") != -1) {
                final String[] split = newLine.split("=");
                final KeyValuePair pair = new KeyValuePair();
                if (split.length == 2) {
                    pair.key = split[0];
                    pair.value = split[1];
                } else {
                    // Expect length to be 1. Key with empty value.
                    pair.key = split[0];
                    pair.value = "";
                }
                properties.add(pair);
            } else {
                return newLine;
            }
        }
        return null;
    }

    private class KeyValuePair {

        public String key;
        public String value;

    }

}
