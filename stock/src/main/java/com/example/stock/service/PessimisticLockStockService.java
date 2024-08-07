package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class PessimisticLockStockService {
	private final StockRepository stockRepository;

	public PessimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		// 락을 걸고 재고 데이터를 가져온다.
		Stock stock = stockRepository.findByIdWithPessimisticLock(id);

		stock.decrease(quantity);
		stockRepository.save(stock);
	}
}
