package com.harlap.test.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class QueryParameterTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private QueryParameter parameter;

    @Before
    public void setup() {
        parameter = new QueryParameter(KEY, VALUE);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullKey() {
        new QueryParameter(null, "value");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullValue() {
        new QueryParameter("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyKey() {
        new QueryParameter("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptyValue() {
        new QueryParameter("key", "");
    }

    @Test
    public void testGetKey() {
        assertEquals(KEY, parameter.getKey());
    }

    @Test
    public void testGetValue() {
        assertEquals(VALUE, parameter.getValue());
    }

    @Test
    public void testToString() {
        assertEquals(KEY + "=" + VALUE, parameter.toString());
    }

    @Test
    public void testEquals() {
        assertFalse(parameter.equals(null));
        assertFalse(parameter.equals(new String()));
        assertTrue(parameter.equals(parameter));

        final QueryParameter equalParameter = new QueryParameter(KEY, VALUE);

        assertTrue(parameter.equals(equalParameter));

        final QueryParameter parameterDifferentKey = new QueryParameter(KEY + "1", VALUE);
        assertFalse(parameter.equals(parameterDifferentKey));

        final QueryParameter parameterDifferentValue = new QueryParameter(KEY, VALUE + "2");
        assertFalse(parameter.equals(parameterDifferentValue));

    }

    @Test
    public void testHashCode() {
        final QueryParameter equalParameter = new QueryParameter(KEY, VALUE);
        assertEquals("Equal objects should have equal hashcode.", parameter.hashCode(), equalParameter.hashCode());
    }

}
