package com.github.kristofa.test.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MediaTypeTest {

    @Test
    public void getValue() {
        assertEquals("application/json; charset=UTF-8", MediaType.APPLICATION_JSON_UTF8.getValue());
    }

}
