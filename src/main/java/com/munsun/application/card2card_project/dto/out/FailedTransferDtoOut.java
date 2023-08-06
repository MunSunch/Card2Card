package com.munsun.application.card2card_project.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedTransferDtoOut {
    private String message;
    private Long id;
}
