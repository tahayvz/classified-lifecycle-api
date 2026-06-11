package com.marketplace.classifieds.domain.service;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.exception.BadWordException;
import com.marketplace.classifieds.domain.exception.DuplicateClassifiedException;
import com.marketplace.classifieds.domain.port.out.BadWordsPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;

public class ClassifiedValidationService {

    private final BadWordsPort badWordsPort;
    private final ClassifiedPort classifiedPort;

    public ClassifiedValidationService(BadWordsPort badWordsPort, ClassifiedPort classifiedPort) {
        this.badWordsPort = badWordsPort;
        this.classifiedPort = classifiedPort;
    }

    public void validateBadWords(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        for (String bad : badWordsPort.getBadWords()) {
            if (text.contains(bad.toLowerCase())) {
                throw new BadWordException("İçerikte yasaklı kelime bulundu: " + bad);
            }
        }
    }

    public void validateDuplicate(String title, String description, ClassifiedCategory category) {
        boolean exists = classifiedPort.existsByTitleAndDescription(
                title.trim(),
                description.trim(),
                category
        );

        if (exists) {
            throw new DuplicateClassifiedException("Aynı ilan daha önce eklenmiş.");
        }
    }
}