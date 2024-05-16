package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryListResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

@JsonTest
public class GenreListResponseTest {

    @Autowired
    private JacksonTester<GenreListResponse> json;

    @Test
    public void testMarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectCreatedAt = Instant.now();
        final var expectDeletedAt = Instant.now();

        final var response = new GenreListResponse(
                expectedId,
                expectedName,
                expectedIsActive,
                expectCreatedAt,
                expectDeletedAt
        );

        final var actualJson = this.json.write(response);
        Assertions.assertThat(actualJson)
                .hasJsonPath("$.id", expectedId)
                .hasJsonPath("$.name", expectedName)
                .hasJsonPath("$.is_active", expectedIsActive)
                .hasJsonPath("$.created_at", expectCreatedAt.toString())
                .hasJsonPath("$.deleted_at", expectDeletedAt.toString());
    }
}
