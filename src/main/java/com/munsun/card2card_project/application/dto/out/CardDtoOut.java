package com.munsun.card2card_project.application.dto.out;

public record CardDtoOut (
     String cardNumber,
     String cardValidTill,
     String cardCVV,
     String currency,
     Long value,
     Boolean isActive
) {}