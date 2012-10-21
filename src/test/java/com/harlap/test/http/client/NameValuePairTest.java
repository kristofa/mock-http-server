package com.harlap.test.http.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NameValuePairTest {

	private final static String NAME = "Name";
	private final static String VALUE = "Value";
	
	private NameValuePair nameValuePair;
	
	@Before
	public void setup() {
		nameValuePair = new NameValuePair(NAME, VALUE);
	}
	

	@Test(expected=IllegalArgumentException.class)
	public void testNameValuePairEmptyName() {
		new NameValuePair("", VALUE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNameValuePairEmptyValue() {
		new NameValuePair(NAME, "");
	}

	@Test
	public void testGetName() {
		assertEquals(NAME, nameValuePair.getName());
	}

	@Test
	public void testGetValue() {
		assertEquals(VALUE, nameValuePair.getValue());
	}

	@Test
	public void testEqualsObject() {
		final NameValuePair equalNameValuePair = new NameValuePair(NAME, VALUE);
		final NameValuePair nonEqualNameValuePair1 = new NameValuePair(NAME + "a", VALUE);
		final NameValuePair nonEqualNameValuePair2 = new NameValuePair(NAME, VALUE + "b");
		
		assertFalse(nameValuePair.equals(null));
		assertFalse(nameValuePair.equals(new String()));
		assertFalse(nameValuePair.equals(nonEqualNameValuePair1));
		assertFalse(nameValuePair.equals(nonEqualNameValuePair2));
		assertTrue(nameValuePair.equals(nameValuePair));
		assertTrue(nameValuePair.equals(equalNameValuePair));
		
	}

	@Test
	public void testHashCode() {
		final NameValuePair equalNameValuePair = new NameValuePair(NAME, VALUE);
		assertEquals("Hashcode of equal objects should be equal.", nameValuePair.hashCode(), equalNameValuePair.hashCode());
	}
}
