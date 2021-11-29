package ru.skillbox.diplom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, BadRequestException.class})
    protected ResponseEntity<CustomErrorResponse> handleEntityNotFoundException(Exception ex) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setError(ErrorCommonType.INVALID_REQUEST.toString().toLowerCase());
        error.setErrorDescription(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<CustomErrorResponse> handleUnauthorizedException(Exception ex) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setError(ErrorCommonType.UNAUTHORIZED.toString().toLowerCase());
        error.setErrorDescription(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
