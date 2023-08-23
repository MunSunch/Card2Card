package com.munsun.card2card_project.application.dto.in;

import jakarta.validation.constraints.Pattern;

public record CardDtoIn (
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть длиной 3 символа и указана строчными буквами")
    String currency
) {}
