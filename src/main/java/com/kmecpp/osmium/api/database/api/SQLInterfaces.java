package com.kmecpp.osmium.api.database.api;

import java.util.List;

import com.kmecpp.osmium.api.util.Pagination;

public interface SQLInterfaces {

	public static interface SelectInterfaces {

		public static interface SISelect<T> extends SIOrderBy<T> {

			SIOrderBy<T> where(Filter filter);

		}

		public static interface SIOrderBy<T> extends SILimit<T> {

			SILimit<T> orderBy(OrderBy by);

		}

		public static interface SILimit<T> extends SITerminal<T> {

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

			T get();

			List<T> execute();

		}

	}

	/*
	 * REPLACE
	 */
	public interface ReplaceInterface {

	}

}
