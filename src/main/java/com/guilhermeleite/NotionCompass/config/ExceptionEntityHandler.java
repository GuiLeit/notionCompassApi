package com.guilhermeleite.NotionCompass.config;

import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.PagesRequestException;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.WorkspaceRequestException;
import com.guilhermeleite.NotionCompass.dtos.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionEntityHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionEntityHandler.class);

    @ExceptionHandler(WorkspaceRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleWorkspaceRequestException(WorkspaceRequestException e) {
        log.error("Workspace request error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(PagesRequestException.class)
    public ResponseEntity<ErrorResponseDto> handlePagesRequestException(PagesRequestException e) {
        log.error("Pages request error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(e.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(NoHandlerFoundException e) {
        log.error("Path not found: {}", e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("Path not found: " + e.getRequestURL()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        log.error("Non mapped error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("An unexpected error occurred: " + e.getMessage()));
    }
}
