package com.aryan.analyticsservice.controller;

import com.aryan.analyticsservice.dto.FAQDto;
import com.aryan.analyticsservice.model.FAQ;
import com.aryan.analyticsservice.repository.FAQRepository;
import com.aryan.analyticsservice.service.admin.faq.FAQServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FAQController {

    private final FAQServiceImpl faqService;

    @PostMapping("/{productId}")
    public ResponseEntity<FAQDto> createFaq(@PathVariable Long productId, @RequestBody FAQDto faqDto) {
        FAQDto createdFaq = faqService.postFAQ(productId, faqDto);
        if (createdFaq != null) {
            return ResponseEntity.ok(createdFaq);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<FAQDto>> getFaqsByProductId(@PathVariable Long productId) {
        List<FAQDto> faqs = faqService.getFaqsByProductId(productId);
        return ResponseEntity.ok(faqs);
    }
}

