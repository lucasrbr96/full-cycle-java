package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CreateGenreUseCaseTest {

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId(){
        //given
        final var expectName = "Ação";
        final var expectIsActive = true;
        final var expectCategories = List.<CategoryID>of();

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));

        Mockito.when(genreGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        //when

        final var actualOutput = useCase.execute(aCommand);

        //then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Mockito.verify(genreGateway, Mockito.times(1)).create(Mockito.argThat(aGenre ->
                        Objects.equals(expectName, aGenre.getName())
                        && Objects.equals(expectIsActive, aGenre.isActive())
                        && Objects.equals(expectCategories, aGenre.getCategories())
                        && Objects.nonNull(aGenre.getId())
                        && Objects.nonNull(aGenre.getCreatedAt())
                        && Objects.nonNull(aGenre.getUpdatedAt())
                        && Objects.isNull(aGenre.getDeletedAt())
                ));
    }

    private List<String> asString(final List<CategoryID> categories){
        return categories.stream()
                .map(CategoryID::getValue).toList();
    }
}
