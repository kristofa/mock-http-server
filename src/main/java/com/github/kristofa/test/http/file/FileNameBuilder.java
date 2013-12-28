package com.github.kristofa.test.http.file;

/**
 * File name builder which is used by {@link HttpRequestResponseFileLogger}.
 * 
 * @author kristof
 */
enum FileNameBuilder {

    /**
     * File name in which to store http request (except entity).
     */
    REQUEST_FILE_NAME("request"),
    /**
     * File name in which to store http request entity.
     */
    REQUEST_ENTITY_FILE_NAME("request_entity"),
    /**
     * File name in which to store http response.
     */
    RESPONSE_FILE_NAME("response"),
    /**
     * File name in which to store http response entity.
     */
    RESPONSE_ENTITY_FILE_NAME("response_entity");

    private String fileTypeNamePart;

    private FileNameBuilder(final String fileTypeNamePart) {
        this.fileTypeNamePart = fileTypeNamePart;
    }

    /**
     * Gets full file name.
     * 
     * @param uniqueFileNamePart When storing request/responses we want a unique part o the file name.
     * @param seqNr When storing multiple request/responses we want unique seq nr.
     * @return File name.
     */
    public String getFileName(final String uniqueFileNamePart, final int seqNr) {
        return uniqueFileNamePart + "_" + fileTypeNamePart + "_" + String.format("%05d", seqNr) + ".txt";
    }
}
