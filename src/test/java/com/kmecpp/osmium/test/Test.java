package com.kmecpp.osmium.test;

import com.kmecpp.osmium.api.database.DBColumn;
import com.kmecpp.osmium.api.database.mysql.MySQLTable;

public class Test {

	@MySQLTable(name = "product")
	public static class Product {

		@DBColumn(primary = true)
		private int category;

		@DBColumn(primary = true)
		private double price;

	}

	@MySQLTable(name = "customer")
	public static class Customer {

		@DBColumn(primary = true)
		private int id;

	}

	@MySQLTable(name = "product_order")
	public static class ProductOrder {

		public Product product;
		public Customer customer;

	}

}
