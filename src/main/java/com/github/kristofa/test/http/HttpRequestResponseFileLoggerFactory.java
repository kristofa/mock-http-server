package com.github.kristofa.test.http;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;

/**
 * Factory that creates {@link HttpRequestResponseFileLogger} instances.
 * 
 * @see HttpRequestResponseFileLogger
 * @see LoggingHttpProxy
 * @author kristof
 */
public class HttpRequestResponseFileLoggerFactory implements HttpRequestResponseLoggerFactory {

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final String directory;
    private final String fileName;

    /**
     * Creates a new instance.
     * 
     * @param directory Target directory in which to store request/responses. Directory should already exist. Should not be
     *            <code>null</code> or blank.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension. Should not be <code>null</code> or blank.
     */
    public HttpRequestResponseFileLoggerFactory(final String directory, final String fileName) {
        Validate.notBlank(directory);
        Validate.notBlank(fileName);
        this.directory = directory;
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestResponseLogger getHttpRequestResponseLogger() {
        return new HttpRequestResponseFileLogger(directory, fileName, atomicInteger.incrementAndGet());
    }
}
