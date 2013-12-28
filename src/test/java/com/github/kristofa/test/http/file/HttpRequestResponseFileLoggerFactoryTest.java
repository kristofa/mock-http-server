package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.kristofa.test.http.HttpRequestResponseLogger;
import com.github.kristofa.test.http.file.HttpRequestResponseFileLogger;
import com.github.kristofa.test.http.file.HttpRequestResponseFileLoggerFactory;

public class HttpRequestResponseFileLoggerFactoryTest {

    private final static String DIRECTORY = "/tmp";
    private final static String FILE_NAME = "testFile";

    @Test
    public void testGetHttpRequestResponseLogger() {
        final HttpRequestResponseFileLoggerFactory factory = new HttpRequestResponseFileLoggerFactory(DIRECTORY, FILE_NAME);
        final HttpRequestResponseLogger httpRequestResponseLogger = factory.getHttpRequestResponseLogger();
        assertNotNull(httpRequestResponseLogger);
        assertTrue(httpRequestResponseLogger instanceof HttpRequestResponseFileLogger);
        final HttpRequestResponseFileLogger fileLogger = (HttpRequestResponseFileLogger)httpRequestResponseLogger;
        assertEquals(DIRECTORY, fileLogger.getDirectory());
        assertEquals(FILE_NAME, fileLogger.getFileName());
        assertEquals("Seqnr starts at 1.", 1, fileLogger.getSeqNr());

        final HttpRequestResponseLogger httpRequestResponseLogger2 = factory.getHttpRequestResponseLogger();
        assertFalse("We expect a new instance with each request.", httpRequestResponseLogger2 == httpRequestResponseLogger);
        final HttpRequestResponseFileLogger fileLogger2 = (HttpRequestResponseFileLogger)httpRequestResponseLogger2;
        assertEquals(DIRECTORY, fileLogger2.getDirectory());
        assertEquals(FILE_NAME, fileLogger2.getFileName());
        assertEquals("We expect seqnr to increment by 1.", 2, fileLogger2.getSeqNr());

        assertSame("We expect that underlying request file writer is singleton.", fileLogger.getRequestFileWriter(),
            fileLogger2.getRequestFileWriter());
        assertSame("We expect that underlying response file writer is singleton.", fileLogger.getResponseFileWriter(),
            fileLogger2.getResponseFileWriter());

    }

}
