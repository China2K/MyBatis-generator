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

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.foregin.AssociationRootTable;
import org.mybatis.generator.config.foregin.AssociationTable;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class CountByExampleElementGenerator extends AbstractXmlElementGenerator {

	public CountByExampleElementGenerator() {
		super();
	}

	@Override
	public void addElements(XmlElement parentElement) {

		AssociationRootTable associationRootTable = introspectedTable.getTableConfiguration().getAssociationRootTable();

		XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

		String fqjt = introspectedTable.getExampleType();

		answer.addAttribute(new Attribute("id", introspectedTable.getCountByExampleStatementId())); //$NON-NLS-1$
		answer.addAttribute(new Attribute("parameterType", fqjt)); //$NON-NLS-1$
		answer.addAttribute(new Attribute("resultType", "java.lang.Long")); //$NON-NLS-1$ //$NON-NLS-2$

		context.getCommentGenerator().addComment(answer);

		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ");

		String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
		if (associationRootTable.getChildren().size() > 0) {
			tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
			tableName = tableName + " as " + associationRootTable.getAlias();
			sb.append(tableName);
			answer.addElement(new TextElement(sb.toString()));
			generateJoinSql(associationRootTable, answer, associationRootTable.getAlias());
		} else {
			sb.append(tableName);
			answer.addElement(new TextElement(sb.toString()));
		}

		answer.addElement(getExampleIncludeElement());

		if (context.getPlugins().sqlMapCountByExampleElementGenerated(answer, introspectedTable)) {
			parentElement.addElement(answer);
		}
	}

	private void generateJoinSql(AssociationRootTable associationRootTable, XmlElement answer, String leftAlias) {
		StringBuilder sb = new StringBuilder();
		for (AssociationTable associationTable : associationRootTable.getChildren()) {
			sb.setLength(0);
			XmlElement ifElement = new XmlElement("if");
			ifElement.addAttribute(new Attribute("test", associationTable.getAssociationField().getName()));
			String tableName = associationTable.getIntrospectedTable().getFullyQualifiedTable()
					.getIntrospectedTableName();

			sb.append(" left join " + tableName + " as " + associationTable.getAlias());
			sb.append(" on " + leftAlias + "." + associationTable.getMappedColumn() + "=");
			sb.append(associationTable.getAlias() + "." + associationTable.getColumn());

			ifElement.addElement(new TextElement(sb.toString()));
			generateJoinSql(associationTable, ifElement, associationTable.getAlias());
			answer.addElement(ifElement);
		}
	}
}
