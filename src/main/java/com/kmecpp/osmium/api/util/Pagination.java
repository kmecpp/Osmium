package com.kmecpp.osmium.api.util;

public class Pagination {

	public static int getPageCount(int itemCount, int pageSize) {
		return ((itemCount - 1) / pageSize) + 1;
	}

	public static int getStartIndex(int page, int pageSize) {
		return pageSize * (page - 1);
	}

	public static int getStartIndexReversed(int numItems, int page, int pageSize) {
		return (numItems - 1) - getStartIndex(page, pageSize);
	}

}
