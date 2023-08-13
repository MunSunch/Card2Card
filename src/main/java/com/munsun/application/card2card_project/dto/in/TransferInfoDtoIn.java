package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferInfoDtoIn {
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    private String cardFromNumber;

    @Pattern(regexp = "^(0[1-9]|[10-12])/2[3-9]$",
             message = "Срок действия карты должен быть указан форматом: месяц/год")
    private String cardFromValidTill;

    @Pattern(regexp = "^\\d{3}$",
             message = "CVV карты - это трехзначное число")
    private String cardFromCVV;

    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    private String cardToNumber;

    @Valid
    @JsonProperty("amount")
    private AmountDtoIn amountDtoIn;
}
