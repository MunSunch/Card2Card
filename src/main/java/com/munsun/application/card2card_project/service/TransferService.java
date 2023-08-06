package com.munsun.application.card2card_project.service;

import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.SuccessTransferDtoOut;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;

import java.util.List;

public interface TransferService {
    SuccessTransferDtoOut send(TransferInfoDtoIn info) throws Exception;
    SuccessTransferDtoOut confirm(ConfirmTransferDtoIn info) throws Exception;
    List<TransferInfoDtoOut> getAll();
}
