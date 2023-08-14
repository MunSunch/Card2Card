package com.munsun.application.card2card_project.exception;

public class CardNotFoundException extends TransferException {
    public CardNotFoundException(String message, long operationId) {
        super(message, operationId);
    }

    public CardNotFoundException(long operationId) {
        this("Карта или карты не найдены", operationId);
    }

    public CardNotFoundException() {
        this("Карта или карты не найдены", 0L);
    }
}
