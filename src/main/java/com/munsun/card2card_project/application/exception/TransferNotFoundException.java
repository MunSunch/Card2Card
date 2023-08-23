package com.munsun.card2card_project.application.exception;

public class TransferNotFoundException extends TransferException{
    public TransferNotFoundException(String message, long operationId) {
        super(message, operationId);
    }

    public TransferNotFoundException(long operationId) {
        this("Транзакция не найдена", operationId);
    }

    public TransferNotFoundException() {}
}
