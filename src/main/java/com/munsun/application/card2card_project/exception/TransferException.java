package com.munsun.application.card2card_project.exception;

public class TransferException extends Exception{
    private long operationId;

    public TransferException(String message, long operationId) {
        super(message);
        this.operationId = operationId;
    }

    public TransferException() {
        this.operationId = 0;
    }

    public TransferException(String message) {
        super(message);
    }

    public long getOperationId() {
        return operationId;
    }
}
