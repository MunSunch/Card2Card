package com.munsun.application.card2card_project.model;

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
}
