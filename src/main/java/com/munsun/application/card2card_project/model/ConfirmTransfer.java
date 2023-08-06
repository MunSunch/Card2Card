package com.munsun.application.card2card_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmTransfer {
    private TransferInfo transferInfo;
    private Long codeConfirm;
}
