package com.kmecpp.osmium.api.database.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn {

	/**
	 * Whether or not this column should be primary. Primary columns should
	 * generally only be primitives or objects that serialize completely with
	 * their toString() method
	 * 
	 * @return whether or not this column is primary
	 */
	boolean primary() default false;

	boolean unique() default false;

	boolean nullable() default false;

	boolean autoIncrement() default false;

	int maxLength() default -1;

	//	String defaultValue() default "";

}
