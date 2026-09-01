package com.marketplace.classifieds.adapter.out.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BadWordsAdapterTest {

    private BadWordsAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BadWordsAdapter();
    }

    @Test
    void getBadWords_shouldBeEmpty_beforeLoading() {
        assertThat(adapter.getBadWords()).isEmpty();
    }

    @Test
    void loadBadWords_shouldPopulateFromClasspathResource() {
        adapter.loadBadWords();

        assertThat(adapter.getBadWords()).isNotEmpty();
    }

    @Test
    void loadBadWords_shouldNotContainBlankEntries() {
        adapter.loadBadWords();

        assertThat(adapter.getBadWords()).noneMatch(String::isBlank);
    }

    @Test
    void loadBadWords_shouldTrimSurroundingWhitespace() {
        adapter.loadBadWords();

        assertThat(adapter.getBadWords())
                .allSatisfy(word -> assertThat(word).isEqualTo(word.trim()));
    }

    @Test
    void loadBadWords_shouldBeIdempotent() {
        adapter.loadBadWords();
        int afterFirstLoad = adapter.getBadWords().size();

        adapter.loadBadWords();

        assertThat(adapter.getBadWords()).hasSize(afterFirstLoad);
    }
}
