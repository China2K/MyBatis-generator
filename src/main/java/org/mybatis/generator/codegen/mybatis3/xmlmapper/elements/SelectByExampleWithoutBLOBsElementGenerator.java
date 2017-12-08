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

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

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
public class SelectByExampleWithoutBLOBsElementGenerator extends AbstractXmlElementGenerator {

	public SelectByExampleWithoutBLOBsElementGenerator() {
		super();
	}

	@Override
	public void addElements(XmlElement parentElement) {
		AssociationRootTable associationRootTable = introspectedTable.getTableConfiguration().getAssociationRootTable();

		String fqjt = introspectedTable.getExampleType();

		XmlElement answer = new XmlElement("select"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", //$NON-NLS-1$
				introspectedTable.getSelectByExampleStatementId()));
		if (associationRootTable.getChildren().size() > 0) {
			answer.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordDtoType()));
		} else {
			answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId())); //$NON-NLS-1$
		}
		answer.addAttribute(new Attribute("parameterType", fqjt)); //$NON-NLS-1$

		context.getCommentGenerator().addComment(answer);

		answer.addElement(new TextElement("select")); //$NON-NLS-1$
		XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
		ifElement.addAttribute(new Attribute("test", "distinct")); //$NON-NLS-1$ //$NON-NLS-2$
		ifElement.addElement(new TextElement("distinct")); //$NON-NLS-1$
		answer.addElement(ifElement);

		StringBuilder sb = new StringBuilder();
		if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
			sb.append('\'');
			sb.append(introspectedTable.getSelectByExampleQueryId());
			sb.append("' as QUERYID,"); //$NON-NLS-1$
			answer.addElement(new TextElement(sb.toString()));
		}
		if (associationRootTable.getChildren().size() > 0) {
			XmlElement baseColumnList = new XmlElement("include");
			baseColumnList.addAttribute(new Attribute("refid", associationRootTable.getBaseColumnListId()));
			answer.addElement(baseColumnList);
			generateIncludeColumnSql(associationRootTable, answer);
		} else {
			answer.addElement(getBaseColumnListElement());
		}
		sb.setLength(0);
		sb.append("from ");
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

		ifElement = new XmlElement("if"); //$NON-NLS-1$
		ifElement.addAttribute(new Attribute("test", "orderByClause != null")); //$NON-NLS-1$ //$NON-NLS-2$
		ifElement.addElement(new TextElement("order by ${orderByClause}")); //$NON-NLS-1$
		answer.addElement(ifElement);

		if (context.getPlugins().sqlMapSelectByExampleWithoutBLOBsElementGenerated(answer, introspectedTable)) {
			parentElement.addElement(answer);
		}
	}

	private void generateIncludeColumnSql(AssociationRootTable associationRootTable, XmlElement answer) {
		for (AssociationTable associationTable : associationRootTable.getChildren()) {
			XmlElement ifElement = new XmlElement("if");
			ifElement.addAttribute(new Attribute("test", associationTable.getAssociationField().getName())); //$NON-NLS-1$ //$NON-NLS-2$
			XmlElement columnRef = new XmlElement("include");
			columnRef.addAttribute(new Attribute("refid", associationTable.getBaseColumnListId()));
			ifElement.addElement(new TextElement(","));
			ifElement.addElement(columnRef);
			generateIncludeColumnSql(associationTable, ifElement);
			answer.addElement(ifElement);
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
