package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class OptimisticLockStockService {
	private final StockRepository stockRepository;

	public OptimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		// Optimistic Lock으로 재고(Stock) 조회
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);

		// 재고 수량 감소
		stock.decrease(quantity);
		stockRepository.save(stock);
	}
}
