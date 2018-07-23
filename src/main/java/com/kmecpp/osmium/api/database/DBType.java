package com.kmecpp.osmium.api.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Location;
import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.SimpleDate;
import com.kmecpp.osmium.api.World;
import com.kmecpp.osmium.util.Reflection;
import com.kmecpp.osmium.util.StringUtil;

public enum DBType {

	STRING,
	BOOLEAN,
	INTEGER,
	LONG,
	FLOAT,
	DOUBLE,
	UUID,

	DATE,
	//	LIST,
	LOCATION,
	//	INVENTORY,

	SERIALIZABLE,

	//	OBJECT,
	//	JAVA_SERIALIZABLE,
	;

	public Object get(ResultSet rs, int i) throws SQLException {
		String value = rs.getString(i);
		if (value == null) {
			return null;
		}
		switch (this) {
		case STRING:
			return value;
		case BOOLEAN:
			return value.equalsIgnoreCase("true") ? true : value.equalsIgnoreCase("false") ? false : null;
		case INTEGER:
			return rs.getInt(i);
		case LONG:
			return rs.getLong(i);
		case FLOAT:
			return rs.getFloat(i);
		case DOUBLE:
			return rs.getDouble(i);
		case UUID:
			return java.util.UUID.fromString(value);
		case DATE:
			return SimpleDate.fromString(value);
		case LOCATION:
			String[] parts = value.split(",");
			Optional<World> optionalWorld = Osmium.getWorld(parts[0]);
			if (optionalWorld.isPresent()) {
				return new Location(optionalWorld.get(), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
			} else {
				return null;
			}
		case SERIALIZABLE:
			//Uses custom serialization
			String type = rs.getMetaData().getColumnTypeName(i);
			return Database.deserialize(type, value);
		//			try {
		//				String key = type.substring(type.indexOf("_") + 1);
		//				return Database.deserialize(key, value);
		//				//				int index = value.indexOf("|");
		//				//				return SQLite.deserialize(Class.forName(value.substring(0, index)), value.substring(index + 1));
		//			} catch (Exception e) {
		//				OsmiumLogger.error("Corrupted SQLite table record! Could not parse " + type + " with value: '" + value + "'");
		//				throw new RuntimeException(e);
		//			}
		default:
			throw new RuntimeException();
		}
	}

	public static DBType fromName(String name) {
		if (StringUtil.startsWithIgnoreCase(name, SERIALIZABLE.name())) {
			return SERIALIZABLE;
		}
		for (DBType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return STRING;
		//		throw new IllegalArgumentException("SQLite database type: '" + name + "'" + " does not exist!");
	}

	public static DBType fromClass(Class<?> cls) {
		//@formatter:off
		return Reflection.isAssignable(cls, String.class) ? STRING
			: Reflection.isAssignable(cls, Boolean.class, boolean.class) ? BOOLEAN 	
			: Reflection.isAssignable(cls, Integer.class, int.class) ? INTEGER
			: Reflection.isAssignable(cls, Long.class, long.class) ? LONG
			: Reflection.isAssignable(cls, Float.class, float.class) ? FLOAT
			: Reflection.isAssignable(cls, Double.class, double.class) ? DOUBLE
			: Reflection.isAssignable(cls, UUID.class) ? UUID
			: Reflection.isAssignable(cls, SimpleDate.class) ? DATE
//			: Reflection.isAssignable(cls, DBList.class) ? LIST
			: Reflection.isAssignable(cls, Location.class) ? LOCATION
//			: Reflection.isAssignable(cls, Inventory.class) ? INVENTORY
			: Database.isSerializable(cls) ? SERIALIZABLE
			: STRING;
		//@formatter:on
	}

	public static String getTypeName(Class<?> cls) {
		DBType type = fromClass(cls);
		return type.toString() + (type == DBType.SERIALIZABLE ? "_" + Database.getTypeId(cls) : "");
	}

}
