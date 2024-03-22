package com.project.durumoongsil.teutoo.exception;

import com.project.durumoongsil.teutoo.common.RestError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlingValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserUnauthorizedException.class)
    public RestError handlingAuthorizationException(UserUnauthorizedException ex) {
        return new RestError(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotFoundUserException.class)
    public RestError handlingNotFoundUserException(NotFoundUserException ex) {
        return new RestError(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PtProgramNotFoundException.class)
    public RestError handlingPtProgramNotFoundException(PtProgramNotFoundException ex) {
        return new RestError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TrainerInfoNotFoundException.class)
    public RestError handlingTrainerInfoNotFoundException(TrainerInfoNotFoundException ex) {
        return new RestError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateEmailException.class)
    public RestError handlingDuplicationEmailException(DuplicateEmailException ex) {
        return new RestError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEstimateException.class)
    public RestError handlingDuplicateEstimateException(DuplicateEstimateException ex) {
        return new RestError(HttpStatus.CONFLICT.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnauthorizedActionException.class)
    public RestError handlingDuplicateEstimateException(UnauthorizedActionException ex) {
        return new RestError(HttpStatus.FORBIDDEN.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RestError handlingMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return new RestError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ScheduleConflictException.class)
    public RestError handlingScheduleConflictException(ScheduleConflictException ex) {
        return new RestError(HttpStatus.CONFLICT.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidActionException.class)
    public RestError handlingInvalidActionException(InvalidActionException ex) {
        return new RestError(HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

}
