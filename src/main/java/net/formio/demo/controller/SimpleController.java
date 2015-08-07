package net.formio.demo.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.demo.domain.Nation;
import net.formio.demo.domain.Person;
import net.formio.demo.forms.FormConstants;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.common.SessionAttributeStorage;

/**
 * Simple person editing form controller.
 * @author Radek Beran
 */
@WebServlet(urlPatterns = {"/simple.html"})
public class SimpleController extends AbstractBaseController {
	private static final long serialVersionUID = -940571494115484909L;
	private static final String PAGE_NAME = "simple";
	private static final SessionAttributeStorage<Person> personStorage = new SessionAttributeStorage<>("person");
	
	// immutable definition of the form, can be freely shared/cached
	// private static final FormMapping<Person> personForm = Forms.automatic(Person.class, "person").build();		
	private static final FormMapping<Person> personForm = Forms.basic(Person.class, "person")
		// whitelist of formProperties to bind
		.fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation", "birthDate")
		.build(FormConstants.DEFAULT_LOCATION);

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(ContentTypes.HTML);
		if (request.getParameter("submitted") != null) {
			FormData<Person> formData = personForm.bind(new ServletRequestParams(request));
			if (formData.isValid()) {
				personStorage.storeData(request.getSession(), formData.getData());
				redirect(request, response, PAGE_NAME, true);
			} else {
				// show validation errors
				renderForm(request, response, formData);
			}
		} else {
			// loading currently stored data to show it in the form
			FormData<Person> formData = new FormData<>(findOrCreatePerson(request));
			renderForm(request, response, formData);
		}
	}

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, FormData<Person> formData) throws ServletException, IOException {
		FormMapping<Person> filledForm = personForm.fill(formData);
		Map<Nation, String> nationItems = new LinkedHashMap<>();
		for (Nation nation : Nation.values()) {
			nationItems.put(nation, nation.name()); 
		}
		request.setAttribute("nationItems", nationItems);
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}
	
	private Person findOrCreatePerson(HttpServletRequest request) {
		Person p = personStorage.findData(request.getSession());
		if (p == null) {
			p = initPerson();
		}
		return p;
	}
	
	private Person initPerson() {
		Person aPerson = new Person("Obi-Wan", "Kenobi");
		Calendar birth = Calendar.getInstance();
		birth.set(1970, 11, 3);
		aPerson.setBirthDate(birth.getTime());
		aPerson.setMale(true);
		aPerson.setNation(Nation.JEDI_KNIGHT);
		aPerson.setPersonId(1L);
		return aPerson;
	}

}
