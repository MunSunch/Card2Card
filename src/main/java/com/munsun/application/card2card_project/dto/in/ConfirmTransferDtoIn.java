package com.munsun.application.card2card_project.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmTransferDtoIn {
    @NotBlank
    private String operationId;
    @NotBlank
    private String code;
}
