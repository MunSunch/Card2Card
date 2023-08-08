package com.munsun.application.card2card_project.exception;

public class ErrorTransferCurrencyException extends TransferException {
    public ErrorTransferCurrencyException(String message, long operationId) {
        super(message, operationId);
    }

    public ErrorTransferCurrencyException(long operationId) {
        this("Неверная валюта перевода, или карта не поддерживает переводы в данной валюте", operationId);
    }
}
