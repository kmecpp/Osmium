package com.kmecpp.osmium.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {

	Order order() default Order.DEFAULT;

	Class<? extends Event>[] include() default {};

	Class<? extends Event>[] exclude() default {};

}
