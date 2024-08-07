package com.example.stock.service;

// 스프링의 @Transactional 어노테이션 동작 방식
// 스프링은 다음과 같은 StockService을 감싸고 있는 클래스를 생성하여 빈으로 등록하고 의존관계 주입에 사용한다.
public class TransactionStockService {
	private StockService stockService;

	public TransactionStockService(StockService stockService) {
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) {
		// 트랜잭션 시작
		startTransaction();

		// 타겟 클래스 호출
		stockService.decrease(id, quantity);

		// 트랜잭션 종료
		endTransaction();
	}

	private void startTransaction() {
		System.out.println("Transaction started");
	}

	private void endTransaction() {
		System.out.println("Commit");
	}
}
