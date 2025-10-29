package com.geekstore.geekstore.Repository;

import com.geekstore.geekstore.Model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
}
