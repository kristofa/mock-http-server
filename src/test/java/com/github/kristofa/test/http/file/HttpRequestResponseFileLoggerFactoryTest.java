package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.test.http.HttpRequestImpl;
import com.github.kristofa.test.http.HttpRequestResponseLogger;
import com.github.kristofa.test.http.HttpResponseImpl;
import com.github.kristofa.test.http.Method;

public class HttpRequestResponseFileLoggerFactoryTest {

    private final static String DIRECTORY = "target/";
    private final static String FILE_NAME = "testFile";

    @Before
    public void setup() throws IOException {
        deleteFiles(1);
        deleteFiles(2);
    }

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

    @Test
    public void testDeleteExistingFiles() throws IOException {
        final List<File> files = new ArrayList<File>();
        files.addAll(createFiles(1));
        files.addAll(createFiles(2));

        final HttpRequestResponseFileLoggerFactory factory =
            new HttpRequestResponseFileLoggerFactory(DIRECTORY, FILE_NAME, true);
        assertNotNull(factory.getHttpRequestResponseLogger());

        for (final File file : files) {
            assertFalse("We expected all files to be deleted. Was not deleted: " + file, file.exists());
        }
    }

    @Test
    public void testDontDeleteExistingFiles() throws IOException {
        final List<File> files = new ArrayList<File>();
        files.addAll(createFiles(1));
        files.addAll(createFiles(2));

        final HttpRequestResponseFileLoggerFactory factory =
            new HttpRequestResponseFileLoggerFactory(DIRECTORY, FILE_NAME, false);
        assertNotNull(factory.getHttpRequestResponseLogger());

        for (final File file : files) {
            assertTrue("We expected all files to be still existing. Does not exist:" + file, file.exists());
        }
    }

    @Test
    public void testLogSingleRequestAfterDeleteExistingFiles() throws IOException {
        final List<File> files = new ArrayList<File>();
        files.addAll(createFiles(1));
        files.addAll(createFiles(2));

        final HttpRequestResponseFileLoggerFactory factory =
            new HttpRequestResponseFileLoggerFactory(DIRECTORY, FILE_NAME, true);
        final HttpRequestResponseLogger logger = factory.getHttpRequestResponseLogger();
        final HttpRequestImpl request = new HttpRequestImpl();
        request.method(Method.POST).path("/").content(new String("content").getBytes());
        logger.log(request);
        final HttpResponseImpl response = new HttpResponseImpl(200, "application/json", "{}".getBytes());
        logger.log(response);

        for (final File file : getFiles(1)) {
            assertTrue("Those files should exist, should be recreated." + file, file.exists());
        }
        for (final File file : getFiles(2)) {
            assertFalse("Those files should have been deleted." + file, file.exists());
        }
    }

    private List<File> createFiles(final int seqNr) throws IOException {
        final List<File> fileList = getFiles(seqNr);
        for (final File file : fileList) {
            file.createNewFile();
        }
        return fileList;
    }

    private List<File> deleteFiles(final int seqNr) throws IOException {
        final List<File> fileList = getFiles(seqNr);
        for (final File file : fileList) {
            if (file.exists() && !file.delete()) {
                throw new IllegalStateException("Unable to delete file " + file);
            }
        }
        return fileList;
    }

    private List<File> getFiles(final int seqNr) {
        final List<File> fileList = new ArrayList<File>();
        final String requestFileName = FileNameBuilder.REQUEST_FILE_NAME.getFileName(FILE_NAME, seqNr);
        fileList.add(new File(DIRECTORY, requestFileName));
        final String requestEntityFileName = FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName(FILE_NAME, seqNr);
        fileList.add(new File(DIRECTORY, requestEntityFileName));
        final String responseFileName = FileNameBuilder.RESPONSE_FILE_NAME.getFileName(FILE_NAME, seqNr);
        fileList.add(new File(DIRECTORY, responseFileName));
        final String responseEntityFileName = FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(FILE_NAME, seqNr);
        fileList.add(new File(DIRECTORY, responseEntityFileName));
        return fileList;
    }

}
