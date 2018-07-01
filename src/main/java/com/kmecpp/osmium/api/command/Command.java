package com.kmecpp.osmium.api.command;

public @interface Command {

	String[] aliases();

	String description() default "";

	String permission() default "";

	String usage() default "";

	boolean admin() default false;

	boolean playersOnly() default false;

}
