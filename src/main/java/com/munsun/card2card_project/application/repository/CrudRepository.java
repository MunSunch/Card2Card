package com.munsun.card2card_project.application.repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    T add(T newObj);
    List<T> getAll();
    void clear();
}
