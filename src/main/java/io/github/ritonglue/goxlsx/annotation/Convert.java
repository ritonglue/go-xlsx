package io.github.ritonglue.goxlsx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specifies the conversion of a field or property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Convert {

	/**
	 * Specifies the converter to be applied.  A value for this
	 * element must be specified if multiple converters would
	 * otherwise apply.
	 * @return the class converter
	 */
	Class converter() default void.class;

	String format() default "";
}
