package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Детали перевода")
public class AmountDtoIn {
    @Schema(description = "Сумма перевода")
    @Min(message = "Минимальное значение для перевода составляет 10", value = 10)
    @JsonProperty("value")
    private Long value;

    @Schema(description = "Валюта перевода")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть длиной 3 символа и " +
            "указана строчными буквами")
    @JsonProperty("currency")
    private String currency;
}
