package com.munsun.card2card_project.application.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Детали перевода")
public record AmountDtoIn (
    @Schema(description = "Сумма перевода")
    @Min(message = "Минимальное значение для перевода составляет 10", value = 10)
    Long value,

    @Schema(description = "Валюта перевода")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть длиной 3 символа и " +
            "указана строчными буквами")
    String currency
) {}