package com.munsun.application.card2card_project.exception;

public class TransferNotFoundException extends TransferException{
    public TransferNotFoundException(String message, long operationId) {
        super(message, operationId);
    }

    public TransferNotFoundException(long operationId) {
        this("Транзакция не найдена", operationId);
    }

    public TransferNotFoundException() {}
}
