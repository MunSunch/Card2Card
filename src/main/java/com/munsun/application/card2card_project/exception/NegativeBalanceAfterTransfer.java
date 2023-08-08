package com.munsun.application.card2card_project.exception;

public class NegativeBalanceAfterTransfer extends TransferException {
    public NegativeBalanceAfterTransfer(String message, Long operationId) {
        super(message, operationId);
    }

    public NegativeBalanceAfterTransfer(long operationId) {
        this("Недостаточно средств на карте!", operationId);
    }
}
