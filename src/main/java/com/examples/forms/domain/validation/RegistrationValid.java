/*
 * Created on 10.10.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package com.examples.forms.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/** 
 * Valid registration with all neccessary data filled.
 * 
 * @author Radek Beran
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=RegistrationValidator.class)
@Documented
public @interface RegistrationValid {

	String message() default "{constraints.registrationValid}";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
