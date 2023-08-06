package com.munsun.application.card2card_project.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.repository.CardRepository;
import com.munsun.application.card2card_project.repository.CrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TransferRepositoryImpl implements CrudRepository<TransferInfo>{
    private final ConcurrentHashMap<Long, TransferInfo> transfers;
    private final CardRepository cardRepository;
    private final AtomicLong generatorId;
    private final Gson gson;

    @Autowired
    public TransferRepositoryImpl(CardRepository cardRepository, Gson gson) {
        this.gson = gson;
        this.transfers = new ConcurrentHashMap<>();
        this.cardRepository = cardRepository;
        this.generatorId = new AtomicLong();
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        Path path = Path.of("transfers.json");
        String in = "";
        try {
            in = Files.readString(path);
            if(in.isEmpty())
                return;
        } catch (NoSuchFileException e) {
            Files.createFile(path);
            return;
        }
        TypeToken<List<TransferInfo>> collectionType = new TypeToken<>(){};
        var temp = gson.fromJson(in, collectionType);
        temp.forEach(x -> {
            transfers.put(generatorId.incrementAndGet(), x);
        });
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        Path path = Path.of("transfers.json");
        String out = gson.toJson(transfers.values());
        Files.writeString(Path.of("transfers.json"), out);
    }

    @Override
    public TransferInfo add(TransferInfo newObj) {
        newObj.setOperationId(generatorId.incrementAndGet());
        transfers.put(newObj.getOperationId(), newObj);
        return transfers.get(newObj.getOperationId());
    }

    @Override
    public Optional<TransferInfo> remove(Long id) {
        return Optional.ofNullable(transfers.remove(id));
    }

    @Override
    public Optional<TransferInfo> update(Long id, TransferInfo newObj) {
        return Optional.ofNullable(transfers.replace(id, newObj));
    }

    @Override
    public Optional<TransferInfo> get(Long id) {
        return Optional.of(transfers.get(id));
    }

    public List<TransferInfo> getAll() {
        return new ArrayList<>(transfers.values());
    }
}
