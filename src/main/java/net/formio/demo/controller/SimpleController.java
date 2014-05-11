package net.formio.demo.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.demo.domain.Nation;
import net.formio.demo.domain.Person;
import net.formio.security.TokenException;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.ServletRequestContext;
import net.formio.validation.ValidationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple person editing form controller.
 */
public class SimpleController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SimpleController.class);
	
	private static final String ATT_PERSON = "person";
	private static final String SUCCESS = "success";
	
	private static final Locale LOCALE = new Locale("en");
	
	// immutable definition of the form, can be freely shared/cached
	// private static final FormMapping<Person> personForm = Forms.automatic(Person.class, "person").build();
			
	private static final FormMapping<Person> personForm = Forms.basicSecured(Person.class, "person")
		// whitelist of properties to bind
		.fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation", "birthDate")
		.build();	

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		try {
			if (request.getParameter("submitted") != null) {
				FormData<Person> formData = personForm.bind(new ServletRequestParams(request), LOCALE);
				if (formData.isValid()) {
					savePerson(request, formData.getData());
					redirect(request, response, true);
				} else {
					// show validation errors
					renderForm(request, response, formData);
				}
			} else {
				// loading currently stored data to show it in the form
				FormData<Person> formData = new FormData<Person>(findPerson(request), ValidationResult.empty);
				renderForm(request, response, formData);
			}
		} catch (TokenException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, FormData<Person> formData) throws ServletException, IOException {
		FormMapping<Person> filledForm = personForm.fill(formData, LOCALE, new ServletRequestContext(request));
		request.setAttribute("form", filledForm);
		Map<Nation, String> nationItems = new HashMap<Nation, String>();
		for (Nation nation : Nation.values()) {
			nationItems.put(nation, nation.name()); 
		}
		request.setAttribute("nationItems", nationItems);
		if (request.getParameter(SUCCESS) != null) request.setAttribute(SUCCESS, "1");
		request.getRequestDispatcher("/WEB-INF/jsp/simple.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private Person findPerson(HttpServletRequest request) {
		Person p = (Person)request.getSession().getAttribute(ATT_PERSON);
		if (p == null) {
			p = initPerson();
		}
		return p;
	}
	
	private void savePerson(HttpServletRequest request, Person p) {
		request.getSession().setAttribute(ATT_PERSON, p);
	}
	
	private void redirect(HttpServletRequest request,
		HttpServletResponse response, boolean dataSaved) throws IOException {
		response.sendRedirect(request.getContextPath() + "/simple.html" + (dataSaved ? ("?" + SUCCESS + "=1") : ""));
	}
	
	private Person initPerson() {
		Person aPerson = new Person("Jan", "Novak");
		aPerson.setBirthDate(new Date());
		aPerson.setMale(true);
		aPerson.setNation(Nation.CZECH);
		aPerson.setPersonId(1L);
		return aPerson;
	}

}
