package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.stock.domain.Stock;

import jakarta.persistence.LockModeType;

// StockRepository : 재고(Stock)에 대한 데이터베이스와의 CRUD 기능 제공
public interface StockRepository extends JpaRepository<Stock, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE) // Spring Data Jpa에서는 @Lock 어노테이션을 활용하여 손쉽게 Pessimistic Lock을 구현할 수 있다.
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithPessimisticLock(Long id);
}
