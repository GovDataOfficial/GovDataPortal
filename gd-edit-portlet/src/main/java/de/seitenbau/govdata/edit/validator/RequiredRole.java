package de.seitenbau.govdata.edit.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = RequiredRoleValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole
{
  /**
   * error message
   */
  String message();
  
  /**
   * role name
   */
  String name();
  
  /**
   * groups
   */
  Class<?>[] groups() default { };
  
  /**
   * payload
   */
  Class<? extends Payload>[] payload() default {};
}
