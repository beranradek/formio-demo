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
package net.formio.demo.domain.car;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.formio.upload.UploadedFileWrapper;

public class Car implements Serializable {
	private static final long serialVersionUID = -4971576602203269772L;

	private Long carId;

	private CarBrand brand;
	
	private CarModel model;

	private int color;
	
	private boolean withEngineDetails;
	
	private Engine engine;
	
	private List<Accessories> accessoriesList;
	
	private List<UploadedFileWrapper> attachments;
	
	public void updateWith(Car car) {
		this.setCarId(car.getCarId());
		this.setBrand(car.getBrand());
		this.setModel(car.getModel());
		this.setWithEngineDetails(car.isWithEngineDetails());
		this.setEngine(car.getEngine());
		this.setAccessoriesList(car.getAccessoriesList() != null ? new ArrayList<>(car.getAccessoriesList()) : new ArrayList<>());
		this.setAttachments(car.getAttachments() != null ? new ArrayList<>(car.getAttachments()) : new ArrayList<>());
		this.setColor(car.getColor());
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}
	
	public CarBrand getBrand() {
		return brand;
	}

	public void setBrand(CarBrand brand) {
		this.brand = brand;
	}

	public CarModel getModel() {
		return model;
	}

	public void setModel(CarModel model) {
		this.model = model;
	}

	public boolean isWithEngineDetails() {
		return withEngineDetails;
	}

	public void setWithEngineDetails(boolean withEngineDetails) {
		this.withEngineDetails = withEngineDetails;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public List<Accessories> getAccessoriesList() {
		return accessoriesList;
	}

	public void setAccessoriesList(List<Accessories> accessoriesList) {
		this.accessoriesList = accessoriesList;
	}

	public List<UploadedFileWrapper> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<UploadedFileWrapper> attachments) {
		this.attachments = attachments;
	}

}
