package org.mybatis.generator.api.dom.java;

public class ReturnType {
	private FullyQualifiedJavaType type;
	private String annotation;

	public ReturnType(FullyQualifiedJavaType type, String annotation) {
		super();
		this.type = type;
		this.annotation = annotation;
	}

	/**
	 * @return Returns the type.
	 */
	public FullyQualifiedJavaType getType() {
		return type;
	}

	public String getFormattedContent(CompilationUnit compilationUnit) {
		StringBuilder sb = new StringBuilder();

		if (annotation != null) {
			sb.append(annotation);
			sb.append(' ');
		}

		sb.append(JavaDomUtils.calculateTypeName(compilationUnit, type));

		return sb.toString();
	}

	@Override
	public String toString() {
		return getFormattedContent(null);
	}
}
