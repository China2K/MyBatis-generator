package org.mybatis.generator.config.foregin;

import java.util.ArrayList;
import java.util.List;

public class ForeginKey {

	private boolean useAssociation;

	private String mappedField;

	protected String column;

	protected String keyColumn;

	protected String table;

	private ForeginKey parent;

	private List<ForeginKey> children = new ArrayList<ForeginKey>();

	public ForeginKey getParent() {
		return parent;
	}

	public void setParent(ForeginKey parent) {
		this.parent = parent;
	}

	public List<ForeginKey> getChildren() {
		return children;
	}

	public void setChildren(List<ForeginKey> children) {
		this.children = children;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public boolean isUseAssociation() {
		return useAssociation;
	}

	public void setUseAssociation(boolean useAssociation) {
		this.useAssociation = useAssociation;
	}

	public String getMappedField() {
		return mappedField;
	}

	public void setMappedField(String mappedField) {
		this.mappedField = mappedField;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ForeginKey)) {
			return false;
		}
		ForeginKey fk = (ForeginKey) obj;
		boolean truth = true;
		if (!column.equals(fk.getColumn())) {
			truth = false;
		}
		if (!keyColumn.equals(fk.getKeyColumn())) {
			truth = false;
		}
		if (!table.equals(fk.getTable())) {
			truth = false;
		}
		if (mappedField != null) {
			if (!mappedField.equals(fk.getMappedField())) {
				truth = false;
			}
		} else {
			if (fk.getMappedField() != null) {
				truth = false;
			}
		}
		return truth;
	}
}
