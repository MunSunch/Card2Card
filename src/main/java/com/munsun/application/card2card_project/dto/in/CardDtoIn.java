package com.munsun.application.card2card_project.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDtoIn {
    @NotBlank(message = "Валюта не заполнена")
    private String currency;
}
