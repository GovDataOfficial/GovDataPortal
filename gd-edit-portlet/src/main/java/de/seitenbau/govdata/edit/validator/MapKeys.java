package de.seitenbau.govdata.edit.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = MapKeysValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapKeys {
  String message();

  String[] allowedKeys();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
