package com.aryan.productservice.services.admin.faq;

import com.aryan.productservice.dto.FAQDto;
import com.aryan.productservice.model.FAQ;
import com.aryan.productservice.model.Product;
import com.aryan.productservice.repository.FAQRepository;
import com.aryan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FAQServiceImplement implements FAQService {
    private final FAQRepository faqRepository;

    private final ProductRepository productRepository;

    public FAQDto postFAQ(Long productId, FAQDto faqDto) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
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

}
