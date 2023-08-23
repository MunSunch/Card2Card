package com.munsun.card2card_project.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferInfo {
    private Long operationId;
    private Card cardFrom;
    private Card cardTo;
    private String currency;
    private Long value;
    private boolean status;

    public TransferInfo(TransferInfo anotherTransferInfo) {
        this.operationId = anotherTransferInfo.getOperationId();
        this.cardFrom = new Card(anotherTransferInfo.getCardFrom());
        this.cardTo = new Card(anotherTransferInfo.getCardTo());
        this.currency = anotherTransferInfo.getCurrency();
        this.value = anotherTransferInfo.getValue();
        this.status = anotherTransferInfo.isStatus();
    }
}
