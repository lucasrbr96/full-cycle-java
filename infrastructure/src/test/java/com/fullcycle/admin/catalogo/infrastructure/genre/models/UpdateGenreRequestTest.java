package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

@JsonTest
public class UpdateGenreRequestTest {

    @Autowired
    private JacksonTester<UpdateGenreRequest> json;


    @Test
    public void testUnmarshall() throws IOException {
        final var expectedName = "Ação";
        final var expectedCategories = "123";
        final var expectedIsActive = true;

        final var json = """
                {
                    "name": "%s",
                    "categories_id": ["%s"],
                    "is_active": "%s"           
                }
                """.formatted(
                expectedName,
                expectedCategories,
                expectedIsActive
        );
        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories))
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}
