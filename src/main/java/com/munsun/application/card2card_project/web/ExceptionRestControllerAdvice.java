package com.munsun.application.card2card_project.web;

import com.munsun.application.card2card_project.dto.out.FailedTransferDtoOut;
import com.munsun.application.card2card_project.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionRestControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailedTransferDtoOut> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new FailedTransferDtoOut(message, 0L));
    }

    @ExceptionHandler(value = {CardNotFoundException.class, CardNotActiveException.class,
                               ErrorTransferCurrencyException.class, NegativeBalanceAfterTransfer.class,
                               InvalidConfirmException.class})
    public ResponseEntity<FailedTransferDtoOut> cardNotActiveExceptionHandler(TransferException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FailedTransferDtoOut(e.getMessage(), e.getOperationId()));
    }
}
