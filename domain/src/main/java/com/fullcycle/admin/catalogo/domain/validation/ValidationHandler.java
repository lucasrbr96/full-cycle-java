package com.fullcycle.admin.catalogo.domain.validation;

import java.util.List;

public interface ValidationHandler {

    List<Error> getErrors();

    ValidationHandler append(Error anError);

    ValidationHandler append(ValidationHandler anHandler);

    ValidationHandler validate(Validation aValidation);

    default boolean hasError(){
        return getErrors() != null && !(getErrors().isEmpty());
    }

    public interface Validation{
        void validate();
    }
}
