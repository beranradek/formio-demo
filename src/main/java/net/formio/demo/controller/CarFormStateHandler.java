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
package net.formio.demo.controller;

import net.formio.AbstractRequestParams;
import net.formio.RequestParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.error.AjaxAlertErrorHandler;
import net.formio.data.FormStateHandler;
import net.formio.demo.domain.car.Car;
import net.formio.demo.service.CarService;
import net.formio.servlet.ServletRequestParams;
import net.formio.servlet.common.SessionAttributeStorage;

import org.slf4j.Logger;

/**
 * Manipulates (loads/saves) car form state and handles possible error
 * that occured during the AJAX request. 
 * @author Radek Beran
 */
public class CarFormStateHandler extends AjaxAlertErrorHandler<Car> implements FormStateHandler<Car> {
	private static final SessionAttributeStorage<Car> formStateStorage = 
		new SessionAttributeStorage<Car>(CarFormStateHandler.class.getSimpleName() + "_formState");

	private final CarService carService;
	private final Logger log;
	
	public CarFormStateHandler(CarService carService, Logger log) {
		this.carService = carService;
		this.log = log;
	}
	
	@Override
	public Car findFormState(RequestParams requestParams) {
		ServletRequestParams params = (ServletRequestParams)requestParams;
		Car formState = formStateStorage.findData(params.getRequest().getSession());
		if (formState == null) {
			// Form state should be the same object as the one used to fill the form with data,
			// so the form state corresponds to filled data (e.g. state of checkbox that
			// influence visibility of other form part).
			formState = carService.createNewCar();
		}
		return formState;
	}

	@Override
	public void saveFormState(RequestParams requestParams, Car formState) {
		ServletRequestParams params = (ServletRequestParams)requestParams;
		formStateStorage.storeData(params.getRequest().getSession(), formState); // save updated form state to session
	}

	@Override
	public AjaxResponse<Car> errorResponse(AbstractRequestParams params, Throwable t) {
		log.error(t.getMessage(), t);
		return super.errorResponse(params, t);
	}
	
}