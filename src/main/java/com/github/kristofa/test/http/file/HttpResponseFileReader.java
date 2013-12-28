package com.github.kristofa.test.http.file;

import java.io.File;

import com.github.kristofa.test.http.HttpResponse;

/**
 * Reads a {@link HttpResponse} from file and reconstructs it.
 * 
 * @see HttpResponseFileWriter
 * @author kristof
 */
public interface HttpResponseFileReader {

    /**
     * Reqds a {@link HttpResponse} that is stored in file(s) and reconstructs it.
     * 
     * @param httpResponseFile File in which all http response properties are stored except entity.
     * @param httpResponseEntityFile File in which http response entity is stored. Entity is optional. If given file does not
     *            exist entity in returned response will be <code>null</code>.
     * @return Initialized Http Response.
     */
    HttpResponse read(final File httpResponseFile, final File httpResponseEntityFile);

}
