package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.*;;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CategoryAPI.class)
class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception{

        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aInput =
                new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Right(CreateCategoryOutput.from("123")));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo("123")))
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                        Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                ));
    }

    @Test
    void givenInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception{

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var aInput =
                new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedMessage))));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)))
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenInvalidCommand_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception{

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var aInput =
                new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedMessage)));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)))
                .andExpect(jsonPath("$.message", equalTo(expectedMessage)))
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenAValid_whenCallsGetCategory_shouldReturnCategory() throws Exception{
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category
                .newCategory(expectedName, expectedDescription, expectedIsActive);


        final var expectedId = aCategory.getId().getValue();

        when(getCategoryByIdUseCase.execute(any()))
                .thenReturn(CategoryOutput.from(aCategory));

        MockHttpServletRequestBuilder request = get("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", equalTo(expectedId)))
                        .andExpect(jsonPath("$.name", equalTo(expectedName)))
                        .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                        .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                        .andExpect(jsonPath("$.created_at", equalTo(aCategory.getCreatedAt().toString())))
                        .andExpect(jsonPath("$.updated_at", equalTo(aCategory.getUpdatedAt().toString())))
                        .andExpect(jsonPath("$.deleted_at", equalTo(aCategory.getDeletedAt())));

        verify(getCategoryByIdUseCase, times(1)).execute(eq(expectedId));
    }

    @Test
    void givenAInvalid_whenCallsGetCategory_shouldReturnNotFound() throws Exception{
        final var expectedId = CategoryID.from("123").getValue();
        final var expectedErrorMessage = "Category with ID 123 was not found";


        MockHttpServletRequestBuilder request = get("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);;

        when(getCategoryByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, CategoryID.from("123")));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() throws Exception{
        final var expectedId = "123";
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;


        when(updateCategoryUseCase.execute(any()))
                .thenReturn(API.Right(UpdateCategoryOutput.from(expectedId)));

        final var aCommand = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);


        MockHttpServletRequestBuilder request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd->
            Objects.equals(expectedName, cmd.name())
                    && Objects.equals(expectedDescription, cmd.description())
                    && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenACommandWithInvalidId_whenCallsUpdateCategory_thenShouldReturnNotFoundException() throws Exception{
        final var expectedId = "not-found";
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with ID not-found was not found";

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, CategoryID.from(expectedId)));


        final var aCommand = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);


        MockHttpServletRequestBuilder request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() throws Exception{
        final var expectedId = "123";
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedMessage))));


        final var aCommand = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);


        MockHttpServletRequestBuilder request = put("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void givenAValid_whenCallsDeleteCategory_shouldBeOK() throws Exception{
        final var expectedId = "123";

        doNothing()
                .when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(eq(expectedId));
    }

    @Test
    void givenAInvalid_whenCallsDeleteCategory_shouldBeReturnContent() throws Exception{
        final var expectedId = "123";

        doNothing()
                .when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(eq(expectedId));
    }

    @Test
    void givenValidParams_whenCallsListCategories_shouldReturnCategoriesFiltered() throws Exception{
        final var aCategory = Category.newCategory("Movies", null, true);
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(CategoryListOutput.from(aCategory));

        when(listCategoriesUseCase.execute(any())).thenReturn(
                new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems)
        );

        final var request = get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aCategory.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aCategory.getName())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aCategory.getDescription())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(aCategory.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aCategory.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aCategory.getDeletedAt())));

        verify(listCategoriesUseCase, times(1)).execute(argThat(query->
                Objects.equals(expectedPage, query.page())
                && Objects.equals(expectedPerPage, query.perPage())
                && Objects.equals(expectedDirection, query.direction())
                && Objects.equals(expectedSort, query.sort())
                && Objects.equals(expectedTerms, query.terms())
                ));
    }
}
