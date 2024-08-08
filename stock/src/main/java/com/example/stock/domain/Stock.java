package com.example.stock.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity // DB의 테이블로 매핑
public class Stock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;
	private Long quantity;

	@Version
	private Long version; // Optimistic Lock을 위함

	public Stock() {
	}

	public Stock(Long productId, Long quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void decrease(Long quantity){
		if(this.quantity - quantity < 0){
			throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
		}

		// 현재 수량을 갱신
		this.quantity -= quantity;
	}
}
