package org.mybatis.generator.config.foregin;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;

public class AssociationTable extends AssociationRootTable {

	private String column;

	private String mappedColumn;

	private String mappedField = "";

	private FullyQualifiedJavaType mappedType;

	private AssociationRootTable parent;

	private Field associationField;

	private Method associationFieldGetterMethod;

	private Method associationFieldSetterMethod;

	private String linkedMappedField = "";

	private String linkedMappedFieldInMethodFragment = "";

	public boolean isParentAssociationTable(String tableName) {
		if (parent instanceof AssociationTable) {
			return ((AssociationTable) parent).isParentAssociationTable(tableName);
		} else {
			return parent.getName().equals(tableName);
		}
	}

	public String getLinkedMappedFieldInMethodFragment() {
		return linkedMappedFieldInMethodFragment;
	}

	public void setLinkedMappedFieldInMethodFragment(String linkedMappedFieldInMethodFragment) {
		this.linkedMappedFieldInMethodFragment = linkedMappedFieldInMethodFragment;
	}

	public String getLinkedMappedField() {
		return linkedMappedField;
	}

	public void setLinkedMappedField(String linkedMappedField) {
		this.linkedMappedField = linkedMappedField;
	}

	public Field getAssociationField() {
		return associationField;
	}

	public void setAssociationField(Field associationField) {
		this.associationField = associationField;
	}

	public Method getAssociationFieldGetterMethod() {
		return associationFieldGetterMethod;
	}

	public void setAssociationFieldGetterMethod(Method associationFieldGetterMethod) {
		this.associationFieldGetterMethod = associationFieldGetterMethod;
	}

	public Method getAssociationFieldSetterMethod() {
		return associationFieldSetterMethod;
	}

	public void setAssociationFieldSetterMethod(Method associationFieldSetterMethod) {
		this.associationFieldSetterMethod = associationFieldSetterMethod;
	}

	public FullyQualifiedJavaType getMappedType() {
		return mappedType;
	}

	public void setMappedType(FullyQualifiedJavaType mappedType) {
		this.mappedType = mappedType;
	}

	public String getMappedField() {
		return mappedField;
	}

	public void setMappedField(String mappedField) {
		this.mappedField = mappedField;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getMappedColumn() {
		return mappedColumn;
	}

	public void setMappedColumn(String mappedColumn) {
		this.mappedColumn = mappedColumn;
	}

	public AssociationRootTable getParent() {
		return parent;
	}

	public void setParent(AssociationRootTable parent) {
		this.parent = parent;
	}

}
