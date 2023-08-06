package com.munsun.application.card2card_project.repository;

import com.munsun.application.card2card_project.model.TransferInfo;

public interface TransferRepository {
    TransferInfo send(TransferInfo info);
}
