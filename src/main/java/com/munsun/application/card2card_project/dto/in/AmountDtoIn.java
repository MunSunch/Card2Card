package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AmountDtoIn {
    @Min(message = "Минимальное значение для перевода составляет 10", value = 10)
    @JsonProperty("value")
    private Long value;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть длиной 3 символа и " +
            "указана строчными буквами")
    @JsonProperty("currency")
    private String currency;
}
