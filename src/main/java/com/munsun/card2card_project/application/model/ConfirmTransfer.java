package com.munsun.card2card_project.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmTransfer {
    private TransferInfo transferInfo;
    private Long codeConfirm;

    public ConfirmTransfer(ConfirmTransfer anotherConfirmTransfer) {
        this.transferInfo = new TransferInfo(anotherConfirmTransfer.getTransferInfo());
        this.codeConfirm = anotherConfirmTransfer.getCodeConfirm();
    }
}
