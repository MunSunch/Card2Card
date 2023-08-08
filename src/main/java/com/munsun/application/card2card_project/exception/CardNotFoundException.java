package com.munsun.application.card2card_project.exception;

public class CardNotFoundException extends TransferException {
    public CardNotFoundException(String message, long operationId) {
        super(message, operationId);
    }

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(long operationId) {
        this("Карты или карты не найдены", operationId);
    }

    public CardNotFoundException() {}
}
