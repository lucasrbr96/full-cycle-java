package com.fullcycle.admin.catalogo.application.category.retrieve;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp(){
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValid_whenCallsGetCategory_shouldReturnCategory(){
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category
                .newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        Mockito.when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(aCategory.clone()));

        final var actualCategory = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAInvalid_whenCallsGetCategory_shouldReturnNotFound(){
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "";

        Mockito.when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(
                DomainException.class,
                ()-> useCase.execute(expectedId.getValue())
        );


        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void givenAValid_whenGatewayThrowsException_shouldReturnException(){
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "Gateway Error";

        Mockito.when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class,
                ()-> useCase.execute(expectedId.getValue())
        );


        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}
