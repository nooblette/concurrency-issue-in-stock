package com.example.stock.facade;

import org.springframework.stereotype.Component;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;

import lombok.extern.slf4j.Slf4j;

// 비즈니스 로직 수행 전후로 레디스에 Lock을 추가, 해제하기 위한 클래스
@Slf4j
@Component
public class LettuceLockStockFacade {
	private final RedisLockRepository repository;
	private final StockService stockService;

	public LettuceLockStockFacade(RedisLockRepository repository, StockService stockService) {
		this.repository = repository;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		// Lock 획득
		while(!repository.lock(id)) {
			// Lock 획득에 실패하였다면 100ms 후 재시도, 100ms 텀을 두어 레디스에 부하를 줄인다.
			log.info("lock 획득 실패");
			Thread.sleep(100);
		}

		// Lock 획득에 성공했다면 재고 감소 로직 실행
		log.info("lock 획득");
		try {
			stockService.decrease(id, quantity);
		} finally {
			// 로직이 모두 수행되었다면 Lock 해제
			repository.unlock(id);
			log.info("lock 해제");
		}
	}
}
