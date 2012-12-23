package com.github.kristofa.test.http;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;

/**
 * Factory that creates {@link FileHttpRequestResponseLogger} instances.
 * 
 * @see FileHttpRequestResponseLogger
 * @see LoggingHttpProxy
 * @author kristof
 */
public class FileHttpRequestResponseLoggerFactory implements HttpRequestResponseLoggerFactory {

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
    public FileHttpRequestResponseLoggerFactory(final String directory, final String fileName) {
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
        return new FileHttpRequestResponseLogger(directory, fileName, atomicInteger.incrementAndGet());
    }
}
