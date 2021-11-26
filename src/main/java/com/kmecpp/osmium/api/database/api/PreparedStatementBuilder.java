package com.kmecpp.osmium.api.database.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementBuilder {

	void build(PreparedStatement builder) throws SQLException;

}
