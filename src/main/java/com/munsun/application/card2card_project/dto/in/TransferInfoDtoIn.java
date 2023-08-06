package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferInfoDtoIn {
    @NotBlank(message = "Номер карты пустой!")
    private String cardFromNumber;

    @NotBlank(message = "Срок действия карты пустой!")
    private String cardFromValidTill;

    @NotBlank(message = "CVV карты пустой!")
    private String cardFromCVV;

    @NotBlank(message = "Номер карты пустой!")
    private String cardToNumber;

    @JsonProperty("amount")
    private AmountDtoIn amountDtoIn;
}
