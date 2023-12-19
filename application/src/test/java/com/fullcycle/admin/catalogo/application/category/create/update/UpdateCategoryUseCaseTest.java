package com.fullcycle.admin.catalogo.application.category.create.update;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {
    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId(){
        final var expectedName = "filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("film", null, true);

        final var expectId = aCategory.getId();
        final var aCommand = UpdateCateCommand.with(
                expectId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive
        );

        when(categoryGateway.findById(expectId))
                .thenReturn(Optional.of(aCategory));


        when(categoryGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand).get();

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id);
        verify(categoryGateway, times(1)).findById(expectId);
        verify(categoryGateway, times(1)).update(argThat(
                aUpdateCategory ->
                        Objects.equals(expectedName, aUpdateCategory.getName())
                                && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                                && Objects.equals(expectId, aUpdateCategory.getId())
                                && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                                && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                                && aCategory.getCreatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                                && Objects.isNull(aUpdateCategory.getDeletedAt())
        ));
    }
}
