package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
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

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

public class CreateGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

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

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreId(){
        //given
        final var expectName = "Ação";
        final var expectIsActive = true;
        final var expectCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));

        Mockito.when(genreGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        Mockito.when(categoryGateway.existsByIds(any()))
                .thenReturn(expectCategories);

        //when

        final var actualOutput = useCase.execute(aCommand);

        //then
        Mockito.verify(categoryGateway, Mockito.times(1))
                .existsByIds(expectCategories);
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

    @Test
    public void givenAValidCommandWithInactiveGenre_whenCallsCreateGenre_shouldReturnGenreId(){
        //given
        final var expectName = "Ação";
        final var expectIsActive = false;
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
                        && Objects.nonNull(aGenre.getDeletedAt())
        ));
    }

    @Test
    public void givenAInvalidEmptyName_whenCallsCreateGenre_shouldReturnDomainException(){
        //given
        final var expectName = " ";
        final var expectIsActive = true;
        final var expectCategories = List.<CategoryID>of();

        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));


        //when

        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        //then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(0)).existsByIds(any());
        Mockito.verify(genreGateway, Mockito.times(0)).create(any());
    }

    @Test
    public void givenAInvalidNullName_whenCallsCreateGenre_shouldReturnDomainException(){
        //given
        final String expectName = null;
        final var expectIsActive = true;
        final var expectCategories = List.<CategoryID>of();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));


        //when

        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        //then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(0)).existsByIds(any());
        Mockito.verify(genreGateway, Mockito.times(0)).create(any());
    }

    @Test
    public void givenAInvalidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException(){
        //given
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectName = "Ação";
        final var expectIsActive = true;
        final var expectCategories = List.<CategoryID>of(filmes, series, documentarios);

        final var expectedErrorMessage = "Some categories could not be found: 456, 789";
        final var expectedErrorCount = 1;

        Mockito.when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(series));

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));


        //when

        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        //then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByIds(any());
        Mockito.verify(genreGateway, Mockito.times(0)).create(any());
    }

    @Test
    public void givenAInvalidName_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException(){
        //given
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectName = " ";
        final var expectIsActive = true;
        final var expectCategories = List.<CategoryID>of(filmes, series, documentarios);

        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        Mockito.when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(series));

        final var aCommand =
                CreateGenreCommand.with(expectName, expectIsActive, asString(expectCategories));


        //when

        final var actualException = Assertions.assertThrows(NotificationException.class, () ->
                useCase.execute(aCommand));

        //then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        Assertions.assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());

        Mockito.verify(categoryGateway, Mockito.times(1)).existsByIds(any());
        Mockito.verify(genreGateway, Mockito.times(0)).create(any());
    }
}
