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

import net.formio.binding.ArgumentName;
import net.formio.choice.Identified;
import net.formio.choice.Titled;

public abstract class Codebook implements Serializable, Identified<Integer>, Titled {
	private static final long serialVersionUID = -8748586234677100670L;
	private final int id;
	private final String title;
	
	public Codebook(@ArgumentName("id") int id, @ArgumentName("title") String title) {
		this.id = id;
		this.title = title;
	}

	@Override
	public Integer getId() {
		return Integer.valueOf(id);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Codebook))
			return false;
		Codebook other = (Codebook) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
