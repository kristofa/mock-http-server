package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.github.kristofa.test.http.HttpRequestImpl;
import com.github.kristofa.test.http.Method;
import com.github.kristofa.test.http.file.HttpRequestFileWriterImpl;

public class HttpRequestFileWriterTest {

    private static final String UTF8 = "UTF8";

    private static final String FILE_NAME = "HttpRequestFileWriterTest.txt";
    private static final String FILE_NAME_ENTITY = "HttpRequestFileWriterTest_entity.txt";

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

    private HttpRequestFileWriterImpl writer;

    @Before
    public void setup() {
        writer = new HttpRequestFileWriterImpl();
    }

    @Test
    public void testWrite() throws IOException {
        final HttpRequestImpl httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.method(METHOD).httpMessageHeader(HEADER_NAME_1, HEADER_VALUE_1)
            .httpMessageHeader(HEADER_NAME_2, HEADER_VALUE_2).path(PATH).queryParameter(PARAM_NAME_1, PARAM_VALUE_1)
            .queryParameter(PARAM_NAME_2, PARAM_VALUE_2).content(CONTENT);

        final File requestFile = new File(TEMP_DIR, FILE_NAME);
        final File requestEntityFile = new File(TEMP_DIR, FILE_NAME_ENTITY);

        writer.write(httpRequestImpl, requestFile, requestEntityFile);

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

        assertEquals(expectedRequestFileContent, FileUtils.readFileToString(requestFile, UTF8));
        assertArrayEquals(CONTENT, FileUtils.readFileToByteArray(requestEntityFile));
    }

}
