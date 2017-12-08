package org.mybatis.generator.config.foregin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.utils.StringUtils;

public class AssociationUtils extends StringUtils {

	private static final String ASSOCIATION_FIELD_SUFFIX = "Association";

	private static final String COLUMNID_PREFIX = "Mapped";

	public static Field generateFieldColumnMapField(AssociationRootTable table) {
		Field fieldColumnMap = new Field();
		fieldColumnMap.setVisibility(JavaVisibility.PUBLIC);
		fieldColumnMap.setStatic(true);
		FullyQualifiedJavaType type = FullyQualifiedJavaType.getNewMapInstance();
		type.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
		type.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
		fieldColumnMap.setType(type);
		fieldColumnMap.setInitializationString("new HashMap<String,String>()");
		String fieldColumnMapName_suffix = "FieldColumnMap";
		String fieldColumnMapName = "";
		if (table instanceof AssociationTable) {
			fieldColumnMapName = getLinkedAssociationMappedField(table, "_", null) + fieldColumnMapName_suffix;
		} else {
			fieldColumnMapName = AssociationRootTable.LINKED_ASSOCIATION_MAPPED_FIELD_OVERRIDE
					+ fieldColumnMapName_suffix;
		}
		fieldColumnMap.setName(fieldColumnMapName);
		fieldColumnMap.setFinal(true);
		return fieldColumnMap;
	}

	public static Field generateAssociationField(AssociationTable associationTable) {
		String fieldName = getLinkedAssociationMappedField(associationTable, "_", null) + ASSOCIATION_FIELD_SUFFIX;
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setName(fieldName);
		return field;
	}

	public static Method generateAssociationFieldGetterMethod(AssociationTable associationTable) {
		String fieldName = generateAssociationField(associationTable).getName();
		Method getterMethod = new Method();
		getterMethod.setVisibility(JavaVisibility.PUBLIC);
		getterMethod.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		getterMethod.setName("get" + upperCaseFirstChar(fieldName));
		String line = getLinkedAssociationMappedFields(associationTable, "_", " && ", ASSOCIATION_FIELD_SUFFIX, null);
		getterMethod.addBodyLine("return " + line + ";");
		return getterMethod;
	}

	public static Method generateAssociationFieldSetterMethod(AssociationTable associationTable) {
		String fieldName = generateAssociationField(associationTable).getName();
		Method setterMethod = new Method();
		setterMethod.setVisibility(JavaVisibility.PUBLIC);
		setterMethod.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), fieldName));
		setterMethod.setName("set" + upperCaseFirstChar(fieldName));
		String linkedFields = getLinkedAssociationMappedFields(associationTable, "_", "&&", ASSOCIATION_FIELD_SUFFIX,
				null);
		List<String> associationFieldNames = Arrays.asList(linkedFields.split("&&"));
		List<String> parentAssociationFieldNames = new ArrayList<String>(associationFieldNames);
		parentAssociationFieldNames.remove(parentAssociationFieldNames.size() - 1);
		if (parentAssociationFieldNames.size() > 0) {
			setterMethod.addBodyLine("if (" + fieldName + ") {");
			for (String parentAssociationFieldName : parentAssociationFieldNames) {
				setterMethod.addBodyLine("this." + parentAssociationFieldName + " = true;");
			}
			setterMethod.addBodyLine("}");
		}
		setterMethod.addBodyLine("this." + fieldName + " = " + fieldName + ";");
		return setterMethod;
	}

	public static String getLinkedColumnListId(AssociationRootTable associationTable, String suffix) {
		return COLUMNID_PREFIX + "_" + getLinkedAssociationMappedField(associationTable, "_", suffix);
	}

	private static String getLinkedAssociationMappedFields(AssociationRootTable associationTable,
			String delimiter_field, String delimiter_fields, String mappedFieldSuffix, String reslut) {
		if (associationTable instanceof AssociationTable) {
			String associationField = getLinkedAssociationMappedField(associationTable, delimiter_field, null)
					+ mappedFieldSuffix;
			if (isBlank(reslut)) {
				reslut = associationField;
			} else {
				reslut = associationField + delimiter_fields + reslut;
			}
			return getLinkedAssociationMappedFields(((AssociationTable) associationTable).getParent(), delimiter_field,
					delimiter_fields, mappedFieldSuffix, reslut);
		} else {
			return reslut;
		}
	}

	public static String getLinkedMappedField(AssociationRootTable associationTable) {
		return getLinkedAssociationMappedField(associationTable, ".", null);
	}

	public static String getLinkedMappedFieldInMethodFragment(AssociationRootTable associationTable) {
		return getLinkedAssociationMappedField(associationTable, "_", null);
	}

	private static String getLinkedAssociationMappedField(AssociationRootTable associationTable, String delimiter,
			String name) {
		if (associationTable instanceof AssociationTable) {
			String mappedField = ((AssociationTable) associationTable).getMappedField();
			if (isBlank(name)) {
				name = mappedField;
			} else {
				name = mappedField + delimiter + name;
			}
			return getLinkedAssociationMappedField(((AssociationTable) associationTable).getParent(), delimiter, name);
		} else {
			return name;
		}
	}

}
