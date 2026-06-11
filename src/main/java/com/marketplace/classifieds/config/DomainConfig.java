package com.marketplace.classifieds.config;

import com.marketplace.classifieds.domain.service.ClassifiedStatusService;
import com.marketplace.classifieds.domain.port.out.BadWordsPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import com.marketplace.classifieds.domain.service.ClassifiedValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ClassifiedValidationService classifiedValidationService(BadWordsPort badWordsPort, ClassifiedPort classifiedPort) {
        return new ClassifiedValidationService(badWordsPort, classifiedPort);
    }

    @Bean
    public ClassifiedStatusService classifiedStatusService(ClassifiedStatusHistoryPort historyPort) {
        return new ClassifiedStatusService(historyPort);
    }
}
