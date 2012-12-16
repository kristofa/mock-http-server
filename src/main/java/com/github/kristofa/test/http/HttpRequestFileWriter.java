package com.github.kristofa.test.http;

import java.io.File;

/**
 * Writes a {@link HttpRequest} to file.
 * 
 * @see HttpRequestFileReader
 * @author kristof
 */
public interface HttpRequestFileWriter {

    /**
     * Writes a {@link HttpRequest} to file.
     * 
     * @param request The http request to persist.
     * @param httpRequestFile File in which all http request properties will be stored except entity.
     * @param httpRequestEntityFile File in which http request entity will be stored. Entity is optional. If http request
     *            does not have entity this file will not be created.
     */
    void write(final HttpRequest request, final File httpRequestFile, final File httpRequestEntityFile);

}
