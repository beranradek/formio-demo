package net.formio.demo.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.ContentTypes;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.ajax.action.AjaxAction;
import net.formio.demo.domain.car.Accessories;
import net.formio.demo.domain.car.Car;
import net.formio.demo.forms.CarForm;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.common.ServletResponses;

/**
 * Dynamic form controller showing AJAX actions including various accessibilities of form elements.
 * @author Radek Beran
 */
@WebServlet(urlPatterns = {"/dynamic.html"})
public class DynamicFormController extends AbstractBaseController {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_NAME = "dynamic";
	private static final String PAGE_URL = "/" + PAGE_NAME + ".html";
	private final CarForm carForm;

	public DynamicFormController() {
		this.carForm = new CarForm(PAGE_URL);
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletRequestParams params = new ServletRequestParams(request);
		Car formState = carForm.getFormStateHandler().findFormState(params);
		FormData<Car> formData = new FormData<>(formState);
		if (params.isTdiAjaxRequest()) {
			AjaxAction<Car> action = Forms.findAjaxAction(params, carForm.definition(formState).fill(formData));
			if (action == null) {
				// ServletResponses.notFound(response);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				ServletResponses.ajaxResponse(params, response, action, carForm.getFormStateHandler());
			}
		} else {
			// No (AJAX) action to process, just rendering the whole form
			renderWholeForm(request, response, formData);
		}
	}
	
	/** Render whole form. */
	private void renderWholeForm(HttpServletRequest request, HttpServletResponse response, FormData<Car> formData) throws ServletException, IOException {
		response.setContentType(ContentTypes.HTML);
		RequestParams params = new ServletRequestParams(request);
		FormMapping<Car> filledForm = carForm.definition(carForm.getFormStateHandler().findFormState(params)).fill(formData);
		request.setAttribute("formMarkup", carForm.getFormRenderer().renderElement(filledForm));
		
		FormMapping<Accessories> filledAddAccessoriesForm = carForm.addAccessoriesForm.fill(new FormData<>(
			carForm.getCarService().createNewAccessories()));
		request.setAttribute("addAccessoriesForm", carForm.getFormRenderer().renderElement(filledAddAccessoriesForm));
		
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}
}
