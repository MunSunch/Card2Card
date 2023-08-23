package com.munsun.card2card_project.application.exception;

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
