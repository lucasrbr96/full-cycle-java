package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre(){
        //given
        final var aGenre = genreGateway.create(Genre.newGenre("ação", true));
        final var expectedId = aGenre.getId();

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()) );

        Assertions.assertEquals(0, genreRepository.count());

    }

    @Test
    public void givenAInvalidGenreId_whenCallsDeleteGenre_shouldDeleteGenre(){
        //given
        genreGateway.create(Genre.newGenre("ação", true));

        final var expectedId = GenreID.from("123");

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()) );

        Assertions.assertEquals(1, genreRepository.count());

    }
}
