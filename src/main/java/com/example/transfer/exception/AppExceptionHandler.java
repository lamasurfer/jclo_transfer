package com.example.transfer.exception;

import com.example.transfer.model.response.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionHandler.class);
    private final MessageSourceAccessor messages;

    public AppExceptionHandler(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final String message = messages.getMessage("bad.request.message");
        LOGGER.info("Exception " + e.getClass().getSimpleName() + ": " + message);
        return ResponseEntity.badRequest().body(new ErrorMessage(message));
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorMessage> handleTransactionException(TransactionException e) {
        final String message = e.getMessage();
        LOGGER.info("Exception " + e.getClass().getSimpleName() + ": " + message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(message));
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ErrorMessage> handleAccountException(AccountException e) {
        final String message = e.getMessage();
        LOGGER.info("Exception " + e.getClass().getSimpleName() + ": " + message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final BindingResult result = e.getBindingResult();
        final String message = result.getAllErrors()
                .stream()
                .map(messages::getMessage)
                .sorted()
                .collect(Collectors.joining(", "));
        LOGGER.info("Exception " + e.getClass().getSimpleName() + ": " + message);
        return ResponseEntity.badRequest().body(new ErrorMessage(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllExceptions(Exception e) {
        final String message = messages.getMessage("unexpected.error");
        LOGGER.warn("Exception " + e.getClass().getSimpleName() + ": " + message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(message));
    }
}
