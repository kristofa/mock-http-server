package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.test.http.HttpResponse;

public class HttpResponseFileReaderImplTest {

    private final static String TEST_FILE_DIRECTORY = "target/test-classes/";

    private HttpResponseFileReaderImpl reader;

    @Before
    public void setup() {
        reader = new HttpResponseFileReaderImpl();
    }

    @Test
    public void testReadValidFile() {
        final HttpResponse response =
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_valid_file.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_entity_valid_file.txt"));
        assertEquals(200, response.getHttpCode());
        assertEquals("application/json", response.getContentType());
        assertNotNull(response.getContent());
    }

    @Test
    public void testReadValidFileNoEntityAndNoContentType() {
        final HttpResponse response =
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_valid_file_no_contenttype.txt"),
                new File(TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_no_entity.txt"));
        assertEquals(200, response.getHttpCode());
        assertNull(response.getContentType());
        assertNull(response.getContent());
    }

    @Test
    public void testReadInvalidFile() {

        try {
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_invalid_file.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpResponseFileReaderImplTest_entity_invalid_file.txt"));
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals("Unexpected value. Expected [HttpCode] but was 200", e.getMessage());
        }
    }

}
