package com.github.kristofa.test.http;

/**
 * The content of a HttpRequest or response can be structured data like JSON / XML.
 *
 * {@link HttpRequestImpl} treats content as a byte array by default. However in the case JSON / XML you can have a non equal
 * byte array which still gives you the same representation: for example different order of properties in json..
 *
 * To be able to have custom equals/hashcode logic you can set your own {@link ContentMatcher} when building a
 * {@link HttpRequestImpl}.
 * 
 * @see HttpRequestImpl
 * @author kristof
 */
public abstract class ContentMatcher {

    /**
     * Gets content.
     * 
     * @return Content.
     */
    abstract public byte[] getContent();

    /**
     * Sets content.
     * 
     * @param content Content.
     * @throws UnexpectedContentException In case we don't expect given content. This matcher is incompatible with given
     *             content.
     */
    abstract public void setContent(byte[] content) throws UnexpectedContentException;

    /**
     * {@inheritDoc}
     */
    @Override
    abstract public boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    abstract public int hashCode();

    /**
     * Create a copy of this ContentProxy.
     * 
     * @return Copy of this content proxy.
     */
    abstract public ContentMatcher copy();

}
