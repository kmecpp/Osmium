package com.kmecpp.osmium.api.database.api;

import java.util.List;
import java.util.Optional;

import com.kmecpp.osmium.api.util.Pagination;

public interface SQLInterfaces {

	public static interface SelectInterfaces {

		public static interface SISelect<T> extends SIGroupBy<T> {

			SIGroupBy<T> where(Filter filter);

		}

		public static interface SIGroupBy<T> extends SIOrderBy<T> {
			
			default SIOrderBy<T> groupByColumn(String column) {
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

			Optional<T> get();

			List<T> execute();

		}

	}

	/*
	 * REPLACE
	 */
	public interface ReplaceInterface {

	}

}
