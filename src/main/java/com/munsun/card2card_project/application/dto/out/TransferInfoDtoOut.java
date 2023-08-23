package com.munsun.card2card_project.application.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferInfoDtoOut (
    Long operationId,
    String cardFromNumber,
    String cardToNumber,
    AmountDtoOut amountDtoOut,
    boolean status
) {}
