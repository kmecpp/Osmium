package com.kmecpp.osmium.api.database.sqlite;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetProcessor {

	Object process(ResultSet rs);

}
