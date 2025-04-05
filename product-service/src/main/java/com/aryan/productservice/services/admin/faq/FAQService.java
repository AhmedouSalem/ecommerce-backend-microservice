package com.aryan.productservice.services.admin.faq;


import com.aryan.productservice.dto.FAQDto;

public interface FAQService {
    FAQDto postFAQ(Long productId, FAQDto faqDto);
}
