package com.munsun.card2card_project.application.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Перевод")
public record TransferInfoDtoIn (
    @Schema(description = "Номер карты источника")
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    String cardFromNumber,

    @Schema(description = "Срок действия карты источника")
    @Pattern(regexp = "^((0[1-9])|[10-12])/[2-3][1-9]$",
             message = "Срок действия карты должен быть указан форматом: месяц/год")
    String cardFromValidTill,

    @Schema(description = "CVV карты источника")
    @Pattern(regexp = "^\\d{3}$",
             message = "CVV карты - это трехзначное число")
    String cardFromCVV,

    @Schema(description = "Номер карты цели")
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    String cardToNumber,

    @Schema(description = "Детали перевода")
    @Valid
    @JsonProperty("amount")
    AmountDtoIn amountDtoIn
) {}
