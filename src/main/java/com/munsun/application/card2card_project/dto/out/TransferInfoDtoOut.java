package com.munsun.application.card2card_project.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferInfoDtoOut {
    private Long operationId;
    private String cardFromNumber;
    private String cardToNumber;
    @JsonProperty("amount")
    private AmountDtoOut amountDtoOut;
    private boolean status;
}
