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

import net.formio.RequestParams;
import net.formio.ajax.AjaxResponse;
import net.formio.ajax.error.AjaxErrorHandler;
import net.formio.demo.domain.car.Car;
import net.formio.demo.service.CarService;
import net.formio.servlet.data.SessionFormStateStorage;

import org.slf4j.Logger;

/**
 * Manipulates (loads/saves) car form state and handles possible error
 * that occured during the AJAX request. 
 * @author Radek Beran
 */
public class CarFormStateHandler extends SessionFormStateStorage<Car> implements AjaxErrorHandler<Car> {
	private final AjaxErrorHandler<Car> nestedAjaxErrorHandler;
	private final CarService carService;
	private final Logger log;
	
	public CarFormStateHandler(AjaxErrorHandler<Car> errorHandler, CarService carService, Logger log) {
		super(CarFormStateHandler.class.getSimpleName() + "_formState");
		this.nestedAjaxErrorHandler = errorHandler;
		this.carService = carService;
		this.log = log;
	}
	
	@Override
	public Car createNewFormState() {
		return carService.createNewCar();
	}
	
	@Override
	public AjaxResponse<Car> errorResponse(RequestParams params, Throwable t) {
		log.error(t.getMessage(), t);
		return nestedAjaxErrorHandler.errorResponse(params, t);
	}
	
}