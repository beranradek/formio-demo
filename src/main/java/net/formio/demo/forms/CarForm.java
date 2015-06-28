/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.formio.demo.forms;

import java.util.List;
import java.util.Locale;

import net.formio.AbstractRequestParams;
import net.formio.Field;
import net.formio.FormData;
import net.formio.FormElement;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.MappingType;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.JsEvent;
import net.formio.ajax.action.AjaxAction;
import net.formio.ajax.action.FormStateAjaxAction;
import net.formio.ajax.action.JsEventToAction;
import net.formio.ajax.error.AjaxAlertErrorHandler;
import net.formio.demo.controller.CarFormStateHandler;
import net.formio.demo.domain.car.Accessories;
import net.formio.demo.domain.car.Car;
import net.formio.demo.domain.car.CarBrand;
import net.formio.demo.domain.car.CarModel;
import net.formio.demo.domain.car.Engine;
import net.formio.demo.service.CarService;
import net.formio.demo.utils.Sleeper;
import net.formio.props.types.InlinePosition;
import net.formio.render.BasicFormRenderer;
import net.formio.render.RenderContext;
import net.formio.render.tdi.InsertionPosition;
import net.formio.render.tdi.TdiResponseBuilder;
import net.formio.validation.validators.WholeNumberValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Car form definition with handling AJAX actions.
 * @author Radek Beran
 */
public class CarForm {
	
	private static final Locale FORM_LOCALE = Locale.ENGLISH;
	protected static final Logger log = LoggerFactory.getLogger(CarForm.class);
	
	private final String urlBase;
	private final CarService carService;
	private final BasicFormRenderer formRenderer;
	private final CarFormStateHandler formStateHandler;
	
	public CarForm(String urlBase) {
		this.urlBase = urlBase;
		this.carService = new CarService();
		this.formStateHandler = new CarFormStateHandler(new AjaxAlertErrorHandler<Car>(), carService, log);
		this.formRenderer = new BasicFormRenderer(new RenderContext(FormConstants.DEFAULT_LOCALE));
	}
	
	/** Returns form definition based on current form state. */
	public FormMapping<Car> definition(final Car formState) {
		return Forms.basic(Car.class, "carForm")
			.dataAjaxActions(new JsEventToAction[] {
				new JsEventToAction<Car>("addAccessoriesForm-addAccessories", addAccessories()),
				new JsEventToAction<Car>("save", saveAllChanges()) 
			})
			.field("carId", Field.HIDDEN)
			.field(Forms.field("brand", Field.DROP_DOWN_CHOICE)
				.required(true)
				.choices(carService.findCarBrands())
				.chooseOptionDisplayed(true) 
				.chooseOptionTitle("Choose car brand")
				.dataAjaxActions(new JsEventToAction<Car>(JsEvent.CHANGE, brandChanged())))
			.field(Forms.field("model", Field.DROP_DOWN_CHOICE)
				.required(true)
				.choices(carService.findCarModels(formState.getBrand() != null ? formState.getBrand().getId().intValue() : 0))
				.chooseOptionDisplayed(true)
				.chooseOptionTitle("Choose car model")
				.enabled(formState.getBrand() != null)
				.dataAjaxActions(new JsEventToAction<Car>(JsEvent.BLUR, refreshErrors())))
			.field(Forms.field("color").visible(false))
			.field(Forms.field("withEngineDetails", Field.CHECK_BOX)
				.dataAjaxActions(new JsEventToAction<Car>(engineDetailsCheckChanged())))
			.nested(Forms.basic(Engine.class, "engine")
				.visible(formState.isWithEngineDetails())
				.field(Forms.<Integer>field("cylinderCount")
					.colInputWidth(2)
					.required(true)
					.validator(WholeNumberValidator.<Integer>range(2, 8))
					.dataAjaxActions(new JsEventToAction<Car>(JsEvent.BLUR, refreshErrors())))	
				.field(Forms.field("volume")
					.colInputWidth(2)
					.required(true)
					.dataAjaxActions(new JsEventToAction<Car>(JsEvent.BLUR, refreshErrors())))
				.build())
			.nested(Forms.basic(Accessories.class, "accessoriesList", MappingType.LIST)
				.field(Forms.field("name").required(true))
				.field(Forms.field("quantity")
					.required(true)
					.inline(InlinePosition.FIRST)
					.colInputWidth(2))
				.field(Forms.field("removeAccessoriesUrl", Field.LINK)
					.detached(true)
					.dataAjaxActions(new JsEventToAction<Car>(removeAccessories()))
					.confirmMessage("Do you really want to remove the accessories?")
					.inline(InlinePosition.LAST))
				.build())
			.build(Forms.config().locale(FORM_LOCALE).urlBase(urlBase).build());
	}
	
	public final FormMapping<Accessories> addAccessoriesForm =
		Forms.basic(Accessories.class, "addAccessoriesForm").fields(
			Forms.field("name").placeholder("Name of accessories").colInputWidth(2).inline(InlinePosition.FIRST), 
			Forms.field("quantity").labelVisible(false).placeholder("Qty").colLabelWidth(1).colInputWidth(1).inline(InlinePosition.INNER),
			Forms.field("addAccessories", Field.BUTTON).detached(true).colInputWidth(1).inline(InlinePosition.LAST))
			.build(Forms.config().locale(FORM_LOCALE).build());
	
	public BasicFormRenderer getFormRenderer() {
		return formRenderer;
	}
	
	public CarFormStateHandler getFormStateHandler() {
		return formStateHandler;
	}
	
	public CarService getCarService() {
		return carService;
	}
	
