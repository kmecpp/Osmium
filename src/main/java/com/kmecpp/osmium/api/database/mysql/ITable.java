package com.kmecpp.osmium.api.database.mysql;

public interface ITable {

	default void save() {
		//		MDBManager.save(this);
	}

	default void saveAsync() {
		//		MDBManager.saveAsync(this);
	}

}
