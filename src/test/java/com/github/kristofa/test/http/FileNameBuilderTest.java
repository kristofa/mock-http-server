package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileNameBuilderTest {

    @Test
    public void testGetFileName() {
        assertEquals("unique_request_00001.txt", FileNameBuilder.REQUEST_FILE_NAME.getFileName("unique", 1));
        assertEquals("unique2_request_entity_00002.txt", FileNameBuilder.REQUEST_ENTITY_FILE_NAME.getFileName("unique2", 2));
        assertEquals("unique3_response_00003.txt", FileNameBuilder.RESPONSE_FILE_NAME.getFileName("unique3", 3));
        assertEquals("unique4_response_entity_00004.txt",
            FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName("unique4", 4));
    }

}
