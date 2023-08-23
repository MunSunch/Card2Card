package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.model.TransferInfo;

import java.util.Optional;

public interface TransferRepository extends CrudRepository<TransferInfo> {
    Optional<TransferInfo> findByIdOperation(long idOperation);
}
