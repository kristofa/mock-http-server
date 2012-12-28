package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class HttpRequestFileReaderTest {

    private final static String TEST_FILE_DIRECTORY = "target/test-classes/";

    private HttpRequestFileReaderImpl reader;

    @Before
    public void setup() {
        reader = new HttpRequestFileReaderImpl();
    }

    @Test
    public void testReadValidRequestNoEntity() {

        final HttpRequest request =
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_valid_file.txt"), new File(
                "unexisting_file.txt"));
        assertEquals(Method.POST, request.getMethod());
        assertEquals("/a/b", request.getPath());
        final Set<HttpMessageHeader> httpMessageHeaders = request.getHttpMessageHeaders();
        assertEquals(2, httpMessageHeaders.size());
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Content-Type", "application/json")));
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Agent", "Eclipse")));
        final Set<QueryParameter> queryParameters = request.getQueryParameters();
        assertEquals(3, queryParameters.size());
        assertTrue(queryParameters.contains(new QueryParameter("a", "b")));
        assertTrue(queryParameters.contains(new QueryParameter("a", "c")));
        assertTrue(queryParameters.contains(new QueryParameter("c", "d")));
        assertNull(request.getContent());
    }

    @Test
    public void testReadValidRequestWithEntity() {

        final HttpRequest request =
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_valid_file.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_entity_valid_file.txt"));
        assertEquals(Method.POST, request.getMethod());
        assertEquals("/a/b", request.getPath());
        final Set<HttpMessageHeader> httpMessageHeaders = request.getHttpMessageHeaders();
        assertEquals(2, httpMessageHeaders.size());
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Content-Type", "application/json")));
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Agent", "Eclipse")));
        final Set<QueryParameter> queryParameters = request.getQueryParameters();
        assertEquals(3, queryParameters.size());
        assertTrue(queryParameters.contains(new QueryParameter("a", "b")));
        assertTrue(queryParameters.contains(new QueryParameter("a", "c")));
        assertTrue(queryParameters.contains(new QueryParameter("c", "d")));
        assertNotNull(request.getContent());
    }

    @Test
    public void testReadValidRequestWithQueryParamWithEmptyValue() {
        final HttpRequest request =
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_empty_query_param_value.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_entity_valid_file.txt"));
        assertEquals(Method.POST, request.getMethod());
        assertEquals("/a/b", request.getPath());
        final Set<HttpMessageHeader> httpMessageHeaders = request.getHttpMessageHeaders();
        assertEquals(2, httpMessageHeaders.size());
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Content-Type", "application/json")));
        assertTrue(httpMessageHeaders.contains(new HttpMessageHeader("Agent", "Eclipse")));
        final Set<QueryParameter> queryParameters = request.getQueryParameters();
        assertEquals(3, queryParameters.size());
        assertTrue(queryParameters.contains(new QueryParameter("a", "b")));
        assertTrue(queryParameters.contains(new QueryParameter("a", "c")));
        assertTrue(queryParameters.contains(new QueryParameter("c", "")));
        assertNotNull(request.getContent());

    }

    @Test
    public void testReadInvalidFile_NotStartWithMethod() {
        try {
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_invalidFile1.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_entity_valid_file.txt"));
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals("Unexpected value. Expected [Method] but was [HttpMessageHeader]", e.getMessage());
        }
    }

    @Test
    public void testReadInvalidFile_EndOfFileAfterHttpMessageHeader() {
        try {
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_invalidFile2.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_entity_valid_file.txt"));
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals("Expected [Path] after [HttpMessageHeader] but got null", e.getMessage());
        }
    }

    @Test
    public void testReadInvalidFile_UnexpectedContentAfterQueryParams() {
        try {
            reader.read(new File(TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_invalidFile3.txt"), new File(
                TEST_FILE_DIRECTORY, "HttpRequestFileReaderTest_entity_valid_file.txt"));
            fail("Expected exception.");
        } catch (final IllegalStateException e) {
            assertEquals("Expected nothing after [QueryParameters] but got [UnexpectedSection]", e.getMessage());
        }
    }
}
