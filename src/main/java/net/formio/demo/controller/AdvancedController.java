package net.formio.demo.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.demo.domain.Address;
import net.formio.demo.domain.AttendanceReason;
import net.formio.demo.domain.Collegue;
import net.formio.demo.domain.NewCollegue;
import net.formio.demo.domain.Registration;
import net.formio.demo.servlet.SessionStorage;
import net.formio.security.TokenException;
import net.formio.servlet.ServletRequestContext;
import net.formio.servlet.ServletRequestParams;
import net.formio.upload.UploadedFile;
import net.formio.upload.UploadedFileWrapper;
import net.formio.validation.ValidationResult;

/**
 * Advanced registration editing form controller.
 * @author Radek Beran
 */
public class AdvancedController extends AbstractBaseController {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_NAME = "advanced";
	private static final SessionStorage<Registration> regStorage = new SessionStorage<Registration>("registration");
	private static final SessionStorage<ArrayList<UploadedFileWrapper>> regCertsStorage = new SessionStorage<ArrayList<UploadedFileWrapper>>("registrationCertificates");
	private static final SessionStorage<UploadedFile> regCvStorage = new SessionStorage<UploadedFile>("registrationCv");
	private static final int MAX_CERTIFICATE_CNT = 3;
	
	// immutable definition of the form, can be freely shared/cached
	private static final FormMapping<Registration> registrationForm =
		Forms.automaticSecured(Registration.class, "registration")
			.nested(Forms.automaticSecured(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance")).build())
			.build();
			
//	private static final FormMapping<RegDate> regDateMapping = Forms.basic(RegDate.class, "regDate").fields("month", "year").build();
//	private static final FormMapping<Registration> registrationForm =
//		Forms.basic(Registration.class, "registration")
//		  // whitelist of properties to bind
//		  .fields("attendanceReasons", "cv", "interests", "email")
//		  .nested(Forms.automatic(UploadedFileWrapper.class, "certificates", null, MappingType.LIST).build())
//		  .nested(Forms.basic(Address.class, "contactAddress", Forms.factoryMethod(Address.class, "getInstance"))
//		    .fields("street", "city", "zipCode").build())
//		  .nested(Forms.basic(Collegue.class, "collegues", null, MappingType.LIST)
//		    .fields("name", "email")
//		    .nested(regDateMapping)
//		    .build())
//		  .nested(Forms.basic(NewCollegue.class, "newCollegue")
//		    .fields("name", "email")
//		    .nested(regDateMapping)
//		    .build())
//		  .build();

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(HTML_CONTENT_TYPE);
		RequestParams reqParams = new ServletRequestParams(request);
		if (reqParams.getRequestError() != null || reqParams.getParamValue("submitted") != null) {
			processFormSubmission(request, response, reqParams);
		} else if (reqParams.getRequestError() != null || reqParams.getParamValue("newCollegue") != null) {
			processNewCollegue(request, response, reqParams);
		} else if (reqParams.getParamValue("removeCollegue") != null) {
			processRemoveCollegue(request, response, reqParams);
		} else if (reqParams.getParamValue("removeCertificate") != null) {
			processRemoveCertificate(request, response, reqParams);
		} else {
			// no submission, loading currently stored data to show it in the form
			// log.info(registrationForm + "\n");
			Registration reg = findRegistration(request);
			FormData<Registration> formData = new FormData<Registration>(reg, ValidationResult.empty);
			renderForm(request, response, formData);
		}
	}

	protected void processRemoveCollegue(HttpServletRequest request, HttpServletResponse response, RequestParams reqParams) throws IOException {
		String indexAsStr = reqParams.getParamValue("removeCollegue");
		// Form cannot be bound when using GET for remove: FormData<Registration> formData = registrationForm.bind(reqParams);
		// Request does not provide all necessary data.
		// Registration reg = formData.getData();
		Registration reg = findRegistration(request);
		removeCollegue(request, Integer.valueOf(indexAsStr).intValue(), reg);
		redirect(request, response, PAGE_NAME, false);
	}
	
	protected void processRemoveCertificate(HttpServletRequest request, HttpServletResponse response, RequestParams reqParams) throws IOException {
		// Form cannot be bound when using GET for remove: FormData<Registration> formData = registrationForm.bind(reqParams);
		// Request does not provide all necessary data.
		// Registration reg = formData.getData();
		Registration reg = findRegistration(request);
		String indexAsStr = reqParams.getParamValue("removeCertificate");
		removeCertificate(request, Integer.valueOf(indexAsStr).intValue(), reg);
		redirect(request, response, PAGE_NAME, false);
	}

	protected void processNewCollegue(HttpServletRequest request, HttpServletResponse response, RequestParams reqParams) throws IOException, ServletException {
		// Only validations with beanvalidation group NewCollegue.New.class will be triggered
		FormData<Registration> newCollegueFormData = registrationForm.bind(reqParams, DEFAULT_LOCALE, NewCollegue.New.class);
		if (newCollegueFormData.isValid()) {
			Registration reg = newCollegueFormData.getData();
			updateWithRememberedFiles(request, reg);
			rememberFilesTemporarily(request, reg);
			addNewCollegue(request, newCollegueFormData.getData().getNewCollegue(), reg);
			redirect(request, response, PAGE_NAME, false);
		} else {
			// show validation errors
			Registration reg = newCollegueFormData.getData();
			reg.setNewCollegue(newCollegueFormData.getData().getNewCollegue()); // invalid new collegue
			updateWithRememberedFiles(request, reg);
			rememberFilesTemporarily(request, reg);
			
			FormData<Registration> formData = new FormData<Registration>(reg, newCollegueFormData.getValidationResult()); // with validation errors
			renderForm(request, response, formData);
		}
	}

	protected void processFormSubmission(HttpServletRequest request, HttpServletResponse response, RequestParams reqParams) throws IOException, ServletException {
		try {
			FormData<Registration> formData = registrationForm.bind(reqParams, DEFAULT_LOCALE); // shown form data updated from request right here
			if (formData.isValid()) {
				saveRegistration(request, formData.getData());
				redirect(request, response, PAGE_NAME, true);
			} else {
				// show validation errors
				updateWithRememberedFiles(request, formData.getData());
				rememberFilesTemporarily(request, formData.getData());
				renderForm(request, response, formData);
			}
		} catch (TokenException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	protected void renderForm(HttpServletRequest request, HttpServletResponse response, 
		FormData<Registration> formData) throws ServletException, IOException {
		updateWithRememberedFiles(request, formData.getData());
		log.info(registrationForm + "\n");
		FormMapping<Registration> filledForm = registrationForm.fill(formData, DEFAULT_LOCALE, new ServletRequestContext(request));
		log.info(filledForm + "\n");
		
		// Passing form to the template
		request.setAttribute("interests", Registration.allInterests()); // codebook
		request.setAttribute("attendanceReasons", AttendanceReason.values()); // codebook
		super.renderForm(request, response, filledForm, PAGE_NAME);
	}
	
	private void saveRegistration(HttpServletRequest request, Registration newReg) {
		updateWithRememberedFiles(request, newReg);
		if (newReg.getCertificates() != null && !newReg.getCertificates().isEmpty()) {
			for (UploadedFileWrapper file : newReg.getCertificates()) {
				if (file != null && !file.isEmpty()) {
					saveFile(file.getFile());
				}
			}
		}
		if (newReg.getCv() != null) {
			saveFile(newReg.getCv());
		}
		clearRememberedFiles(request);
		// Files were stored, data of files are not hold any more, we will reset the files
		newReg.setCertificates(initCertificates());
		regStorage.storeData(request.getSession(), newReg);
	}
	
	private Registration findRegistration(HttpServletRequest request) {
		Registration reg = regStorage.findData(request.getSession());
		if (reg == null) {
			reg = initRegistration();
		}
		return reg;
	}
	
	private void addNewCollegue(HttpServletRequest request, NewCollegue newCollegue, Registration registration) {
		List<Collegue> newCollegues = new ArrayList<Collegue>(registration.getCollegues());
		newCollegues.add(newCollegue.toCollegue());
		registration.setCollegues(newCollegues);
		registration.setNewCollegue(new NewCollegue());
		regStorage.storeData(request.getSession(), registration);
	}
	
	private void removeCollegue(HttpServletRequest request, int index, Registration registration) {
		updateWithRememberedFiles(request, registration);
		if (registration.getCollegues() != null && index >= 0 && index < registration.getCollegues().size()) {
			List<Collegue> newCollegues = new ArrayList<Collegue>(registration.getCollegues());
			newCollegues.remove(index);
			registration.setCollegues(newCollegues);
		}
		regStorage.storeData(request.getSession(), registration);
	}
	
	private void removeCertificate(HttpServletRequest request, int index, Registration registration) {
		updateWithRememberedFiles(request, registration);
		if (registration.getCertificates() != null && index >= 0 && index < registration.getCertificates().size()) {
			List<UploadedFileWrapper> newCerts = new ArrayList<UploadedFileWrapper>(registration.getCertificates());
			newCerts.remove(index);
			newCerts = appendEmptyCertsUpToMax(newCerts);
			registration.setCertificates(newCerts);
			regCertsStorage.storeData(request.getSession(), (ArrayList<UploadedFileWrapper>)registration.getCertificates());
		}
		regStorage.storeData(request.getSession(), registration);
	}
	
	private void saveFile(UploadedFile file) {
		try {
			if (file != null) {
				ReadableByteChannel ich = null; // file.getContent();
				FileOutputStream fos = null;
				try {
			    	// fos = new FileOutputStream("C:\\someDir\\" + file.getFileName());
			    	// fos.getChannel().transferFrom(ich, 0, Long.MAX_VALUE);
					log.info("File " + file.getFileName() + " was successfully processed.");
			    } finally {
			    	if (fos != null) fos.close();
			    	if (ich != null) ich.close();
			    	file.deleteTempFile();
			    }
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	private Registration initRegistration() {
		Set<AttendanceReason> attendanceReasons = new HashSet<AttendanceReason>();
		attendanceReasons.add(AttendanceReason.COMPANY_INTEREST);
		Registration aRegistration = new Registration(attendanceReasons);
		aRegistration.setInterests(new int[] {Registration.ds.getInterestId(), Registration.webFrameworks.getInterestId()});
		aRegistration.setContactAddress(Address.getInstance("Milady Horakove 22", "Praha", "16000"));
		
		List<Collegue> collegues = new ArrayList<Collegue>();
		Collegue michael = new Collegue();
		michael.setName("Michael");
		michael.setEmail("michael@email.com");
		collegues.add(michael);
		
		Collegue jane = new Collegue();
		jane.setName("Jane");
		jane.setEmail("jane@email.com");
		collegues.add(jane);
		
		aRegistration.setCollegues(collegues);
		
		List<UploadedFileWrapper> certs = initCertificates();
		aRegistration.setCertificates(certs);
		return aRegistration;
	}

	private List<UploadedFileWrapper> initCertificates() {
		List<UploadedFileWrapper> certs = new ArrayList<UploadedFileWrapper>();
		certs = appendEmptyCertsUpToMax(certs);
		return certs;
	}
	
	private void rememberFilesTemporarily(HttpServletRequest request, Registration reg) {
		List<UploadedFileWrapper> certs = appendEmptyCertsUpToMax(reg.getCertificates());
		reg.setCertificates(certs);
		if (reg.getCertificates() != null && !reg.getCertificates().isEmpty()) {
			regCertsStorage.storeData(request.getSession(), (ArrayList<UploadedFileWrapper>)reg.getCertificates());
		}
		if (reg.getCv() != null) {
			regCvStorage.storeData(request.getSession(), reg.getCv());
		}
	}
	
	private void updateWithRememberedFiles(HttpServletRequest request, Registration reg) {
		List<UploadedFileWrapper> list = regCertsStorage.findData(request.getSession());
		if (reg.getCertificates() == null || reg.getCertificates().isEmpty()) {
			if (list != null) {
				list = appendEmptyCertsUpToMax(list);
			} else {
				list = initCertificates();
			}
			reg.setCertificates(list);
		} else if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				UploadedFileWrapper wr = list.get(i);
				if (wr != null && !wr.isEmpty() && i < reg.getCertificates().size() && (reg.getCertificates().get(i) == null || reg.getCertificates().get(i).isEmpty())) {
					reg.getCertificates().set(i, wr);
				}
			}
			if (list.size() > reg.getCertificates().size()) {
				for (int i = reg.getCertificates().size(); i < list.size(); i++) {
					reg.getCertificates().add(list.get(i));
				}
			}
		}
		if (reg.getCv() == null) {
			reg.setCv(regCvStorage.findData(request.getSession()));
		}
	}
	
	private List<UploadedFileWrapper> appendEmptyCertsUpToMax(List<UploadedFileWrapper> list) {
		List<UploadedFileWrapper> res = new ArrayList<UploadedFileWrapper>();
		if (list != null) {
			res.addAll(list);
		}
		for (int i = res.size(); i < MAX_CERTIFICATE_CNT; i++) {
			res.add(new UploadedFileWrapper());
		}
		return res;
	}

	private void clearRememberedFiles(HttpServletRequest request) {
		regCertsStorage.removeData(request.getSession());
		regCvStorage.removeData(request.getSession());
	}

}
