package net.formio.demo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.demo.domain.User;
import net.formio.demo.domain.UserData;
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
	private static final SessionStorage<UserData> usersStorage = 
		new SessionStorage<UserData>(AjaxFormController.class.getSimpleName() + "_userData");
	private static final String ACTION_ADD_USER = "addUser";
	private static final String ACTION_REMOVE_USER_PREFIX = "removeUser_";
	private static final String ACTION_SAVE_CHANGES = "saveChanges";
	
	private static final FormMapping<UserData> usersForm = 
		Forms.automatic(UserData.class, "userData").build();

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getAction(request);
		if (action != null && !action.isEmpty()) {
			if (action.equals(ACTION_ADD_USER)) {
				processAddUser(request, response);
			} else if (action.startsWith(ACTION_REMOVE_USER_PREFIX)) {
				int userIndex = Integer.valueOf(action.substring(ACTION_REMOVE_USER_PREFIX.length())).intValue();
				processRemoveUser(request, response, userIndex);
			} else if (action.equals(ACTION_SAVE_CHANGES)) {
				processSaveAllChanges(request, response);
			} else {
				// unknown action
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} else {
			FormData<UserData> formData = new FormData<UserData>(findOrCreateUserData(request), ValidationResult.empty);
			renderWholeForm(request, response, formData);
		}
	}

	/** Process AJAX request: Adding new user. */
	protected void processAddUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FormData<UserData> formData = usersForm.bind(new ServletRequestParams(request), DEFAULT_LOCALE);
		if (formData.isValid()) {
			// Append new user and empty the new user data (for adding next user):
			addAndStoreUser(request, formData.getData()); // modifies formData - adds new user to list
			if (isAjaxRequest(request)) {
				FormMapping<UserData> filledForm = usersForm.fill(formData, DEFAULT_LOCALE);
				FormMapping<User> newUserMapping = (FormMapping<User>)filledForm.getNested().get("newUser");
				renderUserAdded(request, response, getUserMappings(filledForm), newUserMapping);
			} else {
				// AJAX (JavaScript) is not available, rendering whole page
				renderWholeForm(request, response, formData);
			}
		} else {
			if (isAjaxRequest(request)) {
				// show validation errors, also in this case AJAX response must be rendered
				FormMapping<UserData> filledForm = usersForm.fill(formData, DEFAULT_LOCALE);
				FormMapping<User> newUserMapping = (FormMapping<User>)filledForm.getNested().get("newUser");
				renderUserAddedInvalid(request, response, newUserMapping);
			} else {
				// AJAX (JavaScript) is not available, rendering whole page
				renderWholeForm(request, response, formData);
			}
		}
	}
	
	/** Process AJAX request: Removing an user. */
	protected void processRemoveUser(HttpServletRequest request, HttpServletResponse response, int userIndex) throws ServletException, IOException {
		FormData<UserData> formData = usersForm.bind(new ServletRequestParams(request), DEFAULT_LOCALE);
		UserData userData = formData.getData();
		if (userData != null && userData.getUsers() != null) {
			if (userIndex >= 0 && userIndex < userData.getUsers().size()) {
				userData.getUsers().remove(userIndex);
				usersStorage.storeData(request.getSession(), userData);
				if (isAjaxRequest(request)) {
					FormMapping<UserData> filledForm = usersForm.fill(formData, DEFAULT_LOCALE, new ServletRequestContext(request));
					renderUserRemoved(request, response, getUserMappings(filledForm));
				} else {
					// AJAX (JavaScript) is not available, rendering whole page
					renderWholeForm(request, response, formData);
				}
			}
		}
	}

	/** Process AJAX request: Saving whole form. */
	protected void processSaveAllChanges(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FormData<UserData> formData = usersForm.bind(new ServletRequestParams(request), DEFAULT_LOCALE);
		if (formData.isValid()) {
			usersStorage.storeData(request.getSession(), formData.getData());
			if (isAjaxRequest(request)) {
				renderFormSuccess(request, response);
			} else {
				// AJAX (JavaScript) is not available, using standard redirect
				redirect(request, response, PAGE_NAME, true);
			}
		} else {
			// show validation errors
			if (isAjaxRequest(request)) {
				FormMapping<UserData> filledForm = usersForm.fill(formData, DEFAULT_LOCALE);
				FormMapping<User> newUserMapping = (FormMapping<User>)filledForm.getNested().get("newUser");
				List<?> userMappings = getUserMappings(filledForm);
				renderFormInvalid(request, response, userMappings, newUserMapping);
			} else {
				renderWholeForm(request, response, formData);
			}
		}
	}

	/** Render whole form - non-ajax response (whole page). */
	private void renderWholeForm(HttpServletRequest request, HttpServletResponse response, FormData<UserData> formData) throws ServletException, IOException {
		response.setContentType(HTML_CONTENT_TYPE);
		FormMapping<UserData> filledForm = usersForm.fill(formData, DEFAULT_LOCALE, new ServletRequestContext(request));
		request.setAttribute("userData", formData.getData());
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}
	
	/** Render result of AJAX request for saving INVALID whole form (part of page). */
	private void renderFormInvalid(
		HttpServletRequest request,
		HttpServletResponse response, 
		List<?> userMappings,
		FormMapping<User> newUserMapping) throws ServletException, IOException {
		response.setContentType(XML_CONTENT_TYPE);
		request.setAttribute("userMappings", userMappings);
		request.setAttribute("newUserMapping", newUserMapping);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/userFormInvalid.jsp").forward(request, response);
	}

	/** Render result of AJAX request for saving whole form (part of page). */
	private void renderFormSuccess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(XML_CONTENT_TYPE);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/userFormSuccess.jsp").forward(request, response);
	}

	/** Render result of AJAX request for adding new user (part of page). */
	private void renderUserAdded(
		HttpServletRequest request, 
		HttpServletResponse response, 
		List<?> userMappings, 
		FormMapping<User> newUserMapping) throws ServletException, IOException {
		response.setContentType(XML_CONTENT_TYPE);
		request.setAttribute("userMappings", userMappings);
		request.setAttribute("newUserMapping", newUserMapping);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/userAdded.jsp").forward(request, response);
	}
	
	/** Render result of AJAX request for adding INVALID new user (part of page). */
	private void renderUserAddedInvalid(
		HttpServletRequest request, 
		HttpServletResponse response, 
		FormMapping<User> newUserMapping) throws ServletException, IOException {
		response.setContentType(XML_CONTENT_TYPE);
		request.setAttribute("newUserMapping", newUserMapping);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/userAddedInvalid.jsp").forward(request, response);
	}
	
	/** Render result of AJAX request for removing the user. */
	private void renderUserRemoved(
		HttpServletRequest request,
		HttpServletResponse response,  
		List<?> userMappings) throws ServletException, IOException {
		response.setContentType(XML_CONTENT_TYPE);
		request.setAttribute("userMappings", userMappings);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/userRemoved.jsp").forward(request, response);
	}
	
	private UserData findOrCreateUserData(HttpServletRequest request) {
		UserData u = usersStorage.findData(request.getSession());
		if (u == null) {
			u = initNewUserData();
		}
		return u;
	}
	
	private void addAndStoreUser(HttpServletRequest request, UserData userData) {
		User newUser = userData.getNewUser();
		if (newUser != null) {
			userData.getUsers().add(newUser);
		}
		userData.setNewUser(new User());
		usersStorage.storeData(request.getSession(), userData);
	}

	private UserData initNewUserData() {
		return new UserData();
	}
	
	private List<?> getUserMappings(FormMapping<UserData> form) {
		return form.getNested().get("users").getList();
	}
}
