package com.guilhermeleite.NotionCompass.domains.workspace.exceptions;

public class WorkspaceRequestException extends RuntimeException {
    public WorkspaceRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
