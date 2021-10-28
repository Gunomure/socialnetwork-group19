package ru.skillbox.diplom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<CustomErrorResponse> handleEntityNotFoundException(Exception ex) {
        CustomErrorResponse error = new CustomErrorResponse();
        error.setErrorDescription(ErrorDetailType.BAD_CRED.getErrorType());
        error.setError(ErrorCommonType.INVALID_REQUEST.toString().toLowerCase());
//        error.setTimestamp(LocalDateTime.now());
//        error.setExceptionMessage(ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
