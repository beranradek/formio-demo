package net.formio.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import net.formio.binding.ArgumentName;
import net.formio.binding.Ignored;
import net.formio.demo.domain.validation.RegistrationValid;
import net.formio.upload.UploadedFile;
import net.formio.upload.UploadedFileWrapper;
import net.formio.validation.constraints.Email;

@RegistrationValid
public class Registration implements Serializable {
	private static final long serialVersionUID = -1568960763694971728L;

	private final Set<AttendanceReason> attendanceReasons;

	private UploadedFile cv;

	private List<UploadedFileWrapper> certificates;

	@Size(min=1, message="{constraints.interests.notEmpty}")
	private int[] interests;

	@Email
	private String email;

	@Valid
	private Address contactAddress;

	@Valid
	@Size(min=1, message="{constraints.collegues.notEmpty}")
	private List<Collegue> collegues;

	@Valid
	private NewCollegue newCollegue;
	
	private transient String internalId;

	public Registration(
		@ArgumentName("attendanceReasons") Set<AttendanceReason> attendanceReasons) {
		if (attendanceReasons == null)
			throw new IllegalArgumentException("attendanceReasons cannot be null, only empty");
		this.attendanceReasons = attendanceReasons;
	}

	public UploadedFile getCv() {
		return cv;
	}

	public void setCv(UploadedFile cv) {
		this.cv = cv;
	}

	public List<UploadedFileWrapper> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<UploadedFileWrapper> certificates) {
		this.certificates = certificates;
	}

	public int[] getInterests() {
		return interests;
	}

	public void setInterests(int[] interests) {
		this.interests = interests;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(Address contactAddress) {
		if (contactAddress == null) {
			this.contactAddress = Address.getInstance(null, null, null);
		} else {
			this.contactAddress = contactAddress;
		}
	}

	public static List<Interest> allInterests() {
		List<Interest> list = new ArrayList<Interest>();
		list.add(parallel);
		list.add(webFrameworks);
		list.add(ai);
		list.add(ds);
		return list;
	}

	public Set<AttendanceReason> getAttendanceReasons() {
		return attendanceReasons;
	}

	public List<Collegue> getCollegues() {
		return collegues;
	}

	public void setCollegues(List<Collegue> collegues) {
		this.collegues = collegues;
	}

	public NewCollegue getNewCollegue() {
		return newCollegue;
	}

	public void setNewCollegue(NewCollegue newCollegue) {
		this.newCollegue = newCollegue;
	}
	
	@Ignored
	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public static final Interest parallel = new Interest(1, "Parallel Computation");
	public static final Interest webFrameworks = new Interest(2, "Web frameworks");
	public static final Interest ai = new Interest(3, "AI");
	public static final Interest ds = new Interest(4, "Data Structures");

}
