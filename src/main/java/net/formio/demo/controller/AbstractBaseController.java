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

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.FormMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base controller.
 * @author Radek Beran
 */
public abstract class AbstractBaseController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_PREFIX = "action_";
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractBaseController.class);
	protected static final String SUCCESS = "success";
	protected static final Locale DEFAULT_LOCALE = new Locale("en");
	protected static final String XML_CONTENT_TYPE = "application/xml; charset=UTF-8";
	protected static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
	protected static final String INFUSE_PARAM = "_infuse";

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
	
	protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, FormMapping<?> filledForm, String pageName) throws ServletException, IOException {
		request.setAttribute("form", filledForm);
		if (request.getParameter(SUCCESS) != null) request.setAttribute(SUCCESS, "1");
		request.getRequestDispatcher("/WEB-INF/jsp/" + pageName + ".jsp").forward(request, response);
	}
	
	protected void redirect(HttpServletRequest request,
		HttpServletResponse response, String pageName, boolean dataSaved) throws IOException {
		response.sendRedirect(request.getContextPath() + "/" + pageName + ".html" + (dataSaved ? ("?" + SUCCESS + "=1") : ""));
	}
	
	/**
	 * Returns name of action parameter after "action_" prefix.
	 * @param request
	 * @return
	 */
	protected String getAction(HttpServletRequest request) {
		String action = null;
		for (String paramName : request.getParameterMap().keySet()) {
			if (paramName != null && paramName.toLowerCase().startsWith(ACTION_PREFIX)) {
				action = paramName.substring(ACTION_PREFIX.length());
				break;
			}
		}
		return action;
	}
	
	/**
	 * Returns true if given request is AJAX request.
	 * @param request
	 * @return
	 */
	protected boolean isAjaxRequest(HttpServletRequest request) {
		return request.getParameter(INFUSE_PARAM) != null;
	}
}
