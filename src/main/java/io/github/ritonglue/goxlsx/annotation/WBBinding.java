package io.github.ritonglue.goxlsx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate Methods and fields
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface WBBinding {

	/**
	 * Determines the header name of the column
	 * @return The value in the spreadsheet where of header name is this value.
	 */
	String header() default "";

	/**
	 * Determines the second header name of the column
	 * @return The value in the spreadsheet where of second header name is this value.
	 */
	String header2() default "";

	/**
	 * Determines the order of the column
	 * In Mode.NAMED, it determines the order of the column during writing.
	 * @return The value on the row of this position.
	 */
	int order() default 0;

	/**
	 * Determines the number of characters width of the column
	 * @return The number of characters of the column
	 */
	int width() default 0;

	/**
	 * Determines the header style of the column
	 * @return The header style of the column
	 */
	String style() default "";

    /**
     * Set the visibility state for a given column
     */
	boolean hidden() default false;
}
