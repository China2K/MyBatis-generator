package org.mybatis.generator.codegen.mybatis3.controller;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.ReturnType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.service.JavaServiceGenerator;
import org.mybatis.generator.config.PropertyRegistry;

public class JavaControllerGenerator extends AbstractJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		String progressStr = getString("Progress.22", introspectedTable.getFullyQualifiedTable().toString());
		progressCallback.startTask(progressStr);
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getJavaControllerType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		FullyQualifiedJavaType modelDtoType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordDtoType());
		FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
		FullyQualifiedJavaType serviceInterfaceType = new FullyQualifiedJavaType(
				introspectedTable.getJavaServiceInterfaceType());

		String rootClass = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_CLASS);
		if (!stringHasValue(rootClass)) {
			rootClass = context.getJavaControllerGeneratorConfiguration().getProperty(PropertyRegistry.ANY_ROOT_CLASS);
		}
		if (rootClass != null) {
			FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootClass);
			topLevelClass.addImportedType(fqjt);
			fqjt.addTypeArgument(modelType);
			topLevelClass.addImportedType(modelType);
			fqjt.addTypeArgument(modelDtoType);
			topLevelClass.addImportedType(modelDtoType);
			fqjt.addTypeArgument(exampleType);
			topLevelClass.addImportedType(exampleType);
			topLevelClass.setSuperClass(fqjt);
		}

		topLevelClass.addAnnotation("@Controller(\"" + generateBeanName(type.getShortName()) + "\")");
		topLevelClass.addImportedType("org.springframework.stereotype.Controller");
		topLevelClass.addAnnotation("@RequestMapping(\"/" + lowerCaseFirstChar(modelType.getShortName()) + "\")");
		topLevelClass.addImportedType("org.springframework.web.bind.annotation.RequestMapping");

		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(serviceInterfaceType);
		field.addAnnotation("@Resource(name=\""
				+ JavaServiceGenerator.generateBeanName(serviceInterfaceType.getShortName().substring(1)) + "\")");
		String serviceFieldName = lowerCaseFirstChar(serviceInterfaceType.getShortName().substring(1));
		field.setName(serviceFieldName); //$NON-NLS-1$
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		topLevelClass.addImportedType("javax.annotation.Resource");
		topLevelClass.addImportedType(serviceInterfaceType);

		addImported(topLevelClass);
		addListMethod(serviceFieldName, modelDtoType, exampleType, topLevelClass);
		addCountMethod(serviceFieldName, exampleType, topLevelClass);
		addFindMethod(serviceFieldName, modelDtoType, exampleType, topLevelClass);
		addGetMethod(serviceFieldName, modelDtoType, topLevelClass);
		addSaveMethod(serviceFieldName, modelType, topLevelClass);
		addUpdateMethod(serviceFieldName, modelType, topLevelClass);
		addDeleteByIdMethod(serviceFieldName, topLevelClass);
		addDeleteByPropMethod(serviceFieldName, exampleType, topLevelClass);
		addLoadMethod(serviceFieldName, modelDtoType, modelType, exampleType, topLevelClass);

		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(serviceInterfaceType);
		method.addAnnotation("@Override");
		method.setName("getBaseService");
		method.addBodyLine("return " + serviceFieldName + ";"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(topLevelClass);
		return answer;
	}

	public static String generateBeanName(String shortName) {
		return "Generated" + shortName;
	}

	private void addImported(TopLevelClass topLevelClass) {
		FullyQualifiedJavaType type = new FullyQualifiedJavaType("net.sf.json.JSONObject");
		topLevelClass.addImportedType(type);
		type = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.ResponseBody");
		topLevelClass.addImportedType(type);
		type = new FullyQualifiedJavaType("org.springframework.data.domain.Page");
		topLevelClass.addImportedType(type);
		type = new FullyQualifiedJavaType(context.getJavaControllerGeneratorConfiguration().getProperty(
				PropertyRegistry.SEARCH_BEAN_CLASS));
		topLevelClass.addImportedType(type);
		type = new FullyQualifiedJavaType(context.getJavaControllerGeneratorConfiguration().getProperty(
				PropertyRegistry.BASIC_EXCEPTION_CLASS));
		topLevelClass.addImportedType(type);
		type = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestParam");
		topLevelClass.addImportedType(type);
		type = FullyQualifiedJavaType.getNewListInstance();
		topLevelClass.addImportedType(type);
		topLevelClass.addImportedType("org.springframework.ui.Model");
	}

	private void addCountMethod(String serviceName, FullyQualifiedJavaType exampleType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/count.do\")");
		method.setName("count");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.String"), "searchParam");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine(exampleType.getShortName() + " example = convert2OrExample(searchParam);");
		method.addBodyLine("return toJSONResult(true, " + serviceName + ".countByExample(example));");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		topLevelClass.addMethod(method);
	}

	private void addListMethod(String serviceName, FullyQualifiedJavaType modelType,
			FullyQualifiedJavaType exampleType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/list.do\")");
		method.setName("list");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "offset");
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "limit");
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "sEcho");
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("java.lang.String"), "searchParam");
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("java.lang.String"), "orderCause");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine("Page<" + modelType.getShortName() + "> pageResult = null;");
		method.addBodyLine("List<SearchBean[]> searchBeans = convert2OrSearchBean(searchParam);");
		method.addBodyLine(exampleType.getShortName() + " example = convertSearchBean2OrExample(searchBeans);");
		method.addBodyLine("if (limit != null) {");
		method.addBodyLine("if (orderCause != null) {");
		method.addBodyLine("pageResult = " + serviceName + ".pageFind(orderCause, offset, limit, example);");
		method.addBodyLine("} else {");
		method.addBodyLine("pageResult = " + serviceName + ".pageFind(offset, limit, example);");
		method.addBodyLine("}");
		method.addBodyLine("} else {");
		method.addBodyLine("pageResult = " + serviceName + ".pageFind(example);");
		method.addBodyLine("}");
		method.addBodyLine("if (sEcho != null) {");
		method.addBodyLine("return toJSONResult(pageResult.getTotalElements(), pageResult.getContent(), sEcho);");
		method.addBodyLine("}");
		method.addBodyLine("return toJSONResult(pageResult.getTotalElements(), pageResult.getContent());");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		topLevelClass.addMethod(method);
	}

	private void addFindMethod(String serviceName, FullyQualifiedJavaType modelType,
			FullyQualifiedJavaType exampleType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/find.do\")");
		method.setName("find");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.String"), "searchParam");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine("List<" + modelType.getShortName() + "> result = null;");
		method.addBodyLine("List<SearchBean[]> searchBeans = convert2OrSearchBean(searchParam);");
		method.addBodyLine(exampleType.getShortName() + " example = convertSearchBean2OrExample(searchBeans);");
		method.addBodyLine("result = " + serviceName + ".find(example);");
		method.addBodyLine("return toJSONResult(true,result);");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		topLevelClass.addMethod(method);
	}

	private void addGetMethod(String serviceName, FullyQualifiedJavaType modelType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/get.do\")");
		method.setName("get");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "id");
		param.addAnnotation("@RequestParam");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine(modelType.getShortName() + " " + lowerCaseFirstChar(modelType.getShortName()) + " = "
				+ serviceName + ".selectByPrimaryKey(id);");
		method.addBodyLine("return toJSONResult(true, " + lowerCaseFirstChar(modelType.getShortName()) + ");");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		topLevelClass.addMethod(method);
	}

	private void addLoadMethod(String serviceName, FullyQualifiedJavaType modelType,
			FullyQualifiedJavaType modelTypeSource, FullyQualifiedJavaType exampleType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("String"), "");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/load.do\")");
		method.setName("load");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "id");
		param.addAnnotation("@RequestParam");
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("Model"), "model");
		method.addParameter(param);

		method.addBodyLine("try {");
		method.addBodyLine("String searchParam = \"id:\" + id + \":=\";");
		method.addBodyLine(exampleType.getShortName() + " example = convert2OrExample(searchParam);");
		method.addBodyLine("example.enableAllAssociation();");
		method.addBodyLine(modelType.getShortName() + " " + lowerCaseFirstChar(modelType.getShortName()) + " = "
				+ serviceName + ".findUnique(example);");
		method.addBodyLine("model.addAttribute(\"dto\"," + lowerCaseFirstChar(modelType.getShortName()) + ");");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("}");
		method.addBodyLine("return \"" + lowerCaseFirstChar(modelTypeSource.getShortName()) + "Detail\";");
		topLevelClass.addMethod(method);
	}

	private void addSaveMethod(String serviceName, FullyQualifiedJavaType modelType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/save.do\")");
		method.setName("save");
		String paramName = lowerCaseFirstChar(modelType.getShortName());
		Parameter param = new Parameter(modelType, paramName);
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine(serviceName + ".insert(" + paramName + ");");
		method.addBodyLine("return toJSONResult(true);");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		topLevelClass.addMethod(method);
	}

	private void addUpdateMethod(String serviceName, FullyQualifiedJavaType modelType, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/update.do\")");
		method.setName("update");
		String paramName = lowerCaseFirstChar(modelType.getShortName());
		Parameter param = new Parameter(modelType, paramName);
		method.addParameter(param);
		param = new Parameter(new FullyQualifiedJavaType("java.lang.boolean"), "selective");
		param.addAnnotation("@RequestParam(defaultValue = \"true\", required = false)");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine("if (selective) {");
		method.addBodyLine(serviceName + ".updateByPrimaryKeySelective(" + paramName + ");");
		method.addBodyLine("} else {");
		method.addBodyLine(serviceName + ".updateByPrimaryKey(" + paramName + ");");
		method.addBodyLine("}");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		method.addBodyLine("return toJSONResult(true);");
		topLevelClass.addMethod(method);
	}

	private void addDeleteByIdMethod(String serviceName, TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/deleteById.do\")");
		method.setName("deleteById");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "id");
		param.addAnnotation("@RequestParam");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine(serviceName + ".deleteByPrimaryKey(id);");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		method.addBodyLine("return toJSONResult(true);");
		topLevelClass.addMethod(method);
	}

	private void addDeleteByPropMethod(String serviceName, FullyQualifiedJavaType exampleType,
			TopLevelClass topLevelClass) {
		ReturnType returnType = new ReturnType(new FullyQualifiedJavaType("net.sf.json.JSONObject"), "@ResponseBody");
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnTypeWithAnnotation(returnType);
		method.addAnnotation("@RequestMapping(\"/deleteByProp.do\")");
		method.setName("deleteByProp");
		Parameter param = new Parameter(new FullyQualifiedJavaType("java.lang.String"), "searchParam");
		param.addAnnotation("@RequestParam");
		method.addParameter(param);
		method.addBodyLine("try {");
		method.addBodyLine(exampleType.getShortName() + " example = convert2OrExample(searchParam);");
		method.addBodyLine(serviceName + ".deleteByExample(example);");
		method.addBodyLine("} catch (BasicException e) {");
		method.addBodyLine("e.printStackTrace();");
		method.addBodyLine("return toJSONResult(false, e.getMessage());");
		method.addBodyLine("}");
		method.addBodyLine("return toJSONResult(true);");
		topLevelClass.addMethod(method);
	}

}
