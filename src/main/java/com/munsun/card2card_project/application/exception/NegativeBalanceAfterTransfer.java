package com.munsun.card2card_project.application.exception;

public class NegativeBalanceAfterTransfer extends TransferException {
    public NegativeBalanceAfterTransfer(String message, Long operationId) {
        super(message, operationId);
    }

    public NegativeBalanceAfterTransfer(long operationId) {
        this("Недостаточно средств на карте!", operationId);
    }
}
