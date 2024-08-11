package com.example.stock.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class StockService {
	// StockService : 재고(Stock)에 대한 CRUD 기능을 통한 비즈니스 로직 제공
	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	// 재고 감소 로직 구현
	// Named Lock을 사용하는 경우 부모와 별도의 트랜잭션에서 수행되어야하므로 Propagation 수준을 REQUIRES_NEW로 지정한다.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized void decrease(Long id, Long quantity) {
		// Stock 조회
		Optional<Stock> stock = stockRepository.findById(id);

		// 재고 감소
		stock.orElseThrow(() -> new RuntimeException("해당하는 재고가 없습니다."))
			.decrease(quantity);

		// 갱신된 값을 저장
		stockRepository.save(stock.get());
	}
}
