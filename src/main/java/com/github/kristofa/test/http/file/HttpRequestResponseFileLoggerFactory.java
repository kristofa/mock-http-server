package com.github.kristofa.test.http.file;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kristofa.test.http.HttpRequestResponseLogger;
import com.github.kristofa.test.http.HttpRequestResponseLoggerFactory;
import com.github.kristofa.test.http.LoggingHttpProxy;

/**
 * Factory that creates {@link HttpRequestResponseFileLogger} instances.
 * 
 * @see HttpRequestResponseFileLogger
 * @see LoggingHttpProxy
 * @author kristof
 */
public class HttpRequestResponseFileLoggerFactory implements HttpRequestResponseLoggerFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpRequestResponseFileLoggerFactory.class);

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final String directory;
    private final String fileName;
    private final HttpRequestFileWriter requestWriter;
    private final HttpResponseFileWriter responseWriter;
    private final boolean deleteExistingFiles;
    private boolean firstRequest = true;

    /**
     * Creates a new instance. We will not delete any existing files.
     * 
     * @param directory Target directory in which to store request/responses. Directory should already exist. Should not be
     *            <code>null</code> or blank.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension. Should not be <code>null</code> or blank.
     */
    public HttpRequestResponseFileLoggerFactory(final String directory, final String fileName) {
        this(directory, fileName, false);
    }

    /**
     * Creates a new instance.
     * 
     * @param directory Target directory in which to store request/responses. Directory should already exist. Should not be
     *            <code>null</code> or blank.
     * @param fileName Base file name. Should not contain extension. Will be suffixed with sequence number and .txt
     *            extension. Should not be <code>null</code> or blank.
     * @param deleteExistingFiles If value is <code>true</code> we will delete all existing files prior to logging new
     *            requests. This is often helpful because if we have less requests than before otherwise old files keep on
     *            lingering which can cause failing tests.
     */
    public HttpRequestResponseFileLoggerFactory(final String directory, final String fileName,
        final boolean deleteExistingFiles) {
        Validate.notBlank(directory);
        Validate.notBlank(fileName);
        this.directory = directory;
        this.fileName = fileName;
        this.deleteExistingFiles = deleteExistingFiles;
        requestWriter = new HttpRequestFileWriterImpl();
        responseWriter = new HttpResponseFileWriterImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestResponseLogger getHttpRequestResponseLogger() {

        if (firstRequest && deleteExistingFiles) {
            synchronized (atomicInteger) {
                if (firstRequest) {
                    deleteExistingFiles();
                    firstRequest = false;
                }

            }
        }

        return new HttpRequestResponseFileLogger(directory, fileName, atomicInteger.incrementAndGet(), requestWriter,
            responseWriter);
    }

    private void deleteExistingFiles() {

        int seqNr = 1;
        while (true) {
            final String requestFileName = FileNameBuilder.REQUEST_FILE_NAME.getFileName(fileName, seqNr);
            if (FileDeleteStatus.DID_NOT_EXIST.equals(delete(directory, requestFileName))) {
                return;
            }
            final String requestEntityFileName = FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(fileName, seqNr);
            delete(directory, requestEntityFileName);
            final String responseFileName = FileNameBuilder.RESPONSE_FILE_NAME.getFileName(fileName, seqNr);
            delete(directory, responseFileName);
            final String responseEntityFileName = FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(fileName, seqNr);
            delete(directory, responseEntityFileName);
            seqNr++;
        }
    }

    private FileDeleteStatus delete(final String directory, final String fileName) {
        final File file = new File(directory, fileName);
        if (file.exists()) {
            if (!file.delete()) {
                LOGGER.warn("Unable to delete " + file);
                return FileDeleteStatus.DELETE_FAILED;
            }
            return FileDeleteStatus.DELETE_SUCCES;
        }
        return FileDeleteStatus.DID_NOT_EXIST;
    }

    private enum FileDeleteStatus {
        DID_NOT_EXIST, DELETE_SUCCES, DELETE_FAILED
    };
}
