package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerValidationException(final ValidationException e) {
        log.info("409 {}", e.getMessage());
        return new ErrorResponse(409, "Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final NotFoundException e) {
        log.info("404 {}", e.getMessage());
        return new ErrorResponse(404, "Object not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalException(final InternalServerErrorException e) {
        log.info("500 {}", e.getMessage(), e);
        return new ErrorResponse(500, "Internal Server Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataExistExceptionException(DataExistException e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ErrorResponse(409, "Integrity Constraint Violation", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataExistExceptionException(BadRequestException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ErrorResponse(400, "Bad Request", e.getMessage());
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerUnsupportedState(final UnsupportedStateException e) {
        log.warn("500 {}", e.getMessage(), e);
        return new ErrorResponse(500, "Unsupported State", e.getMessage());
        //return new ErrorResponse(e.getMessage(), e.getMessage());
    }
}