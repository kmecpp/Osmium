package com.kmecpp.osmium.api.database.api;

import java.util.List;
import java.util.Optional;

import com.kmecpp.osmium.api.database.api.JoinClause.JoinType;
import com.kmecpp.osmium.api.util.Pagination;

public interface SQLInterfaces {

	public static interface SelectInterfaces {

		public static interface SIBase<T> extends SIWhere<T> {

			SIWhere<T> join(JoinClause join);

			default SIWhere<T> leftJoin(String table, String criteria) {
				return join(new JoinClause(JoinType.LEFT, table, criteria));
			}

			default SIWhere<T> rightJoin(String table, String criteria) {
				return join(new JoinClause(JoinType.RIGHT, table, criteria));
			}

			default SIWhere<T> innerJoin(String table, String criteria) {
				return join(new JoinClause(JoinType.INNER, table, criteria));
			}

			default SIWhere<T> crossJoin(String table) {
				return join(new JoinClause(JoinType.CROSS, table, null));
			}

		}

		public static interface SIWhere<T> extends SIGroupBy<T> {

			SIGroupBy<T> where(Filter filter);

		}

		public static interface SIGroupBy<T> extends SIOrderBy<T> {

			default SIOrderBy<T> groupBy(String column) {
				return groupBy(GroupBy.of(column));
			}

			SIOrderBy<T> groupBy(GroupBy groupBy);

		}

		public static interface SIOrderBy<T> extends SILimit<T> {

			default SILimit<T> orderByDesc(String column) {
				return orderBy(OrderBy.desc(column));
			}

			default SILimit<T> orderByAsc(String column) {
				return orderBy(OrderBy.asc(column));
			}

			SILimit<T> orderBy(OrderBy by);

		}

		public static interface SILimit<T> extends SITerminal<T> {

			default Optional<T> getFirst() {
				return limit(1).get();
			}

			default SITerminal<T> page(int page) {
				return page(page, 10);
			}

			default SITerminal<T> page(int page, int pageSize) {
				return limit(Pagination.getStartIndex(page, pageSize), pageSize);
			}

			default SITerminal<T> limit(int rowCount) {
				return limit(0, rowCount);
			}

			SITerminal<T> limit(int offset, int rowCount);

		}

		public static interface SITerminal<T> {

			List<T> execute();

			default Optional<T> get() {
				List<T> result = this.execute();
				if (result.size() > 1) {
					throw new RuntimeException("Query returned multiple rows: " + result.size());
				}
				return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
			}

			<R> R transform(ResultSetTransformer<R> resultHandler);

			default void process(ResultSetProcessor resultHandler) {
				transform(resultSet -> {
					resultHandler.process(resultSet);
					return null;
				});
			}

		}

	}

	/*
	 * REPLACE
	 */
	public interface ReplaceInterface {

	}

}
