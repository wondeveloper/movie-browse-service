package com.vivek.imdb.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class SortFieldValidator implements ConstraintValidator<ValidSort, String> {

    private Set<String> allowed;
    private final Set<String> direction = Set.of("asc", "desc");
    @Override
    public void initialize(ValidSort constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        allowed = Set.of(constraintAnnotation.allowed());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty()) return true;
        String[] parts = value.split(",");
        if (parts.length % 2 != 0) return false; //it should be a pair
        for (int i = 0; i < parts.length; i += 2) {
           String field = parts[i].trim();
           String dir = parts[i+1].trim();
           if (field.isEmpty() || dir.isEmpty()) return false;
           if (!allowed.contains(field)) return false;
           if (!direction.contains(dir)) return false;
        }
        return true;
    }
}
