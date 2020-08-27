package com.kmecpp.osmium.api.database.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.database.DBTable;
import com.kmecpp.osmium.api.persistence.Serialization;
import com.kmecpp.osmium.api.util.Reflection;

public class TableProperties {

	private Class<?> tableClass;
	private String name;

	private String[] columns;
	private String[] primaryColumns;
	private String[] primaryColumnsWithMaxLengths; //For MySQL

	private Field[] fields;
	private Field[] primaryFields;

	public TableProperties(Database db, Class<?> cls) {
		this.tableClass = cls;

		DBTable annotation = cls.getAnnotation(DBTable.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Database table '" + cls.getName() + "' must be annotated with @" + DBTable.class.getSimpleName());
		}
		this.name = Osmium.getPlugin(cls).getId() + "_" + annotation.name();

		//		for (Field field : Reflection.getAllFieldsWith(cls, DBColumn.class)) {
		//			field.getName();
		//		}

		ArrayList<Field> fields = new ArrayList<>();
		ArrayList<Field> primaryFields = new ArrayList<>();
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> primaryColumns = new ArrayList<>();
		ArrayList<String> primaryColumnsWithMaxLengths = new ArrayList<>();

		for (Field field : Reflection.getAllFieldsWith(cls, DBColumn.class)) {
			//			System.out.println("LOOPING THROUGH FIELD: " + field);
			field.setAccessible(true);
			DBColumn columnAnnotation = field.getAnnotation(DBColumn.class);
			if (columnAnnotation != null && !Modifier.isStatic(field.getModifiers())) {
				String name = DBUtil.getColumnName(field);

				//				if (CoreOsmiumConfig.Database.enableMysql && columnAnnotation.primary()) {
				//					name += "(" + db.getSerializationData(field.getType()).getType().getMaxLength() + ")";
				//				}

				fields.add(field);
				columns.add(name);

				if (columnAnnotation.primary()) {
					primaryFields.add(field);
					primaryColumns.add(name);
					primaryColumnsWithMaxLengths.add(name + "(" + (columnAnnotation.maxLength() > 0
							? columnAnnotation.maxLength() : Serialization.getData(field.getType()).getType().getMaxLength())
							+ ")");
				}
			}
		}
		this.fields = fields.toArray(new Field[fields.size()]);
		this.primaryFields = primaryFields.toArray(new Field[primaryFields.size()]);
		this.columns = columns.toArray(new String[columns.size()]);
		this.primaryColumns = primaryColumns.toArray(new String[primaryColumns.size()]);
		this.primaryColumnsWithMaxLengths = primaryColumnsWithMaxLengths.toArray(new String[primaryColumnsWithMaxLengths.size()]);
	}

	public String getName() {
		return name;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public String[] getColumns() {
		return columns;
	}

	public String[] getPrimaryColumns() {
		return primaryColumns;
	}

	public String[] getPrimaryColumnsWithLengths() {
		return primaryColumnsWithMaxLengths;
	}

	public Field[] getFields() {
		return fields;
	}

	public Field[] getPrimaryFields() {
		return primaryFields;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getTableClass() {
		return (Class<T>) tableClass;
	}

}
