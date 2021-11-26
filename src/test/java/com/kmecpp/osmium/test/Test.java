package com.kmecpp.osmium.test;

import com.kmecpp.osmium.api.database.api.DBColumn;
import com.kmecpp.osmium.api.database.api.DBTable;

public class Test {

	@DBTable(name = "product")
	public static class Product {

		@DBColumn(primary = true)
		private int category;

		@DBColumn(primary = true)
		private double price;

	}

	@DBTable(name = "customer")
	public static class Customer {

		@DBColumn(primary = true)
		private int id;

	}

	@DBTable(name = "product_order")
	public static class ProductOrder {

		public Product product;
		public Customer customer;

	}

}
