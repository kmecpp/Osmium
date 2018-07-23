package com.kmecpp.osmium.api.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.kmecpp.osmium.util.Reflection;

public class DBRow implements Iterable<DBValue> {

	private final List<String> columns;
	private final List<DBValue> data;

	public DBRow(List<String> columns, List<DBValue> data) {
		this.columns = columns;
		this.data = data;
	}

	public <T> T as(Class<T> cls) {
		//		ArrayList<Constructor<T>> constructors = Reflection.getConstructors(cls, size());
		//		if (constructors.size() != 1) {
		//			throw new IllegalStateException("Could not match database row with unqiue record constructor! Found " + constructors.size() + " possible options!");
		//		}
		//
		//		Constructor<T> constructor = constructors.get(0);
		//
		//		Class<?>[] paramTypes = constructor.getParameterTypes();
		//		Object[] params = new Object[size()];
		//		for (int i = 0; i < params.length; i++) {
		//			DBType.fromClass(paramTypes[i]);
		//			params[i] = data.get(i).as(paramTypes[i]);
		//		}
		//		try {
		//			return constructor.newInstance((Object[]) params);
		//		} catch (Exception e) {
		//			throw new RuntimeException(e);
		//		}

		try {
			T instance = cls.newInstance();
			Database.getTable(cls);
			Field[] fields = DBUtil.getFields(cls);
			if (data.size() != fields.length) {
				throw new RuntimeException("Could not cast DBRow to '" + cls.getName() + "'");
			}
			for (int i = 0; i < fields.length; i++) {
				fields[i].set(instance, data.get(i).as(fields[i].getType()));
			}
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//	public <T> T as(Class<T> cls) {
	//		//Calls the classes constructor using data as parameters
	//		Object[] object = new Object[data.size()];
	//
	//		return Reflection.newInstance(cls, (Object) ArrayUtil.convert(data.toArray(new DBValue[data.size()]), Object.class,
	//				new Converter<DBValue, Object>() {
	//
	//					@Override
	//					public Object convert(DBValue value) {
	//						return value.get();
	//					}
	//
	//				}));
	//	}
	//
	//	public void mergeByIndex(Object obj) {
	//		try {
	//			Field[] fields = Reflection.getAllFields(obj);
	//			for (int i = 0; i < fields.length; i++) {
	//				Field field = fields[i];
	//				field.set(obj, data.get(i).as(field.getType()));
	//			}
	//		} catch (IllegalArgumentException | IllegalAccessException e) {
	//			e.printStackTrace();
	//		}
	//	}

	/**
	 * Merges the given objects fields with the column values from this
	 * {@link DBRow}. This method only overwrites fields whose names match the
	 * column names, case insensitive.
	 * 
	 * @param obj
	 *            the object to merge with this row
	 * @return the modified object
	 */
	public <T> T merge(T obj) {
		try {
			for (Field field : Reflection.getFields(obj)) {
				if (Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				for (Entry<String, DBValue> entry : entrySet()) {
					if (entry.getKey().equalsIgnoreCase(field.getName())) {
						field.set(obj, entry.getValue());
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public boolean hasColumn(String name) {
		return columns.contains(name);
	}

	public int size() {
		return columns.size();
	}

	public boolean isEmpty() {
		return columns.isEmpty();
	}

	public DBValue get(int index) {
		return data.get(index);
	}

	public DBValue get(String column) {
		return data.get(columns.indexOf(column));
	}

	public Set<Entry<String, DBValue>> entrySet() {
		HashSet<Entry<String, DBValue>> entrySet = new HashSet<>();
		for (int i = 0; i < size(); i++) {
			entrySet.add(new AbstractMap.SimpleEntry<String, DBValue>(columns.get(i), data.get(i)));
		}
		return entrySet;
	}

	public String[] getColumns() {
		return columns.toArray(new String[columns.size()]);
	}

	public DBValue[] getData() {
		return data.toArray(new DBValue[data.size()]);
	}

	//	public Object getRawData() {
	//		return CollectionsUtil.convert(data, new Converter<DBValue, Object>() {
	//
	//			@Override
	//			public Object convert(DBValue value) {
	//				return value.get();
	//			}
	//
	//		});
	//	}

	@Override
	public Iterator<DBValue> iterator() {
		return data.iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			sb.append((i == 0 ? "" : ", ") + columns.get(i) + ": " + data.get(i));
		}
		return sb.toString();
	}

}