/**
 *    Copyright 2006-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InitializationBlock;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.foregin.AssociationRootTable;
import org.mybatis.generator.config.foregin.AssociationTable;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ExampleWithAssociationGenerator extends AbstractJavaGenerator {

	public ExampleWithAssociationGenerator() {
		super();
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
		progressCallback.startTask(getString("Progress.6", table.toString())); //$NON-NLS-1$
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getExampleType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		if (getRootClass() != null) {
			String modelClass = introspectedTable.getBaseRecordType();
			FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(modelClass);
			if (getRootExampleClass() != null) {
				FullyQualifiedJavaType supper = new FullyQualifiedJavaType(getRootExampleClass());
				supper.addTypeArgument(modelType);
				topLevelClass.setSuperClass(supper.getShortName());
				topLevelClass.addImportedType(modelType);
				topLevelClass.addImportedType(supper);
			}
		}

		Method method = null;

		AssociationRootTable associationRootTable = introspectedTable.getTableConfiguration().getAssociationRootTable();
		Method enable_association_method = new Method();
		enable_association_method.setName(AssociationRootTable.ENABLE_ALL_ASSOCIATION_METHOD_NAME);
		enable_association_method.setVisibility(JavaVisibility.PUBLIC);
		Method disable_association_method = new Method();
		disable_association_method.setName(AssociationRootTable.DISABLE_ALL_ASSOCIATION_METHOD_NAME);
		disable_association_method.setVisibility(JavaVisibility.PUBLIC);
		if (associationRootTable.getChildren().size() > 0) {
			generateExampleAssociationConditionMethod(associationRootTable.getChildren(), topLevelClass,
					enable_association_method, disable_association_method);
		} else {
			enable_association_method.addBodyLine("");
			disable_association_method.addBodyLine("");
		}
		topLevelClass.addMethod(enable_association_method);
		topLevelClass.addMethod(disable_association_method);
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewMapInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewHashMapInstance());
		InitializationBlock block = new InitializationBlock(true);
		generateStaticFieldMap(associationRootTable, topLevelClass, block);
		topLevelClass.addInitializationBlock(block);

		// add default constructor
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setConstructor(true);
		method.setName(type.getShortName());
		method.addBodyLine("oredCriteria = new ArrayList<Criteria>();"); //$NON-NLS-1$

		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field, getter, setter for orderby clause
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setName("orderByClause"); //$NON-NLS-1$
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setOrderByClause"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "orderByClause")); //$NON-NLS-1$
		method.addBodyLine("this.orderByClause = orderByClause;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.setName("getOrderByClause"); //$NON-NLS-1$
		method.addBodyLine("return orderByClause;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field, getter, setter for distinct
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setName("distinct"); //$NON-NLS-1$
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setDistinct"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "distinct")); //$NON-NLS-1$
		method.addBodyLine("this.distinct = distinct;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		method.setName("isDistinct"); //$NON-NLS-1$
		method.addBodyLine("return distinct;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field and methods for the list of ored criteria
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);

		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<Criteria>"); //$NON-NLS-1$
		field.setType(fqjt);
		field.setName("oredCriteria"); //$NON-NLS-1$
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(fqjt);
		method.setName("getOredCriteria"); //$NON-NLS-1$
		method.addBodyLine("return oredCriteria;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("or"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getCriteriaInstance(), "criteria")); //$NON-NLS-1$
		method.addBodyLine("oredCriteria.add(criteria);"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("or"); //$NON-NLS-1$
		method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
		method.addBodyLine("Criteria criteria = createCriteriaInternal();"); //$NON-NLS-1$
		method.addBodyLine("oredCriteria.add(criteria);"); //$NON-NLS-1$
		method.addBodyLine("return criteria;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("createCriteria"); //$NON-NLS-1$
		method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
		method.addBodyLine("Criteria criteria = createCriteriaInternal();"); //$NON-NLS-1$
		method.addBodyLine("if (oredCriteria.size() == 0) {"); //$NON-NLS-1$
		method.addBodyLine("oredCriteria.add(criteria);"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$
		method.addBodyLine("return criteria;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("createCriteriaInternal"); //$NON-NLS-1$
		method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
		method.addBodyLine("Criteria criteria = new Criteria(this);"); //$NON-NLS-1$
		method.addBodyLine("return criteria;"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("clear"); //$NON-NLS-1$
		method.addBodyLine("oredCriteria.clear();"); //$NON-NLS-1$
		method.addBodyLine("orderByClause = null;"); //$NON-NLS-1$
		method.addBodyLine("distinct = false;"); //$NON-NLS-1$
		method.addBodyLine(AssociationTable.DISABLE_ALL_ASSOCIATION_METHOD_NAME + "();");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// now generate the inner class that holds the AND conditions
		topLevelClass.addInnerClass(getGeneratedCriteriaInnerClass(topLevelClass));

		topLevelClass.addInnerClass(getCriteriaInnerClass());

		topLevelClass.addInnerClass(getCriterionInnerClass());

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().modelExampleClassGenerated(topLevelClass, introspectedTable)) {
			answer.add(topLevelClass);
		}
		return answer;
	}

	private InnerClass getCriterionInnerClass() {
		Field field;
		Method method;

		InnerClass answer = new InnerClass(new FullyQualifiedJavaType("Criterion")); //$NON-NLS-1$
		answer.setVisibility(JavaVisibility.PUBLIC);
		answer.setStatic(true);
		context.getCommentGenerator().addClassComment(answer, introspectedTable);

		field = new Field();
		field.setName("condition"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("value"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getObjectInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("secondValue"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getObjectInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("noValue"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("singleValue"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("betweenValue"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("listValue"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("typeHandler"); //$NON-NLS-1$
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criterion"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addBodyLine("super();"); //$NON-NLS-1$
		method.addBodyLine("this.condition = condition;"); //$NON-NLS-1$
		method.addBodyLine("this.typeHandler = null;"); //$NON-NLS-1$
		method.addBodyLine("this.noValue = true;"); //$NON-NLS-1$
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criterion"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "typeHandler")); //$NON-NLS-1$
		method.addBodyLine("super();"); //$NON-NLS-1$
		method.addBodyLine("this.condition = condition;"); //$NON-NLS-1$
		method.addBodyLine("this.value = value;"); //$NON-NLS-1$
		method.addBodyLine("this.typeHandler = typeHandler;"); //$NON-NLS-1$
		method.addBodyLine("if (value instanceof List<?>) {"); //$NON-NLS-1$
		method.addBodyLine("this.listValue = true;"); //$NON-NLS-1$
		method.addBodyLine("} else {"); //$NON-NLS-1$
		method.addBodyLine("this.singleValue = true;"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criterion"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addBodyLine("this(condition, value, null);"); //$NON-NLS-1$
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criterion"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "typeHandler")); //$NON-NLS-1$
		method.addBodyLine("super();"); //$NON-NLS-1$
		method.addBodyLine("this.condition = condition;"); //$NON-NLS-1$
		method.addBodyLine("this.value = value;"); //$NON-NLS-1$
		method.addBodyLine("this.secondValue = secondValue;"); //$NON-NLS-1$
		method.addBodyLine("this.typeHandler = typeHandler;"); //$NON-NLS-1$
		method.addBodyLine("this.betweenValue = true;"); //$NON-NLS-1$
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criterion"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue")); //$NON-NLS-1$
		method.addBodyLine("this(condition, value, secondValue, null);"); //$NON-NLS-1$
		answer.addMethod(method);

		return answer;
	}

	private InnerClass getCriteriaInnerClass() {
		Method method;

		InnerClass answer = new InnerClass(FullyQualifiedJavaType.getCriteriaInstance());

		answer.setVisibility(JavaVisibility.PUBLIC);
		answer.setStatic(true);
		answer.setSuperClass(FullyQualifiedJavaType.getGeneratedCriteriaInstance());

		context.getCommentGenerator().addClassComment(answer, introspectedTable, true);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("Criteria");
		method.setConstructor(true);
		method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
		method.addBodyLine("super();");
		method.addBodyLine("super.example = example;");
		answer.addMethod(method);

		return answer;
	}

	private InnerClass getGeneratedCriteriaInnerClass(TopLevelClass topLevelClass) {

		InnerClass answer = new InnerClass(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
		generateBaseMethod(topLevelClass, answer);
		operatorType = new FullyQualifiedJavaType(getBaseSqlOperatorClass());
		topLevelClass.addImportedType(operatorType);
		AssociationRootTable associationRootTable = introspectedTable.getTableConfiguration().getAssociationRootTable();
		generateConditionMethod(associationRootTable, answer);

		return answer;
	}

	private FullyQualifiedJavaType operatorType;

	private void generateStaticFieldMap(AssociationRootTable table, TopLevelClass topLevelClass,
			InitializationBlock block) {
		Field fieldColumnMap = table.getFieldColumnMapField();
		topLevelClass.addField(fieldColumnMap);
		StringBuilder line = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : table.getIntrospectedTable().getNonBLOBColumns()) {
			String javaProperty = introspectedColumn.getJavaProperty();
			String mappedColumn = MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn);
			if (table instanceof AssociationTable) {
				mappedColumn = table.getAlias() + "." + mappedColumn;
			} else {
				if (table.getChildren().size() > 0) {
					mappedColumn = table.getAlias() + "." + mappedColumn;
				}
			}
			line.setLength(0);
			line.append(fieldColumnMap.getName());
			line.append(".put(\"" + javaProperty + "\", ");
			line.append("\"" + mappedColumn + "\");");
			block.addBodyLine(line.toString());
		}
		for (AssociationTable child : table.getChildren()) {
			generateStaticFieldMap(child, topLevelClass, block);
		}
	}

	private void generateExampleAssociationConditionMethod(List<AssociationTable> children,
			TopLevelClass topLevelClass, Method enable_associationAllMethod, Method disable_associationAllMethod) {
		for (AssociationTable associationTable : children) {
			Field associationField = associationTable.getAssociationField();
			topLevelClass.addField(associationField);
			topLevelClass.addMethod(associationTable.getAssociationFieldGetterMethod());
			topLevelClass.addMethod(associationTable.getAssociationFieldSetterMethod());
			enable_associationAllMethod.addBodyLine(associationField.getName() + " = true;");
			disable_associationAllMethod.addBodyLine(associationField.getName() + " = false;");
			if (associationTable.getChildren().size() > 0) {
				generateExampleAssociationConditionMethod(associationTable.getChildren(), topLevelClass,
						enable_associationAllMethod, disable_associationAllMethod);
			}
		}
	}

	private void generateConditionMethod(AssociationRootTable associationRootTable, InnerClass answer) {
		answer.addMethod(getVarargsMethod(associationRootTable));
		for (AssociationTable child : associationRootTable.getChildren()) {
			generateConditionMethod(child, answer);
		}
	}

	private Method getVarargsMethod(AssociationRootTable table) {
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "field"));
		method.addParameter(new Parameter(operatorType, "sqlOperator"));
		method.addParameter(new Parameter(new FullyQualifiedJavaType("Object..."), "values")); //$NON-NLS-1$
		StringBuilder sb = new StringBuilder();
		String methodNameFragment = null;
		String setAssociationMethod = null;
		Field fieldColumnMap = table.getFieldColumnMapField();
		String linkedMappedField = null;
		if (table instanceof AssociationTable) {
			methodNameFragment = ((AssociationTable) table).getLinkedMappedFieldInMethodFragment();
			setAssociationMethod = ((AssociationTable) table).getAssociationFieldSetterMethod().getName();
			linkedMappedField = ((AssociationTable) table).getLinkedMappedField();
		} else {
			methodNameFragment = AssociationRootTable.LINKED_ASSOCIATION_MAPPED_FIELD_OVERRIDE;
		}

		sb.append(upperCaseFirstChar(methodNameFragment));
		sb.insert(0, "and");
		sb.append("Condition");
		method.setName(sb.toString());
		method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
		sb.setLength(0);
		method.addBodyLine("String column = " + fieldColumnMap.getName() + ".get(field);");
		method.addBodyLine("String operator = sqlOperator.getOperator();");
		method.addBodyLine("String condition = column + \" \" + operator;");
		method.addBodyLine("if (sqlOperator.isNoValue()) {");
		method.addBodyLine("addCriterion(condition);");
		method.addBodyLine("} else if (sqlOperator.isSingleValue() || sqlOperator.isArrayValue()) {");
		if (table instanceof AssociationTable) {
			method.addBodyLine("addCriterion(condition, values[0], \"" + linkedMappedField + "." + "\" + field);");
		} else {
			method.addBodyLine("addCriterion(condition, values[0], field);");
		}
		method.addBodyLine("} else if (sqlOperator.isBetweenValue()) {");
		if (table instanceof AssociationTable) {
			method.addBodyLine("addCriterion(condition, values[0], values[1], \"" + linkedMappedField + "."
					+ "\" + field);");
		} else {
			method.addBodyLine("addCriterion(condition, values[0], values[1], field);");
		}
		method.addBodyLine("}");

		if (isNotBlank(setAssociationMethod)) {
			method.addBodyLine("example." + setAssociationMethod + "(true);");
		}
		method.addBodyLine("return (Criteria) this;"); //$NON-NLS-1$

		return method;
	}

	public void generateBaseMethod(TopLevelClass topLevelClass, InnerClass answer) {
		Field field;
		Method method;

		if (getRootClass() != null) {
			String modelClass = introspectedTable.getBaseRecordType();
			FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(modelClass);
			if (getRootCriteriaClass() != null) {
				FullyQualifiedJavaType criteriaSupper = new FullyQualifiedJavaType(getRootCriteriaClass());
				criteriaSupper.addTypeArgument(modelType);
				answer.setSuperClass(criteriaSupper.getShortName());
				topLevelClass.addImportedType(criteriaSupper);
			}
		}

		answer.setVisibility(JavaVisibility.PROTECTED);
		answer.setStatic(true);
		answer.setAbstract(true);
		context.getCommentGenerator().addClassComment(answer, introspectedTable);

		field = new Field();
		field.setName("example");
		field.setType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
		field.setVisibility(JavaVisibility.PROTECTED);
		answer.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("GeneratedCriteria"); //$NON-NLS-1$
		method.setConstructor(true);
		method.addBodyLine("super();"); //$NON-NLS-1$
		method.addBodyLine("criteria = new ArrayList<Criterion>();"); //$NON-NLS-1$
		answer.addMethod(method);

		List<String> criteriaLists = new ArrayList<String>();
		criteriaLists.add("criteria"); //$NON-NLS-1$

		for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
			if (stringHasValue(introspectedColumn.getTypeHandler())) {
				String name = addtypeHandledObjectsAndMethods(introspectedColumn, method, answer);
				criteriaLists.add(name);
			}
		}

		// now generate the isValid method
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("isValid"); //$NON-NLS-1$
		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		StringBuilder sb = new StringBuilder();
		Iterator<String> strIter = criteriaLists.iterator();
		sb.append("return "); //$NON-NLS-1$
		sb.append(strIter.next());
		sb.append(".size() > 0"); //$NON-NLS-1$
		if (!strIter.hasNext()) {
			sb.append(';');
		}
		method.addBodyLine(sb.toString());
		while (strIter.hasNext()) {
			sb.setLength(0);
			OutputUtilities.javaIndent(sb, 1);
			sb.append("|| "); //$NON-NLS-1$
			sb.append(strIter.next());
			sb.append(".size() > 0"); //$NON-NLS-1$
			if (!strIter.hasNext()) {
				sb.append(';');
			}
			method.addBodyLine(sb.toString());
		}
		answer.addMethod(method);

		// now generate the getAllCriteria method
		if (criteriaLists.size() > 1) {
			field = new Field();
			field.setName("allCriteria"); //$NON-NLS-1$
			field.setType(new FullyQualifiedJavaType("List<Criterion>")); //$NON-NLS-1$
			field.setVisibility(JavaVisibility.PROTECTED);
			answer.addField(field);
		}

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("getAllCriteria"); //$NON-NLS-1$
		method.setReturnType(new FullyQualifiedJavaType("List<Criterion>")); //$NON-NLS-1$
		if (criteriaLists.size() < 2) {
			method.addBodyLine("return criteria;"); //$NON-NLS-1$
		} else {
			method.addBodyLine("if (allCriteria == null) {"); //$NON-NLS-1$
			method.addBodyLine("allCriteria = new ArrayList<Criterion>();"); //$NON-NLS-1$

			strIter = criteriaLists.iterator();
			while (strIter.hasNext()) {
				method.addBodyLine(String.format("allCriteria.addAll(%s);", strIter.next())); //$NON-NLS-1$
			}

			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("return allCriteria;"); //$NON-NLS-1$
		}
		answer.addMethod(method);

		// now we need to generate the methods that will be used in the SqlMap
		// to generate the dynamic where clause
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewArrayListInstance());

		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		FullyQualifiedJavaType listOfCriterion = new FullyQualifiedJavaType("java.util.List<Criterion>"); //$NON-NLS-1$
		field.setType(listOfCriterion);
		field.setName("criteria"); //$NON-NLS-1$
		answer.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return criteria;"); //$NON-NLS-1$
		answer.addMethod(method);

		// now add the methods for simplifying the individual field set methods
		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("addCriterion"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addBodyLine("if (condition == null) {"); //$NON-NLS-1$
		method.addBodyLine("throw new RuntimeException(\"Value for condition cannot be null\");"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$
		method.addBodyLine("criteria.add(new Criterion(condition));"); //$NON-NLS-1$
		if (criteriaLists.size() > 1) {
			method.addBodyLine("allCriteria = null;"); //$NON-NLS-1$
		}
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("addCriterion"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
		method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
		method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$
		method.addBodyLine("criteria.add(new Criterion(condition, value));"); //$NON-NLS-1$
		if (criteriaLists.size() > 1) {
			method.addBodyLine("allCriteria = null;"); //$NON-NLS-1$
		}
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("addCriterion"); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value1")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value2")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
		method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
		method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$
		method.addBodyLine("criteria.add(new Criterion(condition, value1, value2));"); //$NON-NLS-1$
		if (criteriaLists.size() > 1) {
			method.addBodyLine("allCriteria = null;"); //$NON-NLS-1$
		}
		answer.addMethod(method);

		FullyQualifiedJavaType listOfDates = new FullyQualifiedJavaType("java.util.List<java.util.Date>"); //$NON-NLS-1$

		if (introspectedTable.hasJDBCDateColumns()) {
			topLevelClass.addImportedType(FullyQualifiedJavaType.getDateInstance());
			topLevelClass.addImportedType(FullyQualifiedJavaType.getNewIteratorInstance());
			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, new java.sql.Date(value.getTime()), property);"); //$NON-NLS-1$
			answer.addMethod(method);

			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(listOfDates, "values")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();"); //$NON-NLS-1$
			method.addBodyLine("Iterator<Date> iter = values.iterator();"); //$NON-NLS-1$
			method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
			method.addBodyLine("dateList.add(new java.sql.Date(iter.next().getTime()));"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, dateList, property);"); //$NON-NLS-1$
			answer.addMethod(method);

			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value1")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value2")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);"); //$NON-NLS-1$
			answer.addMethod(method);
		}

		if (introspectedTable.hasJDBCTimeColumns()) {
			topLevelClass.addImportedType(FullyQualifiedJavaType.getDateInstance());
			topLevelClass.addImportedType(FullyQualifiedJavaType.getNewIteratorInstance());
			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, new java.sql.Time(value.getTime()), property);"); //$NON-NLS-1$
			answer.addMethod(method);

			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(listOfDates, "values")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("List<java.sql.Time> timeList = new ArrayList<java.sql.Time>();"); //$NON-NLS-1$
			method.addBodyLine("Iterator<Date> iter = values.iterator();"); //$NON-NLS-1$
			method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
			method.addBodyLine("timeList.add(new java.sql.Time(iter.next().getTime()));"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, timeList, property);"); //$NON-NLS-1$
			answer.addMethod(method);

			method = new Method();
			method.setVisibility(JavaVisibility.PROTECTED);
			method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value1")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getDateInstance(), "value2")); //$NON-NLS-1$
			method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
			method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
			method.addBodyLine("addCriterion(condition, new java.sql.Time(value1.getTime()), new java.sql.Time(value2.getTime()), property);"); //$NON-NLS-1$
			answer.addMethod(method);
		}
	}

	/**
	 * This method adds all the extra methods and fields required to support a
	 * user defined type handler on some column.
	 * 
	 * @param introspectedColumn
	 * @param constructor
	 * @param innerClass
	 * @return the name of the List added to the class by this method
	 */
	private String addtypeHandledObjectsAndMethods(IntrospectedColumn introspectedColumn, Method constructor,
			InnerClass innerClass) {
		String answer;
		StringBuilder sb = new StringBuilder();

		// add new private field and public accessor in the class
		sb.setLength(0);
		sb.append(introspectedColumn.getJavaProperty());
		sb.append("Criteria"); //$NON-NLS-1$
		answer = sb.toString();

		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(new FullyQualifiedJavaType("java.util.List<Criterion>")); //$NON-NLS-1$
		field.setName(answer);
		innerClass.addField(field);

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		sb.insert(0, "return "); //$NON-NLS-1$
		sb.append(';');
		method.addBodyLine(sb.toString());
		innerClass.addMethod(method);

		// add constructor initialization
		sb.setLength(0);
		sb.append(field.getName());
		sb.append(" = new ArrayList<Criterion>();"); //$NON-NLS-1$;
		constructor.addBodyLine(sb.toString());

		// now add the methods for simplifying the individual field set methods
		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		sb.setLength(0);
		sb.append("add"); //$NON-NLS-1$
		sb.append(introspectedColumn.getJavaProperty());
		sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
		sb.append("Criterion"); //$NON-NLS-1$

		method.setName(sb.toString());
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
		method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
		method.addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
		method.addBodyLine("}"); //$NON-NLS-1$

		method.addBodyLine(String.format("%s.add(new Criterion(condition, value, \"%s\"));", //$NON-NLS-1$
				field.getName(), introspectedColumn.getTypeHandler()));
		method.addBodyLine("allCriteria = null;"); //$NON-NLS-1$
		innerClass.addMethod(method);

		sb.setLength(0);
		sb.append("add"); //$NON-NLS-1$
		sb.append(introspectedColumn.getJavaProperty());
		sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
		sb.append("Criterion"); //$NON-NLS-1$

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName(sb.toString());
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition")); //$NON-NLS-1$
		method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value1")); //$NON-NLS-1$
		method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value2")); //$NON-NLS-1$
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "property")); //$NON-NLS-1$
		if (!introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
			method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
			method.addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
			method.addBodyLine("}"); //$NON-NLS-1$
		}

		method.addBodyLine(String.format("%s.add(new Criterion(condition, value1, value2, \"%s\"));", //$NON-NLS-1$
				field.getName(), introspectedColumn.getTypeHandler()));

		method.addBodyLine("allCriteria = null;"); //$NON-NLS-1$
		innerClass.addMethod(method);

		return answer;
	}
}
