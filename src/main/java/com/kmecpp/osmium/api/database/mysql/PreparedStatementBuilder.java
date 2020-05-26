package com.kmecpp.osmium.api.database.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementBuilder {

	void build(PreparedStatement builder) throws SQLException;

}
