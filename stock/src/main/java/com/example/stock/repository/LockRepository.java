package com.example.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.stock.domain.Stock;

// Named Lock을 위한 리포지토리
// 예제에서는 편의상 Stock 엔티티를 사용했으나, 실무에서는 별도의 DataSource(e.g. JDBC)를 사용한다.
public interface LockRepository extends JpaRepository<Stock, Long> {
	@Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
	void getLock(String key);

	@Query(value = "select release_lock(:key)", nativeQuery = true)
	void releaseLock(String key);
}