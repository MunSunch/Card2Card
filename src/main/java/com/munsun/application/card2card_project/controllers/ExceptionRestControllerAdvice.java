package com.munsun.application.card2card_project.controllers;

import com.munsun.application.card2card_project.dto.out.FailedTransferDtoOut;
import com.munsun.application.card2card_project.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionRestControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<FailedTransferDtoOut> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new FailedTransferDtoOut(message, 0L));
    }

    @ExceptionHandler(value = {CardNotFoundException.class, CardNotActiveException.class,
                               ErrorTransferCurrencyException.class, NegativeBalanceAfterTransfer.class,
                               InvalidConfirmException.class, TransferNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<FailedTransferDtoOut> transferExceptionHandler(TransferException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FailedTransferDtoOut(e.getMessage(), e.getOperationId()));
    }
}
