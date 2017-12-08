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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import java.util.Iterator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class BatchUpdateByPrimaryKeyWithBLOBsElementGenerator extends AbstractXmlElementGenerator {

	public BatchUpdateByPrimaryKeyWithBLOBsElementGenerator() {
		super();
	}

	@Override
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("update");

		String updateByPrimaryKeyWithBLOBsStatementId = introspectedTable.getUpdateByPrimaryKeyWithBLOBsStatementId();
		answer.addAttribute(new Attribute("id", "batch" + upperCaseFirstChar(updateByPrimaryKeyWithBLOBsStatementId)));
		answer.addAttribute(new Attribute("parameterType", "map"));

		context.getCommentGenerator().addComment(answer);

		StringBuilder sb = new StringBuilder();

		sb.append("update ");
		sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
		answer.addElement(new TextElement(sb.toString()));

		// set up for first column
		sb.setLength(0);
		sb.append("set ");

		Iterator<IntrospectedColumn> iter = ListUtilities.removeGeneratedAlwaysColumns(
				introspectedTable.getNonPrimaryKeyColumns()).iterator();
		while (iter.hasNext()) {
			IntrospectedColumn introspectedColumn = iter.next();

			sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
			sb.append(" = case ");
			answer.addElement(new TextElement(sb.toString()));

			sb.setLength(0);
			XmlElement foreach = new XmlElement("foreach");
			foreach.addAttribute(new Attribute("collection", "records"));
			foreach.addAttribute(new Attribute("item", "record"));
			sb.append("when ");
			boolean and = false;
			for (IntrospectedColumn primaryKeyColumn : introspectedTable.getPrimaryKeyColumns()) {
				if (and) {
					sb.setLength(0);
					sb.append("  and ");
				} else {
					and = true;
				}

				sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(primaryKeyColumn));
				sb.append(" = ");
				sb.append(MyBatis3FormattingUtilities.getParameterClause(primaryKeyColumn, "record."));
				foreach.addElement(new TextElement(sb.toString()));
			}
			sb.setLength(0);
			sb.append(" then ");
			sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
			foreach.addElement(new TextElement(sb.toString()));
			answer.addElement(foreach);

			sb.setLength(0);
			sb.append(" end ");
			if (iter.hasNext()) {
				sb.append(',');
			}

			answer.addElement(new TextElement(sb.toString()));

			if (iter.hasNext()) {
				sb.setLength(0);
				OutputUtilities.xmlIndent(sb, 1);
			}
		}

		boolean and = false;
		for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
			sb.setLength(0);
			if (and) {
				sb.append("  and ");
			} else {
				sb.append("where ");
				and = true;
			}

			sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
			sb.append(" in ");
			answer.addElement(new TextElement(sb.toString()));
			sb.setLength(0);

			XmlElement trim = new XmlElement("trim");
			trim.addAttribute(new Attribute("prefix", "("));
			trim.addAttribute(new Attribute("suffix", ")"));
			XmlElement foreach = new XmlElement("foreach");
			foreach.addAttribute(new Attribute("collection", "records"));
			foreach.addAttribute(new Attribute("item", "record"));
			foreach.addAttribute(new Attribute("separator", ","));
			sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
			foreach.addElement(new TextElement(sb.toString()));
			trim.addElement(foreach);

			answer.addElement(trim);
		}

		if (context.getPlugins().sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(answer, introspectedTable)) {
			parentElement.addElement(answer);
		}
	}
}
