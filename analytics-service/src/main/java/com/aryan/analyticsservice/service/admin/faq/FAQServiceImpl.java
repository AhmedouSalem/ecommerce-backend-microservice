package com.aryan.analyticsservice.service.admin.faq;

import com.aryan.analyticsservice.dto.FAQDto;
import com.aryan.analyticsservice.dto.ProductDto;
import com.aryan.analyticsservice.feign.ProductFeign;
import com.aryan.analyticsservice.model.FAQ;
import com.aryan.analyticsservice.repository.FAQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FAQServiceImpl implements FAQService {
	@Autowired
	private final FAQRepository faqRepository;

	private final ProductFeign productFeignClient;

	public FAQDto postFAQ(Long productId, FAQDto faqDto) {
		Optional<ProductDto> optionalProduct = Optional.ofNullable(productFeignClient.getProductById(productId));
		if(optionalProduct.isPresent()) {
			FAQ faq = new FAQ();
			faq.setQuestion(faqDto.getQuestion());
			faq.setAnswer(faqDto.getAnswer());
			faq.setProduct(optionalProduct.get());

			FAQ savedFAQ = faqRepository.save(faq);
			log.info("FAQ posted successfully for product with ID: {}", productId);
			return savedFAQ.getFAQDto();
		}

		log.warn("Failed to post FAQ. Product with ID {} not found.", productId);
		return null;
	}
	public List<FAQDto> getFaqsByProductId(Long productId) {
		List<FAQ> faqs = faqRepository.findAllByProductId(productId);
		return faqs.stream()
				.map(faq -> FAQDto.builder()
						.id(faq.getId())
						.question(faq.getQuestion())
						.answer(faq.getAnswer())
						.productId(faq.getProductId())
						.build())
				.toList();
	}


}
