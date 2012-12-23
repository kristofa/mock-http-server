package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileHttpRequestResponseLoggerFactoryTest {

    private final static String DIRECTORY = "/tmp";
    private final static String FILE_NAME = "testFile";

    @Test
    public void testGetHttpRequestResponseLogger() {
        final FileHttpRequestResponseLoggerFactory factory = new FileHttpRequestResponseLoggerFactory(DIRECTORY, FILE_NAME);
        final HttpRequestResponseLogger httpRequestResponseLogger = factory.getHttpRequestResponseLogger();
        assertNotNull(httpRequestResponseLogger);
        assertTrue(httpRequestResponseLogger instanceof FileHttpRequestResponseLogger);
        final FileHttpRequestResponseLogger fileLogger = (FileHttpRequestResponseLogger)httpRequestResponseLogger;
        assertEquals(DIRECTORY, fileLogger.getDirectory());
        assertEquals(FILE_NAME, fileLogger.getFileName());
        assertEquals("Seqnr starts at 1.", 1, fileLogger.getSeqNr());

        final HttpRequestResponseLogger httpRequestResponseLogger2 = factory.getHttpRequestResponseLogger();
        assertFalse("We expect a new instance with each request.", httpRequestResponseLogger2 == httpRequestResponseLogger);
        final FileHttpRequestResponseLogger fileLogger2 = (FileHttpRequestResponseLogger)httpRequestResponseLogger2;
        assertEquals(DIRECTORY, fileLogger2.getDirectory());
        assertEquals(FILE_NAME, fileLogger2.getFileName());
        assertEquals("We expect seqnr to increment by 1.", 2, fileLogger2.getSeqNr());

    }

}
