package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.stock.domain.Stock;

// StockRepository : 재고(Stock)에 대한 데이터베이스와의 CRUD 기능 제공
public interface StockRepository extends JpaRepository<Stock, Long> {
}
