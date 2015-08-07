package net.formio.demo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.demo.domain.User;
import net.formio.demo.domain.UserData;
import net.formio.format.Location;
import net.formio.servlet.ServletRequestContext;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.common.SessionAttributeStorage;

/**
 * AJAX form controller.
 * @author Radek Beran
 */
@WebServlet(urlPatterns = {"/ajax.html"})
public class AjaxFormController extends AbstractBaseController {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_NAME = "ajax";
	private static final SessionAttributeStorage<UserData> usersStorage = 
		new SessionAttributeStorage<>(AjaxFormController.class.getSimpleName() + "_userData");
	
	enum Actions {
		addUser, removeUser_, saveChanges
	}
	
	private static final FormMapping<UserData> usersForm = 
		Forms.automatic(UserData.class, "userData").build(Location.ENGLISH);

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getAction(request);
		if (action != null && !action.isEmpty()) {
			if (action.equals(Actions.addUser.name())) {
				processAddUser(request, response);
			} else if (action.startsWith(Actions.removeUser_.name())) {
				int userIndex = Integer.valueOf(action.substring(Actions.removeUser_.name().length())).intValue();
				processRemoveUser(request, response, userIndex);
			} else if (action.equals(Actions.saveChanges.name())) {
				processSaveAllChanges(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND); // unknown action
			}
		} else {
			// No processing action, just rendering the form
			FormData<UserData> formData = new FormData<>(findOrCreateUserData(request));
			renderWholeForm(request, response, formData);
		}
	}

	/** Process AJAX request: Adding new user. */
	protected void processAddUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletRequestParams reqParams = new ServletRequestParams(request);
		FormData<UserData> formData = usersForm.bind(reqParams);
		if (formData.isValid()) {
			// Append new user and empty the new user data (for adding next user):
			addAndStoreUser(request, formData.getData()); // modifies formData - adds new user to list
			if (reqParams.isTdiAjaxRequest()) {
				FormMapping<UserData> filledForm = usersForm.fill(formData);
				FormMapping<User> newUserMapping = filledForm.getMapping(User.class, "newUser");
				renderUpdateUsersNewUser(request, response, getUserMappings(filledForm), newUserMapping);
			} else {
				// AJAX (JavaScript) is not available, rendering whole page
				renderWholeForm(request, response, formData);
			}
		} else {
			if (reqParams.isTdiAjaxRequest()) {
				// show validation errors, also in this case AJAX response must be rendered
				FormMapping<UserData> filledForm = usersForm.fill(formData);
				FormMapping<User> newUserMapping = filledForm.getMapping(User.class, "newUser");
				renderUpdateNewUser(request, response, newUserMapping);
			} else {
				// AJAX (JavaScript) is not available, rendering whole page
				renderWholeForm(request, response, formData);
			}
		}
	}
	
	/** Process AJAX request: Removing an user. */
	protected void processRemoveUser(HttpServletRequest request, HttpServletResponse response, int userIndex) throws ServletException, IOException {
		ServletRequestParams reqParams = new ServletRequestParams(request);
		FormData<UserData> formData = usersForm.bind(reqParams);
		UserData userData = formData.getData();
		if (userData != null && userData.getUsers() != null) {
			if (userIndex >= 0 && userIndex < userData.getUsers().size()) {
				userData.getUsers().remove(userIndex);
				usersStorage.storeData(request.getSession(), userData);
				if (reqParams.isTdiAjaxRequest()) {
					FormMapping<UserData> filledForm = usersForm.fill(formData);
					renderUpdateUsers(request, response, getUserMappings(filledForm));
				} else {
					// AJAX (JavaScript) is not available, rendering whole page
					renderWholeForm(request, response, formData);
				}
			}
		}
	}

	/** Process AJAX request: Saving whole form. */
	protected void processSaveAllChanges(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletRequestParams reqParams = new ServletRequestParams(request);
		FormData<UserData> formData = usersForm.bind(reqParams);
		if (formData.isValid()) {
			usersStorage.storeData(request.getSession(), formData.getData());
			if (reqParams.isTdiAjaxRequest()) {
				renderFormSuccess(request, response);
			} else {
				// AJAX (JavaScript) is not available, using standard redirect
				redirect(request, response, PAGE_NAME, true);
			}
		} else {
			// show validation errors
			if (reqParams.isTdiAjaxRequest()) {
				FormMapping<UserData> filledForm = usersForm.fill(formData);
				FormMapping<User> newUserMapping = filledForm.getMapping(User.class, "newUser");
				List<?> userMappings = getUserMappings(filledForm);
				renderUpdateUsersNewUser(request, response, userMappings, newUserMapping);
			} else {
				renderWholeForm(request, response, formData);
			}
		}
	}

	/** Render whole form - non-ajax response (whole page). */
	private void renderWholeForm(HttpServletRequest request, HttpServletResponse response, FormData<UserData> formData) throws ServletException, IOException {
		response.setContentType(ContentTypes.HTML);
		FormMapping<UserData> filledForm = usersForm.fill(formData, new ServletRequestContext(request));
		request.setAttribute("userData", formData.getData());
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}

	/** Render result of AJAX request for saving whole form (part of page). */
	private void renderFormSuccess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(ContentTypes.XML);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/redirectToSuccess.jsp").forward(request, response);
	}

	/** Render result of AJAX request (part of page). */
	private void renderUpdateUsersNewUser(
		HttpServletRequest request, 
		HttpServletResponse response, 
		List<?> userMappings, 
		FormMapping<User> newUserMapping) throws ServletException, IOException {
		response.setContentType(ContentTypes.XML);
		request.setAttribute("userMappings", userMappings);
		request.setAttribute("newUserMapping", newUserMapping);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/updateUsersNewUser.jsp").forward(request, response);
	}
	
	/** Render result of AJAX request (part of page). */
	private void renderUpdateNewUser(
		HttpServletRequest request, 
		HttpServletResponse response, 
		FormMapping<User> newUserMapping) throws ServletException, IOException {
		response.setContentType(ContentTypes.XML);
		request.setAttribute("newUserMapping", newUserMapping);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/updateNewUser.jsp").forward(request, response);
	}
	
	/** Render result of AJAX request for removing the user. */
	private void renderUpdateUsers(
		HttpServletRequest request,
		HttpServletResponse response,  
		List<?> userMappings) throws ServletException, IOException {
		response.setContentType(ContentTypes.XML);
		request.setAttribute("userMappings", userMappings);
		request.getRequestDispatcher("/WEB-INF/jsp/ajax/updateUsers.jsp").forward(request, response);
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
		return form.getMapping(User.class, "users").getList();
	}
}
