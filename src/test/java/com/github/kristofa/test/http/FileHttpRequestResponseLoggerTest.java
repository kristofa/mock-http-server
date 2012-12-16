package com.github.kristofa.test.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileHttpRequestResponseLoggerTest {

    private static final String UTF8 = "UTF8";

    private static final String FILE_NAME = "testLog";

    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private final static Method METHOD = Method.POST;
    private final static String HEADER_NAME_1 = "headerName1";
    private final static String HEADER_VALUE_1 = "headerValue1";
    private final static String HEADER_NAME_2 = "headerName2";
    private final static String HEADER_VALUE_2 = "headerValue2";
    private final static String PATH = "/path";
    private final static String PARAM_NAME_1 = "param1";
    private final static String PARAM_VALUE_1 = "paramValue1";
    private final static String PARAM_NAME_2 = "param2";
    private final static String PARAM_VALUE_2 = "paramValue2";
    private static final byte[] CONTENT = new String("content").getBytes();
    private static final int HTTP_CODE = 200;
    private static final String CONTENTTYPE = "contentType";
    private static final byte[] RESPONSE_CONTENT = new String("responseContent").getBytes();

    private final static int SEQ_NR = 10;

    @Test(expected = NullPointerException.class)
    public void testFileHttpRequestResponseLogger() {
        new FileHttpRequestResponseLogger(null, "test", SEQ_NR);
    }

    @Test
    public void testLogRequest() throws IOException {
        final FileHttpRequestResponseLogger logger = new FileHttpRequestResponseLogger(TEMP_DIR, FILE_NAME, SEQ_NR);

        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(METHOD).httpMessageHeader(HEADER_NAME_1, HEADER_VALUE_1)
            .httpMessageHeader(HEADER_NAME_2, HEADER_VALUE_2).path(PATH).queryParameter(PARAM_NAME_1, PARAM_VALUE_1)
            .queryParameter(PARAM_NAME_2, PARAM_VALUE_2).content(CONTENT);

        logger.log(httpRequestImpl);

        final String expectedRequestFileContent = "[Method]\n" + //
            METHOD + "\n" + //
            "[HttpMessageHeader]\n" + //
            HEADER_NAME_1 + "=" + HEADER_VALUE_1 + "\n" + //
            HEADER_NAME_2 + "=" + HEADER_VALUE_2 + "\n" + //
            "[Path]\n" + //
            PATH + "\n" + //
            "[QueryParameters]\n" + //
            PARAM_NAME_1 + "=" + PARAM_VALUE_1 + "\n" + //
            PARAM_NAME_2 + "=" + PARAM_VALUE_2 + "\n";

        final File expectedRequestFile1 = new File(TEMP_DIR, FILE_NAME + "_request_00010.txt");
        final File expectedRequestEntityFile1 = new File(TEMP_DIR, FILE_NAME + "_request_entity_00010.txt");
        assertEquals(expectedRequestFileContent, FileUtils.readFileToString(expectedRequestFile1, UTF8));
        assertArrayEquals(CONTENT, FileUtils.readFileToByteArray(expectedRequestEntityFile1));

    }

    @Test
    public void testLogResponse() throws IOException {
        final FileHttpRequestResponseLogger logger = new FileHttpRequestResponseLogger(TEMP_DIR, FILE_NAME, SEQ_NR);

        final HttpResponseImpl httpResponseImpl = new HttpResponseImpl(HTTP_CODE, CONTENTTYPE, RESPONSE_CONTENT);

        logger.log(httpResponseImpl);

        final String expectedResponseFileContent = "[HttpCode]\n" + //
            HTTP_CODE + "\n" + //
            "[ContentType]\n" + //
            CONTENTTYPE + "\n";

        final File expectedResponseFile1 = new File(TEMP_DIR, FILE_NAME + "_response_00010.txt");
        final File expectedResponseEntityFile1 = new File(TEMP_DIR, FILE_NAME + "_response_entity_00010.txt");
        assertEquals(expectedResponseFileContent, FileUtils.readFileToString(expectedResponseFile1, UTF8));
        assertArrayEquals(RESPONSE_CONTENT, FileUtils.readFileToByteArray(expectedResponseEntityFile1));

    }
}
