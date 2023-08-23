package com.munsun.card2card_project.application.exception;

public class CardNotActiveException extends TransferException {
    public CardNotActiveException(String message, long operationId) {
        super(message, operationId);
    }

    public CardNotActiveException(long operationId) {
        this("Карты или карты не активны", operationId);
    }
}
