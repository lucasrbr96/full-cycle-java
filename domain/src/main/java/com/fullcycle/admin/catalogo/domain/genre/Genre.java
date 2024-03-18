package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.AggregationRoot;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genre extends AggregationRoot<GenreID> {

    private String name;
    private boolean active;
    private List<Category> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected Genre(
            final GenreID anId,
            final String  aName,
            final boolean isActive,
            final List<Category> categories,
            final Instant aCreatedAt,
            final Instant aUpdateAt,
            final Instant aDeletedAt
    ){
        super(anId);
        this.name = aName;
        this.active = isActive;
        this.categories = categories;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdateAt;
        this.deletedAt = aDeletedAt;

        final var notification = Notification.create();
        validate(notification);

        if (notification.hasError()){
            throw new NotificationException("Failed to create a Aggregation Genre", notification);
        }
    }

    public static Genre newGenre(final String aName, final boolean isActive){
        final var anId = GenreID.unique();
        final var now = Instant.now();
        final var deletedAt = isActive ? null : now;
        return new Genre(anId, aName, isActive, new ArrayList<>(), now, now, deletedAt);
    }


    public static Genre with(
            final GenreID anId,
            final String  aName,
            final boolean isActive,
            final List<Category> categories,
            final Instant aCreatedAt,
            final Instant aUpdateAt,
            final Instant aDeletedAt
    ){
        return new Genre(anId, aName, isActive, categories, aCreatedAt, aUpdateAt, aDeletedAt);

    }

    public static Genre with(final Genre aGenre){
        return new Genre(
                aGenre.id,
                aGenre.name,
                aGenre.active,
                aGenre.categories,
                aGenre.createdAt,
                aGenre.updatedAt,
                aGenre.deletedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public boolean isActive() {
        return active;
    }
}
