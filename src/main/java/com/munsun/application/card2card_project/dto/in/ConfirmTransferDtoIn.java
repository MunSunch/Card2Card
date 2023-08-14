package com.munsun.application.card2card_project.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Подтверждение операции")
public class ConfirmTransferDtoIn {
    @Schema(description = "Уникальный ID перевода")
    @Pattern(regexp = "^\\d+$", message = "Id операции должно быть положительным числом")
    private String operationId;

    @Schema(description = "Код подтверждения")
    @Pattern(regexp = "^\\d{4}$", message = "Код подтверждения не четырехзначное число")
    private String code;
}
