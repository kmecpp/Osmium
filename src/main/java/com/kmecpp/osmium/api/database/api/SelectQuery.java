package com.kmecpp.osmium.api.database.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kmecpp.osmium.api.database.SQLDatabase;
import com.kmecpp.osmium.api.database.TableData;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIBase;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIGroupBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SILimit;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIOrderBy;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SITerminal;
import com.kmecpp.osmium.api.database.api.SQLInterfaces.SelectInterfaces.SIWhere;
import com.kmecpp.osmium.api.logging.OsmiumLogger;
import com.kmecpp.osmium.api.util.IOUtil;

public class SelectQuery<T> implements SIBase<T> {

	private final SQLDatabase database;
	private final TableData tableData;

	private JoinClause join;
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
	public List<T> execute() {
		return transform(resultSet -> {
			try {
				ArrayList<T> result = new ArrayList<>();
				while (resultSet.next()) {
					result.add(this.database.parse(resultSet, tableData));
				}
				return result;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public <R> R transform(ResultSetTransformer<R> resultHandler) {
		String query = "SELECT * FROM " + this.tableData.getName()
				+ (join != null ? join : "")
				+ (filter != null ? filter.createParameterizedStatement() : "")
				+ (groupBy != null ? groupBy : "")
				+ (orderBy != null ? orderBy : "")
				+ (limit != null ? limit : "");

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			OsmiumLogger.debug("Executing query: \"" + query + "\"");
			connection = this.database.getConnection();
			statement = connection.prepareStatement(query);
			if (filter != null) {
				filter.link(statement);
			}
			resultSet = statement.executeQuery();
			return resultHandler.process(resultSet);
		} catch (Exception e) {
			OsmiumLogger.error("Failed to execute database query: \"" + query + "\"");
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(connection, statement, resultSet);
		}
	}

	@Override
	public SIWhere<T> join(JoinClause join) {
		this.join = join;
		return this;
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
