package com.munsun.application.card2card_project.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmTransferDtoIn {
    @Pattern(regexp = "^\\d+$", message = "Id операции должно быть положительным числом")
    private String operationId;

    @Pattern(regexp = "^\\d{4}$", message = "Код подтверждения не четырехзначное число")
    private String code;
}
