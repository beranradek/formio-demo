/*
 * Created on 10.10.2011
 *
 * Copyright (c) 2011 Et netera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package com.examples.forms.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.examples.forms.domain.Address;
import com.examples.forms.domain.Registration;

public class RegistrationValidator implements ConstraintValidator<RegistrationValid, Registration> {

	public static final String TPL_PREFIX = "constraints.registration";
	
	@Override
	public void initialize(RegistrationValid ann) {
		// no initialization required
	}

	@Override
	public boolean isValid(Registration reg, ConstraintValidatorContext ctx) {
		if (reg == null) return true;
		
		// we will specify own message name, without using default message constraints.registrationValid
		ctx.disableDefaultConstraintViolation();
		boolean valid = true;
		// contact address or e-mail required
		if (!contactAddressFilled(reg.getContactAddress()) && !emailFilled(reg.getEmail())) {
			valid = false;
			ctx.buildConstraintViolationWithTemplate(getTplName("addrOrEmailRequired")).addConstraintViolation();
		}
		return valid;
	}
	
	private String getTplName(String violationType) {
		return "{" + TPL_PREFIX + "." + violationType + "}";
	}
	
	private boolean contactAddressFilled(Address addr) {
		return addr != null && !addr.isEmpty();
	}
	
	private boolean emailFilled(String email) {
		return email != null && !email.isEmpty();
	}
}