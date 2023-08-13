package com.munsun.application.card2card_project.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDtoIn {
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть длиной 3 символа и " +
            "указана строчными буквами")
    private String currency;
}
