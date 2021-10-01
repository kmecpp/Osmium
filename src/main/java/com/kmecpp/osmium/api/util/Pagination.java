package com.kmecpp.osmium.api.util;

public class Pagination {

	public static int getPageCount(int itemCount, int pageSize) {
		return ((itemCount - 1) / pageSize) + 1;
	}

	public static int getStartIndex(int page, int pageSize) {
		return pageSize * (page - 1);
	}

	/**
	 * @return the exclusive end index
	 */
	public static int getEndIndex(int page, int pageSize) {
		return getStartIndex(page, pageSize) + pageSize;
	}

	public static int getEndIndex(int page, int pageSize, int totalRecords) {
		return Math.min(getEndIndex(page, pageSize), totalRecords);
	}

	public static int getStartIndexReversed(int numItems, int page, int pageSize) {
		return (numItems - 1) - getStartIndex(page, pageSize);
	}

	public static PaginationResult compute(int page, int pageSize, int totalRecords) {
		return new PaginationResult(page, Pagination.getPageCount(totalRecords, pageSize),
				Pagination.getStartIndex(page, pageSize), Pagination.getEndIndex(page, pageSize, totalRecords));
	}

	public static class PaginationResult {

		private final int page;
		private final int pageCount;
		private final int startIndex;
		private final int endIndex;

		public PaginationResult(int page, int pageCount, int startIndex, int endIndex) {
			this.page = page;
			this.pageCount = pageCount;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public int getPage() {
			return page;
		}

		public int getPageCount() {
			return pageCount;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

	}

}
