package com.examples.forms.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twinstone.formio.FormData;
import org.twinstone.formio.FormMapping;
import org.twinstone.formio.Forms;
import org.twinstone.formio.servlet.HttpServletRequestParams;

import com.examples.forms.domain.Nation;
import com.examples.forms.domain.Person;

/**
 * Home page controller.
 */
public class IndexController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);
	
	private static final String ATT_PERSON = "person";
	private static final String SUCCESS = "success";
	
	// immutable definition of the form, can be freely shared/cached
	private static final FormMapping<Person> personForm = Forms.automatic(Person.class, "person").build();
			
	//private static final FormMapping<Person> personForm = Forms.basic(Person.class, "person")
	//	// whitelist of properties to bind
	//	.fields("personId", "firstName", "lastName", "salary", "phone", "male", "nation", "birthDate")
	//	.build();	

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (request.getParameter("submitted") != null) {
			FormData<Person> formData = personForm.bind(new HttpServletRequestParams(request));
			if (formData.isValid()) {
				savePerson(request, formData.getData());
				redirect(request, response, true);
			} else {
				// show validation errors
				renderForm(request, response, formData);
			}
		} else {
			// loading currently stored data to show it in the form
			FormData<Person> formData = new FormData<Person>(findPerson(request), null);
			renderForm(request, response, formData);
		}
	}

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, FormData<Person> formData) throws ServletException, IOException {
		FormMapping<Person> filledForm = personForm.fill(formData);
		request.setAttribute("form", filledForm);
		Map<Nation, String> nationItems = new HashMap<Nation, String>();
		for (Nation nation : Nation.values()) {
			nationItems.put(nation, nation.name()); 
		}
		request.setAttribute("nationItems", nationItems);
		if (request.getParameter(SUCCESS) != null) request.setAttribute(SUCCESS, "1");
		request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
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
		response.sendRedirect(request.getContextPath() + "/index.html" + (dataSaved ? ("?" + SUCCESS + "=1") : ""));
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
