package com.marketplace.classifieds.domain.service;

import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.exception.BadWordException;
import com.marketplace.classifieds.domain.exception.DuplicateClassifiedException;
import com.marketplace.classifieds.domain.port.out.BadWordsPort;
import com.marketplace.classifieds.domain.port.out.ClassifiedPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassifiedValidationServiceTest {

    BadWordsPort badWordsPort;
    ClassifiedPort classifiedPort;

    ClassifiedValidationService service;

    @BeforeEach
    void setUp() {
        badWordsPort = mock(BadWordsPort.class);
        classifiedPort = mock(ClassifiedPort.class);

        service = new ClassifiedValidationService(badWordsPort, classifiedPort);
    }

    @Test
    void validateBadWords_shouldThrow_whenBadWordExists() {
        when(badWordsPort.getBadWords()).thenReturn(Set.of("opsiyonlu", "tqewwcs"));

        String title = "Bu ilan opsiyonlu";
        String desc = "normal açıklama";

        assertThatThrownBy(() ->
                service.validateBadWords(title, desc))
                .isInstanceOf(BadWordException.class)
                .hasMessageContaining("opsiyonlu");
    }

    @Test
    void validateBadWords_shouldPass_whenNoBadWords() {
        when(badWordsPort.getBadWords()).thenReturn(Set.of("opsiyonlu", "tqewwcs"));

        service.validateBadWords("Temiz başlık", "Açıklama");
    }

    @Test
    void validateBadWords_shouldPass_whenBadWordsListIsEmpty() {
        when(badWordsPort.getBadWords()).thenReturn(Set.of());

        service.validateBadWords("başlık", "açıklama");

        // Exception atmıyorsa test başarılıdır.
    }

    @Test
    void validateDuplicate_shouldThrow_whenDuplicateExists() {
        when(classifiedPort.existsByTitleAndDescription(
                anyString(), anyString(), any()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.validateDuplicate(
                        "Başlık", "Açıklama", ClassifiedCategory.EMLAK))
                .isInstanceOf(DuplicateClassifiedException.class);
    }

    @Test
    void validateDuplicate_shouldPass_whenNotDuplicate() {
        when(classifiedPort.existsByTitleAndDescription(
                anyString(), anyString(), any()))
                .thenReturn(false);

        service.validateDuplicate("Başlık", "Açıklama", ClassifiedCategory.EMLAK);
    }

    @Test
    void validateDuplicate_shouldTrimInputsBeforeCheck() {
        when(classifiedPort.existsByTitleAndDescription(
                "Başlık",
                "Açıklama",
                ClassifiedCategory.EMLAK
        )).thenReturn(false);

        service.validateDuplicate("  Başlık  ", "  Açıklama  ", ClassifiedCategory.EMLAK);

        verify(classifiedPort, times(1))
                .existsByTitleAndDescription("Başlık", "Açıklama", ClassifiedCategory.EMLAK);
    }


}
