package com.kmecpp.osmium.api.database.api;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetProcessor {

	void process(ResultSet rs) throws SQLException;

}
