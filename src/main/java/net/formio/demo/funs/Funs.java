package net.formio.demo.funs;

import java.util.Collection;

import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;

public class Funs {

	public static boolean contains(Collection<?> col, Object o) {
		return col != null && col.contains(o);
	}
	
	public static String maxSeverity(Collection<ConstraintViolationMessage> col) {
		Severity maxSeverity = null;
		if (col != null && !col.isEmpty()) {
			maxSeverity = Severity.INFO;
			for (ConstraintViolationMessage m : col) {
				if (m.getSeverity().ordinal() > maxSeverity.ordinal()) {
					maxSeverity = m.getSeverity(); 
				}
			}
		}
		return maxSeverity != null ? maxSeverity.getStyleClass() : "";
	}

}
