package com.watchout.watchout.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchout.watchout.entities.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
    @Override
    List<Shop> findAll();
    @Override
    Shop getReferenceById(Integer id);
    @Override
    void deleteById(Integer id);
    @Override
    void deleteAllByIdInBatch(Iterable<Integer> ids);
}

