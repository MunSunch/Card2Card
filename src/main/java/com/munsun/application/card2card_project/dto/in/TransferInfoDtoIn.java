package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Перевод")
public class TransferInfoDtoIn {
    @Schema(description = "Номер карты источника")
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    private String cardFromNumber;

    @Schema(description = "Срок действия карты источника")
    @Pattern(regexp = "^(0[1-9]|[10-12])/[2-3][3-9]$",
             message = "Срок действия карты должен быть указан форматом: месяц/год")
    private String cardFromValidTill;

    @Schema(description = "CVV карты источника")
    @Pattern(regexp = "^\\d{3}$",
             message = "CVV карты - это трехзначное число")
    private String cardFromCVV;

    @Schema(description = "Номер карты цели")
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    private String cardToNumber;

    @Schema(description = "Детали перевода")
    @Valid
    @JsonProperty("amount")
    private AmountDtoIn amountDtoIn;
}
