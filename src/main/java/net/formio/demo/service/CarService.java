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
package net.formio.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.formio.demo.domain.car.Accessories;
import net.formio.demo.domain.car.Car;
import net.formio.demo.domain.car.CarBrand;
import net.formio.demo.domain.car.CarModel;
import net.formio.demo.domain.car.Engine;

/**
 * Car operations.
 * @author Radek Beran
 */
public class CarService {
	
	private final Map<Integer, List<CarModel>> modelsByBrands;
	
	public CarService() {
		Map<Integer, List<CarModel>> models = new LinkedHashMap<Integer, List<CarModel>>();
		models.put(Integer.valueOf(1), Arrays.asList(new CarModel(1, "RS 4 Avant"), new CarModel(2, "Quattro")));
		models.put(Integer.valueOf(2), Arrays.asList(new CarModel(50, "Grand Sport"), new CarModel(51, "Veyron")));
		models.put(Integer.valueOf(3), Arrays.asList(new CarModel(100, "911 Carrera"), new CarModel(101, "918 Spyder")));
		this.modelsByBrands = models;
	}
	
	public Accessories createNewAccessories() {
		return new Accessories(null, 0);
	}
	
	public Car createNewCar() {
		Car car = new Car();
		car.setBrand(null);
		car.setModel(null);
		car.setColor(200);
		Engine engine = new Engine();
		engine.setVolume(null);
		engine.setCylinderCount(null);
		car.setEngine(engine);
		List<Accessories> accessoriesList = new ArrayList<Accessories>();
		accessoriesList.add(new Accessories("Wheel Cleaner", 2));
		car.setAccessoriesList(accessoriesList);
		return car;
	}
	
	public List<CarBrand> findCarBrands() {
		List<CarBrand> brands = new ArrayList<CarBrand>();
		brands.add(new CarBrand(1, "Audi"));
		brands.add(new CarBrand(2, "Bugatti"));
		brands.add(new CarBrand(3, "Porsche"));
		return brands;
	}
	
	public List<CarModel> findCarModels(int brandId) {
		List<CarModel> models = modelsByBrands.get(Integer.valueOf(brandId));
		if (models == null) {
			models = new ArrayList<CarModel>();
		}
		return models;
	}
}
