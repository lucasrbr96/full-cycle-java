package com.fullcycle.admin.catalogo.e2e.category;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER
            = new MySQLContainer("mysql:latest")
                .withPassword("123456")
                .withUsername("root")
                .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry){
        final var mappedPort = MY_SQL_CONTAINER.getMappedPort(3306);
        System.out.printf("container is running on port %s\n", mappedPort);
        registry.add("mysql.port", () -> mappedPort);
        System.setProperty("spring.datasource.url", MY_SQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", MY_SQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MY_SQL_CONTAINER.getPassword());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());

        Assertions.assertEquals(0, categoryRepository.count());
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId = 
                givenACategory(expectedName, expectedDescription, expectedIsActive);
        
        final var actualCategory = retrieveACategory(actualId.getValue());

        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.active());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());
    }

    private CategoryResponse retrieveACategory(final String anId) throws Exception {
        final var aRequest = get("/categories/" + anId)
                .contentType(MediaType.APPLICATION_JSON);
        final var json = this.mvc.perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }


    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToPageAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

         listCategories(0, 1)
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                 .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                 .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                 .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                 .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Documentários")));

        listCategories(1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Filmes")));

        listCategories(2, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(2)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Séries")));

        listCategories(3, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, true);
        givenACategory("Séries", null, true);

        listCategories(0,1, "fil")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Filmes")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", true);
        givenACategory("Séries", "A", true);

        listCategories(0,3, "", "description", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Documentários")))
                .andExpect(jsonPath("$.items[1].name", Matchers.equalTo("Filmes")))
                .andExpect(jsonPath("$.items[2].name", Matchers.equalTo("Séries")));

    }

    @Test
    public void asCatalogAdminIShouldBeAbleToGetACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());

        Assertions.assertEquals(0, categoryRepository.count());
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId =
                givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToATreatedErrorByGettingANotFoundCategory() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());

        Assertions.assertEquals(0, categoryRepository.count());

        final var aRequest = get("/categories/123")
                .contentType(MediaType.APPLICATION_JSON);
        final var json = this.mvc.perform(aRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.equalTo("Category with ID 123 was not found")));
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToUpdateACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualId =
                givenACategory("Movies", null, true);

        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aRequestBody =
                new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var aRequest = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToInactiveACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var actualId =
                givenACategory(expectedName, expectedDescription, true);

        final var aRequestBody =
                new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var aRequest = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToActiveACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());

        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var actualId =
                givenACategory(expectedName, expectedDescription, false);

        final var aRequestBody =
                new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);
        final var aRequest = MockMvcRequestBuilders.put("/categories/" + actualId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk());

        final var actualCategory = categoryRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToDeleteACategoryByItsIdentifier() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualId =
                givenACategory("Filmes", null, true);

        this.mvc.perform(delete("/categories/" + actualId.getValue()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(this.categoryRepository.existsById(actualId.getValue()));
    }


    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }
    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

        private ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String directions) throws Exception {
        final var aRequest = get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", directions)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc.perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return this.mvc.perform(aRequest);
    }

    private CategoryID givenACategory(final String aName, final String aDescription, final boolean isActive) throws Exception {
        final var requestBody = new CreateCategoryRequest(aName, aDescription, isActive);
        final var aRequest = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final var actualId = this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/categories/", "");

        return CategoryID.from(actualId);
    }
}
