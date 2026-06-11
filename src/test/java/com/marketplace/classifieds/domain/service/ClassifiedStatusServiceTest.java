package com.marketplace.classifieds.domain.service;

import com.marketplace.classifieds.domain.enums.ClassifiedStatus;
import com.marketplace.classifieds.domain.exception.ImmutableClassifiedException;
import com.marketplace.classifieds.domain.exception.InvalidStatusTransitionException;
import com.marketplace.classifieds.domain.exception.SameStatusException;
import com.marketplace.classifieds.domain.model.Classified;
import com.marketplace.classifieds.domain.model.ClassifiedStatusHistory;
import com.marketplace.classifieds.domain.port.out.ClassifiedStatusHistoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClassifiedStatusServiceTest {

    ClassifiedStatusHistoryPort historyPort;
    ClassifiedStatusService statusService;

    @BeforeEach
    void setUp() {
        historyPort = mock(ClassifiedStatusHistoryPort.class);
        statusService = new ClassifiedStatusService(historyPort);
    }

    private Classified createClassified(ClassifiedStatus status) {
        return Classified.builder()
                .id(10L)
                .status(status)
                .build();
    }

    @Test
    void changeStatus_shouldThrow_whenClassifiedIsImmutable() {
        Classified c = createClassified(ClassifiedStatus.MUKERRER);

        assertThatThrownBy(() ->
                statusService.changeStatus(c, ClassifiedStatus.AKTIF, "admin", "test"))
                .isInstanceOf(ImmutableClassifiedException.class);

        verify(historyPort, never()).save(any());
    }

    @Test
    void changeStatus_shouldThrow_whenSameStatusGiven() {
        Classified c = createClassified(ClassifiedStatus.AKTIF);

        assertThatThrownBy(() ->
                statusService.changeStatus(c, ClassifiedStatus.AKTIF, "admin", "test"))
                .isInstanceOf(SameStatusException.class);

        verify(historyPort, never()).save(any());
    }

    @Test
    void changeStatus_shouldThrow_whenInvalidTransition() {
        Classified c = createClassified(ClassifiedStatus.DEAKTIF);

        assertThatThrownBy(() ->
                statusService.changeStatus(c, ClassifiedStatus.ONAY_BEKLIYOR, "admin", "test"))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(historyPort, never()).save(any());
    }

    @Test
    void changeStatus_shouldSaveCorrectHistoryFields_whenValidTransition() {
        Classified c = createClassified(ClassifiedStatus.ONAY_BEKLIYOR);

        statusService.changeStatus(c, ClassifiedStatus.AKTIF, "adminUser", "Onaylandı");

        assertThat(c.getStatus()).isEqualTo(ClassifiedStatus.AKTIF);

        // Capture the saved history object
        ArgumentCaptor<ClassifiedStatusHistory> captor =
                ArgumentCaptor.forClass(ClassifiedStatusHistory.class);

        verify(historyPort, times(1)).save(captor.capture());

        ClassifiedStatusHistory saved = captor.getValue();

        assertThat(saved.getClassified()).isEqualTo(c);
        assertThat(saved.getPreviousStatus()).isEqualTo(ClassifiedStatus.ONAY_BEKLIYOR);
        assertThat(saved.getNewStatus()).isEqualTo(ClassifiedStatus.AKTIF);
        assertThat(saved.getChangedBy()).isEqualTo("adminUser");
        assertThat(saved.getReason()).isEqualTo("Onaylandı");
    }

}
