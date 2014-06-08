package net.formio.demo.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.demo.domain.User;
import net.formio.demo.servlet.SessionStorage;
import net.formio.servlet.ServletRequestContext;
import net.formio.servlet.ServletRequestParams;
import net.formio.validation.ValidationResult;

/**
 * AJAX form controller.
 * @author Radek Beran
 */
public class AjaxFormController extends AbstractBaseController {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_NAME = "ajax";
	private static final SessionStorage<ArrayList<User>> userListStorage = new SessionStorage<ArrayList<User>>(AjaxFormController.class.getSimpleName() + "_userList");
	private static final SessionStorage<User> newUserStorage = new SessionStorage<User>(AjaxFormController.class.getSimpleName() + "_newUser");
	
	private static final FormMapping<User> newUserForm = Forms.basic(User.class, "user")
		.fields("login")
		.build();	

	@Override
	protected void processRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("submitted") != null) {
			FormData<User> formData = newUserForm.bind(new ServletRequestParams(request), DEFAULT_LOCALE);
			if (formData.isValid()) {
				newUserStorage.storeData(request.getSession(), formData.getData());
				redirect(request, response, PAGE_NAME, true);
			} else {
				// show validation errors
				renderForm(request, response, formData);
			}
		} else {
			// loading currently stored data to show it in the form
			FormData<User> formData = new FormData<User>(findOrCreateNewUser(request), ValidationResult.empty);
			renderForm(request, response, formData);
		}
	}

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, FormData<User> formData) throws ServletException, IOException {
		FormMapping<User> filledForm = newUserForm.fill(formData, DEFAULT_LOCALE, new ServletRequestContext(request));
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}

	private User findOrCreateNewUser(HttpServletRequest request) {
		User u = newUserStorage.findData(request.getSession());
		if (u == null) {
			u = new User();
		}
		return u;
	}
}
