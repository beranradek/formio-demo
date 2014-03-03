package com.examples.forms.funs;

import java.util.Collection;

public class Funs {

	public static boolean contains(Collection<?> col, Object o) {
		return col != null && col.contains(o);
	}

}
