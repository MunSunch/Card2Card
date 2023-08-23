package com.munsun.card2card_project.application.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Подтверждение операции")
public record ConfirmTransferDtoIn (
    @Schema(description = "Уникальный ID перевода")
    @Pattern(regexp = "^\\d+$", message = "Id операции должно быть положительным числом")
    String operationId,

    @Schema(description = "Код подтверждения")
    @Pattern(regexp = "^\\d{4}$", message = "Код подтверждения не четырехзначное число")
    String code
){}
