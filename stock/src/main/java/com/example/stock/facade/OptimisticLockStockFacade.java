package com.example.stock.facade;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.example.stock.service.OptimisticLockStockService;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// Optimistic Lock을 사용할때 업데이트 실패시 재시도(버전을 다시 조회)를 위한 객체
public class OptimisticLockStockFacade {
	private final OptimisticLockStockService optimisticLockStockService;

	public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
		this.optimisticLockStockService = optimisticLockStockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while(true) {
			try {
				optimisticLockStockService.decrease(id, quantity);

				// 재고 차감에 성공하였다면 while loop 종료
				break;
			} catch (ObjectOptimisticLockingFailureException e) {
				// 재고 업데이트 실패시 50ms간 대기 후 재시도
				log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
				Thread.sleep(50);
			}
		}
	}
}
