package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountDtoIn {
    @Positive(message = "Значение пополняемой валюты отрицательное!")
    @Min(value = 10, message = "Минимальная сумма < 10!")
    @JsonProperty("value")
    private Long value;

    @NotBlank(message = "Валюта не заполнена!")
    @JsonProperty("currency")
    private String currency;
}
