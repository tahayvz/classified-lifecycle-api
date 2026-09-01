package com.marketplace.classifieds.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ClassifiedStatusTest {

    private static final Map<ClassifiedStatus, Set<ClassifiedStatus>> ALLOWED =
            new EnumMap<>(ClassifiedStatus.class);

    static {
        ALLOWED.put(ClassifiedStatus.ONAY_BEKLIYOR,
                EnumSet.of(ClassifiedStatus.AKTIF, ClassifiedStatus.DEAKTIF));
        ALLOWED.put(ClassifiedStatus.AKTIF,
                EnumSet.of(ClassifiedStatus.DEAKTIF));
        ALLOWED.put(ClassifiedStatus.DEAKTIF,
                EnumSet.noneOf(ClassifiedStatus.class));
        ALLOWED.put(ClassifiedStatus.MUKERRER,
                EnumSet.noneOf(ClassifiedStatus.class));
    }

    static Stream<Arguments> allTransitionPairs() {
        return Arrays.stream(ClassifiedStatus.values())
                .flatMap(from -> Arrays.stream(ClassifiedStatus.values())
                        .map(to -> Arguments.of(from, to)));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("allTransitionPairs")
    @DisplayName("every from/to pair matches the documented transition matrix")
    void canTransitionTo_shouldMatchMatrix_forEveryPair(ClassifiedStatus from, ClassifiedStatus to) {
        boolean expected = ALLOWED.get(from).contains(to);

        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }

    @Test
    void transitionMatrix_shouldCoverEveryStatus() {
        assertThat(ALLOWED.keySet())
                .containsExactlyInAnyOrder(ClassifiedStatus.values());
    }

    @ParameterizedTest
    @EnumSource(ClassifiedStatus.class)
    void canTransitionTo_shouldRejectSelfTransition(ClassifiedStatus status) {
        assertThat(status.canTransitionTo(status)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ClassifiedStatus.class, names = {"DEAKTIF", "MUKERRER"})
    void terminalStatuses_shouldAllowNoOutgoingTransition(ClassifiedStatus terminal) {
        assertThat(Arrays.stream(ClassifiedStatus.values()).anyMatch(terminal::canTransitionTo))
                .isFalse();
    }

    @Test
    void deaktif_shouldBeReachableFromEveryNonTerminalStatus() {
        assertThat(ClassifiedStatus.ONAY_BEKLIYOR.canTransitionTo(ClassifiedStatus.DEAKTIF)).isTrue();
        assertThat(ClassifiedStatus.AKTIF.canTransitionTo(ClassifiedStatus.DEAKTIF)).isTrue();
    }

    @Test
    void mukerrer_shouldNotBeReachableFromAnyStatus() {
        assertThat(Arrays.stream(ClassifiedStatus.values())
                .anyMatch(s -> s.canTransitionTo(ClassifiedStatus.MUKERRER)))
                .isFalse();
    }
}
