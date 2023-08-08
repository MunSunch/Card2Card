package com.munsun.application.card2card_project.exception;

public class InvalidConfirmException extends TransferException {
    public InvalidConfirmException(String message, long operationId) {
        super(message, operationId);
    }

    public InvalidConfirmException(long operationId) {
        this("Неверный код", operationId);
    }
}
