package com.aryan.productservice.repository;

import com.aryan.productservice.model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long>{
	List<FAQ> findAllByProductId(Long productId);
}
