package com.marketplace.classifieds;

import com.marketplace.classifieds.adapter.in.web.dto.request.CreateClassifiedRequest;
import com.marketplace.classifieds.adapter.in.web.dto.request.UpdateClassifiedStatusRequest;
import com.marketplace.classifieds.adapter.in.web.dto.response.ClassifiedResponse;
import com.marketplace.classifieds.adapter.in.web.dto.response.StatusHistoryResponse;
import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedJpaRepository;
import com.marketplace.classifieds.adapter.out.persistence.jpa.ClassifiedStatusHistoryJpaRepository;
import com.marketplace.classifieds.domain.enums.ClassifiedCategory;
import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Classified lifecycle over HTTP against PostgreSQL")
class ClassifiedLifecycleIT extends AbstractPostgresIT {

    private static final String CLASSIFIEDS = "/api/v1/classifieds";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ClassifiedJpaRepository classifiedRepository;

    @Autowired
    private ClassifiedStatusHistoryJpaRepository historyRepository;

    @BeforeEach
    void clearDatabase() {
        historyRepository.deleteAll();
        classifiedRepository.deleteAll();
    }

    private CreateClassifiedRequest request(ClassifiedCategory category) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return CreateClassifiedRequest.builder()
                .title("Satilik daire " + unique)
                .description("Genis ve ferah, merkezi konumda bir ilan aciklamasi " + unique)
                .category(category)
                .build();
    }

    private ClassifiedResponse create(ClassifiedCategory category) {
        ResponseEntity<ClassifiedResponse> response =
                rest.postForEntity(CLASSIFIEDS, request(category), ClassifiedResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private ResponseEntity<String> changeStatus(Long id, ClassifiedStatus status, String reason) {
        return rest.exchange(
                CLASSIFIEDS + "/" + id + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(UpdateClassifiedStatusRequest.builder()
                        .status(status)
                        .reason(reason)
                        .build()),
                String.class);
    }

    @Test
    void moderatedCategory_shouldTravelFromPendingToActiveToInactive() {
        ClassifiedResponse created = create(ClassifiedCategory.EMLAK);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(ClassifiedStatus.ONAY_BEKLIYOR);

        assertThat(changeStatus(created.getId(), ClassifiedStatus.AKTIF, "approved")
                .getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(changeStatus(created.getId(), ClassifiedStatus.DEAKTIF, "expired")
                .getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ClassifiedResponse> reloaded =
                rest.getForEntity(CLASSIFIEDS + "/" + created.getId(), ClassifiedResponse.class);

        assertThat(reloaded.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reloaded.getBody().getStatus()).isEqualTo(ClassifiedStatus.DEAKTIF);
    }

    @Test
    void unmoderatedCategory_shouldBePublishedImmediately() {
        ClassifiedResponse created = create(ClassifiedCategory.ALISVERIS);

        assertThat(created.getStatus()).isEqualTo(ClassifiedStatus.AKTIF);
    }

    @Test
    void statusHistory_shouldRecordEveryTransitionNewestFirst() {
        ClassifiedResponse created = create(ClassifiedCategory.EMLAK);
        changeStatus(created.getId(), ClassifiedStatus.AKTIF, "approved");
        changeStatus(created.getId(), ClassifiedStatus.DEAKTIF, "expired");

        ResponseEntity<List<StatusHistoryResponse>> history = rest.exchange(
                CLASSIFIEDS + "/" + created.getId() + "/history",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(history.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(history.getBody()).hasSize(2);
        assertThat(history.getBody().get(0).getNewStatus()).isEqualTo(ClassifiedStatus.DEAKTIF);
        assertThat(history.getBody().get(1).getNewStatus()).isEqualTo(ClassifiedStatus.AKTIF);
    }

    @Test
    void invalidTransition_shouldBeRejectedAndLeaveStateUntouched() {
        ClassifiedResponse created = create(ClassifiedCategory.EMLAK);
        changeStatus(created.getId(), ClassifiedStatus.DEAKTIF, "withdrawn");

        ResponseEntity<String> rejected =
                changeStatus(created.getId(), ClassifiedStatus.AKTIF, "reactivate");

        assertThat(rejected.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<ClassifiedResponse> reloaded =
                rest.getForEntity(CLASSIFIEDS + "/" + created.getId(), ClassifiedResponse.class);
        assertThat(reloaded.getBody().getStatus()).isEqualTo(ClassifiedStatus.DEAKTIF);
    }

    @Test
    void rejectedTransition_shouldNotLeaveAHistoryRecord() {
        ClassifiedResponse created = create(ClassifiedCategory.EMLAK);

        changeStatus(created.getId(), ClassifiedStatus.ONAY_BEKLIYOR, "same status");

        assertThat(historyRepository.findByClassifiedIdOrderByChangedAtDesc(created.getId()))
                .isEmpty();
    }

    @Test
    void duplicateClassified_shouldBeRejectedWithConflict() {
        CreateClassifiedRequest first = request(ClassifiedCategory.EMLAK);
        assertThat(rest.postForEntity(CLASSIFIEDS, first, ClassifiedResponse.class)
                .getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> duplicate =
                rest.postForEntity(CLASSIFIEDS, first, String.class);

        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(classifiedRepository.count()).isEqualTo(1);
    }

    @Test
    void invalidPayload_shouldBeRejectedBeforeReachingTheDatabase() {
        CreateClassifiedRequest tooShort = CreateClassifiedRequest.builder()
                .title("kisa")
                .description("cok kisa")
                .category(ClassifiedCategory.EMLAK)
                .build();

        ResponseEntity<String> response =
                rest.postForEntity(CLASSIFIEDS, tooShort, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(classifiedRepository.count()).isZero();
    }

    @Test
    void unknownClassified_shouldReturnNotFound() {
        ResponseEntity<String> response =
                rest.getForEntity(CLASSIFIEDS + "/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void statistics_shouldReflectPersistedClassifieds() {
        create(ClassifiedCategory.EMLAK);
        create(ClassifiedCategory.EMLAK);
        create(ClassifiedCategory.ALISVERIS);

        ResponseEntity<Map<String, Object>> stats = rest.exchange(
                "/api/v1/dashboard/statistics",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(stats.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stats.getBody()).isNotNull();
        assertThat(stats.getBody().get("totalClassifieds")).isEqualTo(3);

        @SuppressWarnings("unchecked")
        Map<String, Integer> byStatus = (Map<String, Integer>) stats.getBody().get("statistics");

        assertThat(byStatus)
                .containsEntry(ClassifiedStatus.ONAY_BEKLIYOR.name(), 2)
                .containsEntry(ClassifiedStatus.AKTIF.name(), 1)
                .hasSize(2);
    }

    @Test
    void actuatorHealth_shouldReportPostgresConnection() {
        ResponseEntity<String> health = rest.getForEntity("/actuator/health", String.class);

        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(health.getBody()).contains("UP");
    }
}
