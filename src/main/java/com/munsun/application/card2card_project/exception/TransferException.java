package com.munsun.application.card2card_project.exception;

public class TransferException extends Exception{
    private final long operationId;

    public TransferException(String message, long operationId) {
        super(message);
        this.operationId = operationId;
    }

    public TransferException() {
        this.operationId = 0;
    }

    public long getOperationId() {
        return operationId;
    }
}
