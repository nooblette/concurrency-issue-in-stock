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

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@SpringBootTest
class StockServiceTest {
	@Autowired
	// private StockService stockService;
	private PessimisticLockStockService stockService;

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
	public void 재고감소() {
		// when
		stockService.decrease(1L, 1L);

		// then : 1L 상품의 재고 : 100 - 1 = 99개가 남아있어야함
		Stock stock = stockRepository.findById(1L).orElseThrow();
		assertEquals(99, stock.getQuantity());
	}

	@Test
	public void 동시에_100개의_요청() throws InterruptedException {
		// when
		// 100개의 쓰레드 사용(멀티스레드)
		int threadCount = 100;

		// ExecutorService : 비동기로 실행하는 작업을 간단하게 실행할 수 있도록 자바에서 제공하는 API
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// CountDownLatch : 작업을 진행중인 다른 스레드가 작업을 완료할때까지 대기할 수 있도록 도와주는 클래스
		CountDownLatch latch = new CountDownLatch(threadCount);
		// 100개의 작업 요청
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		// then
		Stock stock = stockRepository.findById(1L).orElseThrow();
		// 처음에 100개로 저장하고 한개씩 100번 감속하기때문에 0이 될 것을 예상
		assertEquals(0, stock.getQuantity());

	}
}