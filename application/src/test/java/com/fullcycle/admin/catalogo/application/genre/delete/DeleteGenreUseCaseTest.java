package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

public class DeleteGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre(){
        //given
        final var aGenre = Genre.newGenre("ação", true);
        final var expectedId = aGenre.getId();

        //when
        Mockito.doNothing().when(genreGateway).deleteById(Mockito.any());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()) );

        //then
        Mockito.verify(genreGateway, Mockito.times(1)).deleteById(expectedId);

    }

    @Test
    public void givenAInvalidGenreId_whenCallsDeleteGenre_shouldDeleteGenre(){
        //given
        final var expectedId = GenreID.from("123");

        //when
        Mockito.doNothing().when(genreGateway).deleteById(Mockito.any());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()) );

        //then
        Mockito.verify(genreGateway, Mockito.times(1)).deleteById(expectedId);

    }

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenreAndGatewayThrowsUnexpectedError_shouldReceiveException(){
        //given
        final var aGenre = Genre.newGenre("ação", true);
        final var expectedId = aGenre.getId();

        //when
        Mockito.doThrow(new IllegalStateException("Gateway error")).when(genreGateway)
                .deleteById(Mockito.any());

        Assertions.assertThrows(IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue()) );

        //then
        Mockito.verify(genreGateway, Mockito.times(1)).deleteById(expectedId);

    }
}
