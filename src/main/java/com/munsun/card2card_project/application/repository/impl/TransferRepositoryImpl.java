package com.munsun.card2card_project.application.repository.impl;

import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.CardRepository;
import com.munsun.card2card_project.application.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TransferRepositoryImpl implements TransferRepository {
    private final ConcurrentHashMap<Long, TransferInfo> transfers;
    private final AtomicLong generatorId;

    @Autowired
    public TransferRepositoryImpl(CardRepository cardRepository) {
        this.transfers = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
    }

    @Override
    public TransferInfo add(TransferInfo newObj) {
        TransferInfo temp = new TransferInfo(newObj);
        temp.setOperationId(generatorId.incrementAndGet());
        transfers.put(temp.getOperationId(), temp);
        return transfers.get(generatorId.get());
    }

    @Override
    public Optional<TransferInfo> findByIdOperation(long idOperation) {
        return Optional.ofNullable(transfers.get(idOperation));
    }

    public List<TransferInfo> getAll() {
        return new ArrayList<>(transfers.values());
    }

    @Override
    public void clear() {
        transfers.clear();
        generatorId.set(0L);
    }
}
