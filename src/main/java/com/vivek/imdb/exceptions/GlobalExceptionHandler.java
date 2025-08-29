package com.vivek.imdb.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        return build(HttpStatus.BAD_REQUEST, exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/validation-guide"));
            problemDetail.setTitle("Validation Error");
            Map<String, String> errors =exception.getBindingResult().getFieldErrors()
                            .stream().collect(Collectors.toMap(FieldError::getField,
                            DefaultMessageSourceResolvable::getDefaultMessage,
                            (oldValue, newValue) -> oldValue));
            problemDetail.setProperty("errors", errors);
        });
    }

    @ExceptionHandler(CursorNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleCursorNotFoundException(CursorNotFoundException exception){
        return build(HttpStatus.NOT_FOUND, exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/cursor-guide"));
            problemDetail.setTitle("Cursor Validation Error");
            problemDetail.setDetail("Pass a valid cursor -should be the last one or restart again");
        });
    }

    @ExceptionHandler(InvalidYearException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidYear(InvalidYearException exception){
        return build(HttpStatus.NOT_ACCEPTABLE, exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/request-param-info-guide"));
            problemDetail.setTitle("Year Validation Error");
            problemDetail.setDetail("Pass valid year -should be the a number and not alpha numeric");
        });
    }

    @ExceptionHandler(InvalidCursorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleCursorInvalidException(InvalidCursorException exception){
        return build(HttpStatus.BAD_REQUEST, exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/cursor-guide"));
            problemDetail.setTitle("Cursor Validation Error");
            problemDetail.setDetail("Pass valid cursor - invalid cursor is passed which can't be parsed");
        });
    }

    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProblemDetail handleMovieNotException(MovieNotFoundException exception){
        return build(HttpStatus.BAD_REQUEST, exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/exception-guide"));
            problemDetail.setTitle("No Movie for this id");
        });
    }



    private ProblemDetail build(HttpStatus status, Exception ex, Consumer<ProblemDetail> consumer){
        var problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        consumer.accept(problem);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/contraint-guide"));
            problemDetail.setTitle("Constraint violation");
            problemDetail.setDetail("Pass valid parameters in url - Min for page and size is 0");

            Map<String, String> errors = ex.getConstraintViolations().stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (oldValue, newValue) -> oldValue
                    ));

            problemDetail.setProperty("errors", errors);
        });
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Unexpected error");
        problemDetail.setDetail("Something went wrong. Please contact support.");
        // log full stacktrace internally
        log.error("Unhandled exception", ex);
        return problemDetail;
    }

}
