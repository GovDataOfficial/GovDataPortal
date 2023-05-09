package de.seitenbau.govdata.edit.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = CategoriesValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Categories
{
  /**
   * the validation message.
   * @return
   */
  String message();

  /**
   * the groups.
   * @return
   */
  Class<?>[] groups() default { };

  /**
   * the payload.
   * @return
   */
  Class<? extends Payload>[] payload() default {};
}
