package com.vivek.imdb.config;

import com.vivek.imdb.util.SortFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortFieldValidator.class)
public @interface ValidSort {

    String[] allowed() default  {"createdAt", "title", "id"};
    String message() default "Invalid sort field";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};
}
