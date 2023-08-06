package com.munsun.application.card2card_project.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceDtoIn {
    @NotBlank
    @JsonProperty("cardNumber")
    private String cardNumber;

    @Positive
    @Min(10)
    @JsonProperty("money")
    private Long value;
}
