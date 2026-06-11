package com.marketplace.classifieds.adapter.in.web.exception;

import com.marketplace.classifieds.domain.exception.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @GetMapping("/notfound")
    public void notFound() {
        throw new ClassifiedNotFoundException("İlan bulunamadı");
    }

    @GetMapping("/duplicate")
    public void duplicate() {
        throw new DuplicateClassifiedException("Mükerrer ilan");
    }

    @GetMapping("/badword")
    public void badWord() {
        throw new BadWordException("Yasaklı kelime bulundu");
    }

    @GetMapping("/invalid")
    public void invalidStatus() {
        throw new InvalidStatusTransitionException("Geçersiz geçiş");
    }

    @GetMapping("/parse")
    public void parse() {
        throw new HttpMessageNotReadableException("Enum hatası");
    }

    @GetMapping("/generic")
    public void generic() {
        throw new RuntimeException("Patladı");
    }
}
