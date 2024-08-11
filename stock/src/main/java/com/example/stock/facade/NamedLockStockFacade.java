package com.example.stock.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;

// 실제 로직 전후로 Lock을 획득, 해제를 수행하는 클래스
@Component
public class NamedLockStockFacade {
	private final LockRepository lockRepository;
	private final StockService stockService;

	public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
		this.lockRepository = lockRepository;
		this.stockService = stockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity){
		try {
			// id 값을 기준으로 Named Lock 획득
			lockRepository.getLock(id.toString());

			// 재고 감소 로직
			stockService.decrease(id, quantity);
		} finally {
			// 모든 비즈니스 로직을 수행하고 나면 lock 해제
			lockRepository.releaseLock(id.toString());
		}
	}
}
