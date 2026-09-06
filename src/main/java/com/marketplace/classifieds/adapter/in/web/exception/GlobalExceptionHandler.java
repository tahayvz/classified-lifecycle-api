package com.marketplace.classifieds.adapter.in.web.exception;

import com.marketplace.classifieds.domain.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClassifiedNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ClassifiedNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateClassifiedException.class)
    public ResponseEntity<?> handleDuplicate(DuplicateClassifiedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 409);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadWordException.class)
    public ResponseEntity<?> handleBadWord(BadWordException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Yinelenen ilan kısıtı veritabanı tarafından reddedildi.
     *
     * <p>Uygulama önce "böyle bir ilan var mı" diye soruyor, sonra yazıyor. İki
     * eşzamanlı istek arasındaki boşlukta ikisi de kontrolden geçebilir; benzersizlik
     * kısıtı ikincisini veritabanında durdurur. Bu bir sunucu arızası değil, aynı
     * kuralın ikinci savunma hattıdır — 500 değil <b>409</b> dönmeli, tıpkı
     * kontrolün yakaladığı durumda olduğu gibi.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse("Aynı ilan daha önce eklenmiş.", HttpStatus.CONFLICT);
    }

    /**
     * İki eşzamanlı durum değişikliği çakıştı.
     *
     * <p>{@code @Version} olmadan ikisi de yazılır ve biri diğerini sessizce ezerdi;
     * geçmiş tablosuna iki satır girer, ilan tek duruma geçerdi. Artık ikincisi
     * reddediliyor ve çağıran tekrar deneyebilir.
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleConcurrentUpdate(OptimisticLockingFailureException ex) {
        return buildResponse(
                "İlan bu sırada başka bir istek tarafından güncellendi. Lütfen tekrar deneyin.",
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildResponse("Beklenmeyen bir hata oluştu: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParse(HttpMessageNotReadableException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 400);
        body.put("message", "Geçersiz JSON formatı veya enum değeri hatalı: " + ex.getMostSpecificCause().getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidStatus(InvalidStatusTransitionException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 409);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(SameStatusException.class)
    public ResponseEntity<?> handleSameStatus(SameStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 409);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    private ResponseEntity<Object> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("message", message);

        return new ResponseEntity<>(response, status);
    }
}
