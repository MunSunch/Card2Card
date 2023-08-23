package com.munsun.card2card_project.application.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record CardBalanceDtoIn (
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    String cardNumber,

    @Min(message = "Минимальное значение для перевода составляет 10", value = 10)
    @JsonProperty("money")
    Long value
) {}
