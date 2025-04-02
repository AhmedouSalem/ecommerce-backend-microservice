package com.aryan.analyticsservice.service.admin.faq;


import com.aryan.analyticsservice.dto.FAQDto;

public interface FAQService {
	FAQDto postFAQ(Long productId, FAQDto faqDto);
}