	/** Process AJAX request: Updating selected brand on the server. */
	private AjaxAction<Car> brandChanged() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				TdiResponseBuilder rb = formRenderer.ajaxResponse();
				FormData<Car> formData = definition(formState).bind(requestParams);
				// Update form state from AJAX request
				formState.setBrand(formData.getData().getBrand());
				// Model field should be refreshed using AJAX response (models specific for selected brand are offered)
				FormMapping<Car> filledCarMapping = definition(formState).fill(new FormData<Car>(formData.getData()));
				// refresh also the car brand field itself, so the required 
				// validation message can disappear if some value is now selected
				// (field must be filled with current value from AJAX request):
				// TODO: Provide method findElements that finds all elements with given names
			    // and throws exception if some is not found
				
				return new AjaxResponse<Car>(rb.update(filledCarMapping.getField(CarBrand.class, "brand"))
					.update(filledCarMapping.getField(CarModel.class, "model"))
					.focusForName(requestParams.getTdiAjaxSrcElementName()).asString(), formState);
			}
		};
	}
	
	/** Process AJAX request: Updating withEngineDetails on the server. */
	private AjaxAction<Car> engineDetailsCheckChanged() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				TdiResponseBuilder rb = formRenderer.ajaxResponse();
				FormData<Car> formData = definition(formState).bind(requestParams);
				// Update form state from AJAX request
				formState.setWithEngineDetails(formData.getData().isWithEngineDetails());
				// Visibility of engine mapping is changed according to the changed withEngineDetails value.
				// We must refresh the engine mapping using the Ajax response.
				FormMapping<Engine> engineMapping = definition(formState).getMapping(Engine.class, "engine");
				return new AjaxResponse<Car>(rb.update(engineMapping.fill(new FormData<Engine>(formState.getEngine()))).asString(), 
					formState);
			}
		};
	}
	
	/** Process AJAX request: Updating form element that invoked AJAX request (with current
	 * form state and validation errors). */
	private AjaxAction<Car> refreshErrors() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				FormElement<?> filledAjaxSrcElement = definition(formState).fillTdiAjaxSrcElement(requestParams, FORM_LOCALE);
				return new AjaxResponse<Car>(
					formRenderer.ajaxResponse().update(filledAjaxSrcElement).asString(), 
					formState);
			}
		};
	}

	/** Process AJAX request: Saving the whole form (refreshing the form with validation errors shown 
	 * or redirecting the page after success). */
	private AjaxAction<Car> saveAllChanges() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				// Data of the whole form was sent in this AJAX request (tdi class is placed on the form element)
				TdiResponseBuilder rb = formRenderer.ajaxResponse();
				FormData<Car> formData = definition(formState).bind(requestParams);
				if (formData.isValid()) {
					// In practise, save data from the request (form submission) to some persistent storage here...
					formState.updateWith(formData.getData());
					// Simulate longer saving by some sleep
					Sleeper.sleep(1000);
					// Delete form data from session if desired
					// formStateHandler.deleteFormState(requestParams);
					// Causes standard redirect, but via AJAX response (we are serving AJAX request) 
					rb.redirect(urlBase + "?" + FormConstants.SUCCESS + "=1");
				} else {
					// show validation errors, refresh whole form using AJAX response
					rb.update(definition(formState).fill(formData));
				}
				return new AjaxResponse<Car>(rb.asString(), formState);
			}
		};
	}
	
	/** Process AJAX request: Add car accessories to form model. */
	private AjaxAction<Car> addAccessories() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				FormData<Accessories> formData = addAccessoriesForm.bind(requestParams);
				TdiResponseBuilder rb = formRenderer.ajaxResponse();
				if (formData.isValid()) {
					Accessories accessories = formData.getData();
					formState.getAccessoriesList().add(accessories);
					formData = new FormData<Accessories>(carService.createNewAccessories());
					int newListSize = formState.getAccessoriesList().size();
					FormMapping<Car> filledForm = definition(formState).fill(new FormData<Car>(formState));
					List<FormMapping<Accessories>> accessoriesMappings = filledForm.getMapping(Accessories.class, "accessoriesList").getList();
					rb.insert(InsertionPosition.BEFORE, "carForm-accessoriesList-end",
						accessoriesMappings.get(newListSize - 1))
						.update("carForm-accessoriesList-size", "" + newListSize);
				}
				return new AjaxResponse<Car>(rb.update(addAccessoriesForm.fill(formData)).asString(), formState);
			}
		};
	}
	
	/** Process AJAX request: Remove car accessories from the model. */
	private AjaxAction<Car> removeAccessories() {
		return new FormStateAjaxAction<Car>(formStateHandler) {
			@Override
			public AjaxResponse<Car> applyToState(AbstractRequestParams requestParams, Car formState) {
				FormMapping<Car> filledForm = definition(formState).fill(new FormData<Car>(formState));
				FormElement<Object> srcLink = filledForm.findElement(requestParams.getTdiAjaxSrcElementName());
				if (srcLink != null) {
					if (srcLink.getParent().getIndex() != null) {
						// Change state - remove accessories with given index
						formState.getAccessoriesList().remove(srcLink.getParent().getIndex().intValue());
					}
					filledForm = definition(formState).fill(new FormData<Car>(formState));
					TdiResponseBuilder rb = formRenderer.ajaxResponse();
					return new AjaxResponse<Car>(rb.update(filledForm.getMapping(Accessories.class, "accessoriesList")).asString(), 
						formState);
				}
				log.error("Form element with AJAX action not found for " + requestParams.getTdiAjaxSrcElementName());
				return formStateHandler.errorResponse(requestParams, null);
			}
		};
	}
}
