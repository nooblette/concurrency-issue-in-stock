package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class OptimisticLockStockServiceTest {

	@Autowired
	private OptimisticLockStockService stockService;

	@Autowired
	private StockRepository stockRepository;

	// 각 테스트가 실행되기 전에 데이터베이스에 테스트 데이터 생성
	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	// 각 테스트를 실행한 후에 데이터베이스에 테스트 데이터 삭제
	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	public void 업데이트_실패_테스트() throws InterruptedException {
		final Long stockId = 1L; // 테스트할 재고 ID
		final Long decreaseAmount = 1L;
		final int threadCount = 2;

		// ExecutorService : 비동기로 실행하는 작업을 간단하게 실행할 수 있도록 자바에서 제공하는 API
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// CountDownLatch : 작업을 진행중인 다른 스레드가 작업을 완료할때까지 대기할 수 있도록 도와주는 클래스
		CountDownLatch latch = new CountDownLatch(2);
		// 2개 이상의 쓰레드에 작업 요청
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(stockId, decreaseAmount);
				} catch (ObjectOptimisticLockingFailureException e){
					log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		// then
		Stock stock = stockRepository.findById(1L).orElseThrow();
		// 처음에 100개로 저장하고 한개씩 2번 감소하기 때문에 98이 될 것을 예상
		// 별도의 재처리 로직이 없으므로 테스트는 실패한다.
		assertEquals(98, stock.getQuantity());
	}
}