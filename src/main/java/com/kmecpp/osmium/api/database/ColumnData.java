package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;

import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.util.Require;

public class ColumnData {

	private final String name;
	private final Field field;
	private final Class<?> type;
	private final boolean primary;
	private final boolean unique;
	private final boolean nullable;
	private final boolean autoIncrement;
	//	private final MDBTableData foreignKey;
	private final int maxLength;

	private String defaultValue;

	private static final DBColumn DEFAULT_META = DBUtil.createDefaultColumnAnnotation();
	//	private static final MDBColumn FOREIGN_KEY_META = MDBUtil.createForeignKeyMeta();

	public ColumnData(Field field) {
		this(field, field.getType(), Require.nonNull(field.getDeclaredAnnotation(DBColumn.class), DEFAULT_META)); //, MDBUtil.createForeignKey(field));
	}

	public ColumnData(Field field, Class<?> type, DBColumn meta) {
		//		MDBColumn meta = Require.nonNull(field.getDeclaredAnnotation(MDBColumn.class), DEFAULT_META);
		//		Class<?> type = field.getType();

		//		if (type.isAnnotationPresent(MySQLTable.class)) {
		//			this.type = int.class;
		//			this.foreignKey = true;
		//		} else {
		this.field = field;
		this.type = type;
		//		this.foreignKey = foreignKey;
		//		if (field != null) {
		this.name = SQLDatabase.getColumnName(field.getName());
		//		} else {
		//			this.name = manager.getData(type).getTableName() + "_id";
		//		}
		//		}

		this.primary = meta.primary();
		this.unique = meta.unique();
		this.nullable = meta.nullable();
		this.maxLength = meta.maxLength();
		this.autoIncrement = meta.autoIncrement();
		this.defaultValue = meta.defaultValue();
	}

	//	public static MDBColumnData createForeignKeyData(MDB manager, Class<?> target) {
	//		return new MDBColumnData(manager, null, target, FOREIGN_KEY_META, Require.nonNull(manager.getData(target)));
	//	}

	public String getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return type;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public boolean isPrimary() {
		return primary;
	}

	public boolean isUnique() {
		return unique;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	//	public MDBTableData getForeignKey() {
	//		return foreignKey;
	//	}
	//
	//	public boolean isForeignKey() {
	//		return foreignKey != null;
	//	}

}
