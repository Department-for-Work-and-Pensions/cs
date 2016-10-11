package gov.dwp.carers.cs.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by peterwhitehead on 19/09/2016.
 */
@ControllerAdvice
public class CsExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String exception(Exception controllerException) throws Exception {
        LOGGER.error("Controller threw exception, error:" + controllerException.getMessage(), controllerException);
        throw controllerException;
    }
}
