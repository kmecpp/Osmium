package com.kmecpp.osmium.api.database;

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

	//	/**
	//	 * This value indicates the order of the . This value MUST be unique in context with the other columns. Duplicates will not be caught at compile time. This value is used currently just for 
	//	 * 
	//	 * @return the columns ID
	//	 */
	//	int id() default -1;
	//
	//	@SuppressWarnings("rawtypes")
	//	Class<? extends Serializable> serializer() default Serializable.class;

	boolean notNull() default true;

	boolean autoIncrement() default false;

	boolean unique() default false;

	int maxLength() default -1;

}
