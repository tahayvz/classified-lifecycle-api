package com.marketplace.classifieds.adapter.out.validation;

import com.marketplace.classifieds.domain.port.out.BadWordsPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class BadWordsAdapter implements BadWordsPort {

    private final Set<String> badWords = new HashSet<>();

    @PostConstruct
    void loadBadWords() {
        try {
            var inputStream = getClass().getClassLoader().getResourceAsStream("Badwords.txt");

            if (inputStream == null) {
                log.warn("Badwords.txt bulunamadı. Bad word kontrolü PASİF!");
                return;
            }

            try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .forEach(badWords::add);
            }

            log.info("Yasaklı kelimeler yüklendi. Sayı: {}", badWords.size());
        } catch (Exception ex) {
            log.error("Badwords.txt yüklenirken hata oluştu: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public Set<String> getBadWords() {
        return badWords;
    }
}
