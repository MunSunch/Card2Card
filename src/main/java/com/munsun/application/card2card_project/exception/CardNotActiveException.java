package com.munsun.application.card2card_project.exception;

public class CardNotActiveException extends TransferException {
    public CardNotActiveException(String message, long operationId) {
        super(message, operationId);
    }
}
