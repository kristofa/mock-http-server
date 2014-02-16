package com.github.kristofa.test.http.file;

import java.io.File;

import com.github.kristofa.test.http.HttpResponse;
import com.github.kristofa.test.http.HttpResponseProxy;

class FileHttpResponseProxy implements HttpResponseProxy {

    private final String directory;
    private final String filename;
    private final int seqNr;
    private boolean requested = false;
    private final HttpResponseFileReader httpResponseFileReader;

    public FileHttpResponseProxy(final String directory, final String filename, final int seqNr,
        final HttpResponseFileReader responseFileReader) {
        this.directory = directory;
        this.filename = filename;
        this.seqNr = seqNr;
        httpResponseFileReader = responseFileReader;
    }

    @Override
    public boolean alreadyRequested() {
        return requested;
    }

    @Override
    public HttpResponse getResponse() {
        final File responseFile = new File(directory, FileNameBuilder.RESPONSE_FILE_NAME.getFileName(filename, seqNr));
        final File responseEntityFile =
            new File(directory, FileNameBuilder.RESPONSE_ENTITY_FILE_NAME.getFileName(filename, seqNr));
        final HttpResponse response = httpResponseFileReader.read(responseFile, responseEntityFile);
        requested = true;
        return response;
    }

}
