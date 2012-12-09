package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpMessageHeaderFieldTest {

    @Test
    public void testGetValue() {
        assertEquals("Content-Type", HttpMessageHeaderField.CONTENTTYPE.getValue());
    }

}
