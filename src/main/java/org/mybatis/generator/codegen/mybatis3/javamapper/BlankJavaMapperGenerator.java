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
package org.mybatis.generator.codegen.mybatis3.javamapper;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.BlankXMLMapperGenerator;

/**
 * @author Jeff Butler
 * 
 */
public class BlankJavaMapperGenerator extends AbstractJavaClientGenerator {

	/**
     * 
     */
	public BlankJavaMapperGenerator() {
		super(true);
	}

	public BlankJavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
		super(requiresMatchedXMLGenerator);
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		String progressStr = getString("Progress.20", introspectedTable.getFullyQualifiedTable().toString());
		progressCallback.startTask(progressStr);
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3BlankJavaMapperType());
		Interface interfaze = new Interface(type);
		interfaze.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(interfaze);
		interfaze.addAnnotation("@Component(\"" + generateBeanName(type.getShortName()) + "\")");
		interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Component"));

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
			answer.add(interfaze);
		}

		return answer;
	}

	public static String generateBeanName(String shortName) {
		return "_" + shortName;
	}

	@Override
	public AbstractXmlGenerator getMatchedXMLGenerator() {
		return new BlankXMLMapperGenerator();
	}
}
