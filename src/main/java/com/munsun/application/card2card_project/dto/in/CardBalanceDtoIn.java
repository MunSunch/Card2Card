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
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceDtoIn {
    @Pattern(regexp = "^\\d{16}$",
            message = "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9")
    @JsonProperty("cardNumber")
    private String cardNumber;

    @Min(message = "Минимальное значение для перевода составляет 10", value = 10)
    @JsonProperty("money")
    private Long value;
}
