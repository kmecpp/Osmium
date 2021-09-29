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

}
