/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects.
 * Copyright (C) "2016”  Christopher Mohr, David Wojnar, Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package life.qbic.projectbrowser.model;

import java.io.Serializable;
import java.util.List;

public class PropertyBean implements Serializable {
	
	private String label;
	private String code;
	private String type;
	private String description;
	private Object value;
	private List<String> vocabularyValues;
	
	public PropertyBean(String label, String code, String description, Object value) {
		this.label = label;
		this.code = code;
		this.description = description;
		this.setValue(value);
	}

	public PropertyBean() {
		/* TODO Auto-generated constructor stub */
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public List<String> getVocabularyValues() {
		return vocabularyValues;
	}

	public void setVocabularyValues(List<String> vocabularyValues) {
		this.vocabularyValues = vocabularyValues;
	}
}
