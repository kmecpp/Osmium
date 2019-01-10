package com.kmecpp.osmium;

import java.sql.Types;
import java.util.UUID;

public class OsmiumSQLiteDialect extends org.hibernate.dialect.SQLiteDialect {

	public OsmiumSQLiteDialect() {
		//		registerColumnType(Types.VARCHAR, UUID.class.getName());
		registerColumnType(Types.BINARY, "varchar");
	}

}
