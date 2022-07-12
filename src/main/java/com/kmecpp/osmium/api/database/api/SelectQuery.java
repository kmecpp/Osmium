package com.kmecpp.osmium.api.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.TableData;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIGroupBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIOrderBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SILimit;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SISelect;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SITerminal;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.IOUtil;

public class SelectQuery<T> implements SISelect<T> {

	private final SQLDatabase database;
	private final TableData tableData;

	private GroupBy groupBy;
	private OrderBy orderBy;
	private Filter filter;
	private LimitClause limit;

	public SelectQuery(SQLDatabase database, Class<T> tableClass) {
		this.database = database;
		this.tableData = database.getTable(tableClass);
		if (this.tableData == null) {
			throw new IllegalArgumentException("Missing table registration for " + tableClass.getName() + "! Is it annotated with @" + DBTable.class.getSimpleName() + "?");
		}
	}

	@Override
	public Optional<T> get() {
		List<T> result = this.execute();
		if (result.size() > 1) {
			throw new RuntimeException("Query returned multiple rows: " + result.size());
		}
		return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
	}

	@Override
	public List<T> execute() {
		String query = "SELECT * FROM " + this.tableData.getName()
				+ (filter != null ? filter.createParameterizedStatement() : "")
				+ (groupBy != null ? groupBy : "")
				+ (orderBy != null ? orderBy : "")
				+ (limit != null ? limit : "");

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			ArrayList<T> result = new ArrayList<>();

			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = this.database.getConnection();
			statement = connection.prepareStatement(query);
			if (filter != null) {
				filter.link(statement);
			}
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.add(this.database.parse(resultSet, tableData));
			}
			return result;
		} catch (Exception e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	@Override
	public SIGroupBy<T> where(Filter filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public SIOrderBy<T> groupBy(GroupBy groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	@Override
	public SILimit<T> orderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	@Override
	public SITerminal<T> limit(int offset, int rowCount) {
		this.limit = new LimitClause(offset, rowCount);
		return this;
	}

}
