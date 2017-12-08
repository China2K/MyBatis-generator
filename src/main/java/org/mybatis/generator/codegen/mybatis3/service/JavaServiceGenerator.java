package org.mybatis.generator.codegen.mybatis3.service;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.BlankJavaMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

public class JavaServiceGenerator extends AbstractJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		String progressStr = getString("Progress.19", introspectedTable.getFullyQualifiedTable().toString());
		progressCallback.startTask(progressStr);
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getJavaServiceInterfaceType());
		Interface interfaze = new Interface(type);
		interfaze.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(interfaze);

		FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
		IntrospectedColumn key = introspectedTable.getPrimaryKeyColumns().get(0);
		FullyQualifiedJavaType dtoType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordDtoType());
		FullyQualifiedJavaType mapperType = null;
		if (introspectedTable.getMyBatis3JavaMapperType() != null) {
			mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
		}
		FullyQualifiedJavaType blankMapperType = null;
		if (introspectedTable.getMyBatis3BlankJavaMapperType() != null) {
			blankMapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3BlankJavaMapperType());
		}

		String rootInterface = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
		if (!stringHasValue(rootInterface)) {
			rootInterface = context.getJavaServiceGeneratorConfiguration().getProperty(
					PropertyRegistry.ANY_ROOT_INTERFACE);
		}

		if (stringHasValue(rootInterface)) {
			FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
			interfaze.addImportedType(fqjt);
			fqjt.addTypeArgument(modelType);
			interfaze.addImportedType(modelType);
			fqjt.addTypeArgument(dtoType);
			interfaze.addImportedType(dtoType);
			fqjt.addTypeArgument(exampleType);
			interfaze.addImportedType(exampleType);
			if (key != null) {
				fqjt.addTypeArgument(key.getFullyQualifiedJavaType());
				interfaze.addImportedType(key.getFullyQualifiedJavaType());
			}
			interfaze.addSuperInterface(fqjt);
		}

		FullyQualifiedJavaType impl = new FullyQualifiedJavaType(introspectedTable.getJavaServiceImplType());
		TopLevelClass topLevelClass = new TopLevelClass(impl);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);
		String rootClass = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_CLASS);
		if (!stringHasValue(rootClass)) {
			rootClass = context.getJavaServiceGeneratorConfiguration().getProperty(PropertyRegistry.ANY_ROOT_CLASS);
		}
		if (rootClass != null) {
			FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootClass);
			topLevelClass.addImportedType(fqjt);
			fqjt.addTypeArgument(modelType);
			topLevelClass.addImportedType(modelType);
			fqjt.addTypeArgument(dtoType);
			topLevelClass.addImportedType(dtoType);
			fqjt.addTypeArgument(exampleType);
			topLevelClass.addImportedType(exampleType);
			if (key != null) {
				fqjt.addTypeArgument(key.getFullyQualifiedJavaType());
				topLevelClass.addImportedType(key.getFullyQualifiedJavaType());
			}
			topLevelClass.setSuperClass(fqjt);
		}
		topLevelClass.addSuperInterface(type);
		topLevelClass.addImportedType(type);

		topLevelClass.addAnnotation("@Service(\"" + generateBeanName(type.getShortName().substring(1)) + "\")");
		topLevelClass.addImportedType("org.springframework.stereotype.Service");
		topLevelClass.addAnnotation("@Transactional(rollbackFor = Exception.class)");
		topLevelClass.addImportedType("org.springframework.transaction.annotation.Transactional");

		Field field = null;
		if (mapperType != null) {
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(mapperType);
			field.addAnnotation("@Resource");
			field.setName(lowerCaseFirstChar(mapperType.getShortName())); //$NON-NLS-1$
			commentGenerator.addFieldComment(field, introspectedTable);
			topLevelClass.addField(field);
			topLevelClass.addImportedType("javax.annotation.Resource");
			topLevelClass.addImportedType(mapperType);
		}
		if (blankMapperType != null) {
			field = new Field();
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(blankMapperType);
			field.addAnnotation("@Resource(name=\""
					+ BlankJavaMapperGenerator.generateBeanName(blankMapperType.getShortName()) + "\")");
			field.setName(lowerCaseFirstChar(blankMapperType.getShortName())); //$NON-NLS-1$
			commentGenerator.addFieldComment(field, introspectedTable);
			topLevelClass.addField(field);
			topLevelClass.addImportedType("javax.annotation.Resource");
			topLevelClass.addImportedType(blankMapperType);
		}

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		String sqlMapperInterface = introspectedTable.getContext().getSqlMapGeneratorConfiguration()
				.getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
		FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(sqlMapperInterface);
		returnType.addTypeArgument(modelType);
		returnType.addTypeArgument(dtoType);
		returnType.addTypeArgument(exampleType);
		returnType.addTypeArgument(key.getFullyQualifiedJavaType());
		method.setReturnType(returnType);
		method.addAnnotation("@Override");
		method.setName("getGeneratedSqlMapper");
		method.addBodyLine("return " + lowerCaseFirstChar(mapperType.getShortName()) + ";"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		topLevelClass.addImportedType(returnType);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(interfaze);
		answer.add(topLevelClass);
		return answer;
	}

	public static String generateBeanName(String shortName) {
		return "Generated" + shortName;
	}

}
