package org.mybatis.generator.config.foregin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;

public class AssociationRootTable {

	public static final String LINKED_ASSOCIATION_MAPPED_FIELD_OVERRIDE = "base";

	private IntrospectedTable introspectedTable;

	private Field fieldColumnMapField;

	protected String alias;

	private String baseColumnListId;

	private String blobColumnListId;

	private String name;

	private Map<String, ArrayDeque<String>> fields = new HashMap<String, ArrayDeque<String>>();

	private Map<String, ArrayDeque<String>> usedAliases = new HashMap<String, ArrayDeque<String>>();

	private final static String UNIQUE_MATCH = ".+_([0-9]+)";

	public static final String ENABLE_ALL_ASSOCIATION_METHOD_NAME = "enableAllAssociation";
	public static final String DISABLE_ALL_ASSOCIATION_METHOD_NAME = "disableAllAssociation";

	private List<AssociationTable> children = new ArrayList<AssociationTable>();

	public String getUniqueField(String mappedField) {
		if (fields.containsKey(mappedField)) {
			ArrayDeque<String> queue = fields.get(mappedField);
			String lastField = queue.peekLast();
			int index = 1;
			if (Pattern.matches(UNIQUE_MATCH, lastField)) {
				index = Integer.parseInt(lastField.substring(lastField.lastIndexOf("_") + 1));
				index++;
			}
			mappedField = mappedField + "_" + index;
			queue.offer(mappedField);
		} else {
			ArrayDeque<String> queue = new ArrayDeque<String>();
			queue.offer(mappedField);
			fields.put(mappedField, queue);
		}
		return mappedField;
	}

	public String getUniqueAlias(String alias) {
		if (usedAliases.containsKey(alias)) {
			ArrayDeque<String> queue = usedAliases.get(alias);
			String lastAlias = queue.peekLast();
			int index = 1;
			if (Pattern.matches(UNIQUE_MATCH, alias)) {
				index = Integer.parseInt(lastAlias.substring(lastAlias.lastIndexOf("_") + 1));
				index++;
			}
			alias = alias + "_" + index;
			queue.offer(alias);
		} else {
			ArrayDeque<String> queue = new ArrayDeque<String>();
			queue.offer(alias);
			usedAliases.put(alias, queue);
		}
		return alias;
	}

	public Field getFieldColumnMapField() {
		return fieldColumnMapField;
	}

	public void setFieldColumnMapField(Field fieldColumnMapField) {
		this.fieldColumnMapField = fieldColumnMapField;
	}

	public String getBaseColumnListId() {
		return baseColumnListId;
	}

	public void setBaseColumnListId(String baseColumnListId) {
		this.baseColumnListId = baseColumnListId;
	}

	public String getBlobColumnListId() {
		return blobColumnListId;
	}

	public void setBlobColumnListId(String blobColumnListId) {
		this.blobColumnListId = blobColumnListId;
	}

	public List<AssociationTable> getChildren() {
		return children;
	}

	public void setChildren(List<AssociationTable> children) {
		this.children = children;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IntrospectedTable getIntrospectedTable() {
		return introspectedTable;
	}

	public void setIntrospectedTable(IntrospectedTable introspectedTable) {
		this.introspectedTable = introspectedTable;
	}

}
