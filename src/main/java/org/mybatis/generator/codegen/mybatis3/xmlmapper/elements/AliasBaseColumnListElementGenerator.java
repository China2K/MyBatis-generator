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
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.foregin.AssociationRootTable;
import org.mybatis.generator.config.foregin.AssociationTable;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class AliasBaseColumnListElementGenerator extends AbstractXmlElementGenerator {

	public AliasBaseColumnListElementGenerator() {
		super();
	}

	@Override
	public void addElements(XmlElement parentElement) {
		AssociationRootTable associationRootTable = introspectedTable.getTableConfiguration().getAssociationRootTable();

		XmlElement answer = new XmlElement("sql");
		answer.addAttribute(new Attribute("id", associationRootTable.getBaseColumnListId()));
		context.getCommentGenerator().addComment(answer);
		StringBuilder sb = new StringBuilder();
		Iterator<IntrospectedColumn> iter = introspectedTable.getNonBLOBColumns().iterator();
		while (iter.hasNext()) {
			IntrospectedColumn introspectedColumn = iter.next();
			String javaProperty = introspectedColumn.getJavaProperty();
			sb.append(associationRootTable.getAlias() + ".");
			sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(introspectedColumn));
			sb.append(" as '" + javaProperty + "'");
			if (iter.hasNext()) {
				sb.append(", ");
			}
			if (sb.length() > 80) {
				answer.addElement(new TextElement(sb.toString()));
				sb.setLength(0);
			}
		}
		if (sb.length() > 0) {
			answer.addElement(new TextElement(sb.toString()));
		}
		if (context.getPlugins().sqlMapBaseColumnListElementGenerated(answer, introspectedTable)) {
			parentElement.addElement(answer);
		}
		generateForeginTableBaseColumn(associationRootTable, parentElement);

	}

	private void generateForeginTableBaseColumn(AssociationRootTable associationRootTable, XmlElement parentElement) {
		StringBuilder sb = new StringBuilder();
		for (AssociationTable associationTable : associationRootTable.getChildren()) {
			XmlElement foreginColumns = new XmlElement("sql");
			foreginColumns.addAttribute(new Attribute("id", associationTable.getBaseColumnListId()));
			context.getCommentGenerator().addComment(foreginColumns);
			sb.setLength(0);
			Iterator<IntrospectedColumn> iter = associationTable.getIntrospectedTable().getNonBLOBColumns().iterator();
			while (iter.hasNext()) {
				IntrospectedColumn introspectedColumn = iter.next();
				String fieldName = introspectedColumn.getJavaProperty();
				sb.append(associationTable.getAlias() + ".");
				sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(introspectedColumn));
				sb.append(" as '" + associationTable.getLinkedMappedField() + "." + fieldName + "'");
				if (iter.hasNext()) {
					sb.append(", ");
				}
				if (sb.length() > 80) {
					foreginColumns.addElement(new TextElement(sb.toString()));
					sb.setLength(0);
				}
			}
			if (sb.length() > 0) {
				foreginColumns.addElement(new TextElement(sb.toString()));
			}
			parentElement.addElement(foreginColumns);
			generateForeginTableBaseColumn(associationTable, parentElement);
		}
	}
}
