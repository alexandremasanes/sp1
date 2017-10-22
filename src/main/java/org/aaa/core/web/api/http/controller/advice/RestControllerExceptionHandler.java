package org.aaa.core.web.api.http.controller.advice;

import org.aaa.core.web.common.http.exception.CustomHttpExceptions.CommandNotValidatedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by alexandremasanes on 24/03/2017.
 */
@ControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CommandNotValidatedException.class)
    protected ResponseEntity<Object> handle(CommandNotValidatedException exception) {

        HttpStatus httpStatus;

        exception.printStackTrace();

        if(exception.getClass().isAnnotationPresent(ResponseStatus.class)) {
            httpStatus = exception.getClass().getAnnotation(ResponseStatus.class).value();
                return new ResponseEntity<>((exception).getErrors() , httpStatus);
        }
        else
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(httpStatus);
    }

    @ExceptionHandler(Exception.class)
    protected void handle() {
        System.out.println("a log amongst a thousand");
    }
}