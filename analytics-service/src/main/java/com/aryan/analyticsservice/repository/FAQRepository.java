package com.aryan.analyticsservice.repository;

import java.util.List;

import com.aryan.analyticsservice.model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQRepository extends JpaRepository<FAQ, Long>{
    List<FAQ> findAllByProductId(Long productId);
}