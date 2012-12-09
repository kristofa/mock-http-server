package com.github.kristofa.test.http;

/**
 * List of often used http message header fields.
 * 
 * @author kristof
 */
public enum HttpMessageHeaderField {

    /**
     * The Content-Type header field defines {@link MediaType} of the request body.
     * <p>
     * Used in POST and PUT requests and responses.
     */
    CONTENTTYPE("Content-Type");

    private String value;

    private HttpMessageHeaderField(final String value) {
        this.value = value;
    }

    /**
     * Gets text value of http message header field.
     * 
     * @return Text value of http message header field.
     */
    public String getValue() {
        return value;
    }

}
