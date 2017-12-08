package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;

public class BaseRecordDtoGenerator extends AbstractJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
		progressCallback.startTask(getString("Progress.23", table.toString())); //$NON-NLS-1$
		CommentGenerator commentGenerator = context.getCommentGenerator();

		if(introspectedTable.getBaseRecordDtoType()==null){
			return new ArrayList<CompilationUnit>();
		}
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordDtoType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);
		FullyQualifiedJavaType superClass = null;
		if (introspectedTable.getRules().generateBaseRecordClass()) {
			superClass = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		} else if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
			superClass = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
		}
		topLevelClass.setSuperClass(superClass);
		topLevelClass.addImportedType(superClass);
		commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, introspectedTable)) {
			answer.add(topLevelClass);
		}
		return answer;
	}

}
