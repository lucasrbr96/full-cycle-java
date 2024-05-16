package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@JsonTest
public class GenreResponseTest {

    @Autowired
    private JacksonTester<GenreResponse> json;

    @Test
    public void testMarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123");
        final var expectedIsActive = false;
        final var expectCreatedAt = Instant.now();
        final var expectUpdatedAt = Instant.now();
        final var expectDeletedAt = Instant.now();

        final var response = new GenreResponse(
                expectedId,
                expectedName,
                expectedCategories,
                expectedIsActive,
                expectCreatedAt,
                expectUpdatedAt,
                expectDeletedAt
        );

        final var actualJson = this.json.write(response);
        Assertions.assertThat(actualJson)
                .hasJsonPath("$.id", expectedId)
                .hasJsonPath("$.name", expectedName)
                .hasJsonPath("$.categories_id", expectedCategories)
                .hasJsonPath("$.is_active", expectedIsActive)
                .hasJsonPath("$.created_at", expectCreatedAt.toString())
                .hasJsonPath("$.deleted_at", expectDeletedAt.toString())
                .hasJsonPath("$.updated_at", expectUpdatedAt.toString());
    }

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedCategories = "456";
        final var expectedIsActive = false;
        final var expectCreatedAt = Instant.now();
        final var expectUpdatedAt = Instant.now();
        final var expectDeletedAt = Instant.now();

        final var json = """
                {
                    "id": "%s",
                    "name": "%s",
                    "categories_id": ["%s"],
                    "is_active": "%s",
                    "created_at": "%s",
                    "updated_at": "%s",
                    "deleted_at": "%s"             
                }
                """.formatted(
                        expectedId,
                        expectedName,
                        expectedCategories,
                        expectedIsActive,
                        expectCreatedAt.toString(),
                        expectUpdatedAt.toString(),
                        expectDeletedAt.toString()
        );
        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories))
                .hasFieldOrPropertyWithValue("active", expectedIsActive)
                .hasFieldOrPropertyWithValue("createdAt", expectCreatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectDeletedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectUpdatedAt);
    }
}
