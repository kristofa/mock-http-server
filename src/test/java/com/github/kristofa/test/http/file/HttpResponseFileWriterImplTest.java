package com.github.kristofa.test.http.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.github.kristofa.test.http.HttpResponseImpl;
import com.github.kristofa.test.http.file.HttpResponseFileWriterImpl;

public class HttpResponseFileWriterImplTest {

    private static final String UTF8 = "UTF8";

    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private final static String HTTP_RESPONSE_FILENAME = "HttpResponseFileWriterImplTest.txt";
    private final static String HTTP_RESPONSE_ENTITY_FILENAME = "HttpResponseFileWriterImplTest_entity.txt";

    private static final int HTTP_CODE = 200;
    private static final String CONTENTTYPE = "contentType";
    private static final byte[] RESPONSE_CONTENT = new String("responseContent").getBytes();

    @Test
    public void testWrite() throws IOException {
        final HttpResponseImpl httpResponseImpl = new HttpResponseImpl(HTTP_CODE, CONTENTTYPE, RESPONSE_CONTENT);

        final File responseFile = new File(TEMP_DIR, HTTP_RESPONSE_FILENAME);
        final File responseEntityFile = new File(TEMP_DIR, HTTP_RESPONSE_ENTITY_FILENAME);

        final HttpResponseFileWriterImpl writer = new HttpResponseFileWriterImpl();
        writer.write(httpResponseImpl, responseFile, responseEntityFile);

        final String expectedResponseFileContent = "[HttpCode]\n" + //
            HTTP_CODE + "\n" + //
            "[ContentType]\n" + //
            CONTENTTYPE + "\n";

        assertEquals(expectedResponseFileContent, FileUtils.readFileToString(responseFile, UTF8));
        assertArrayEquals(RESPONSE_CONTENT, FileUtils.readFileToByteArray(responseEntityFile));
    }

}
