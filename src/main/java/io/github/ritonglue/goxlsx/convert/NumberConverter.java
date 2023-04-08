package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Number</code> values.
 * </p>
 */

public abstract class NumberConverter<X extends Number> implements AttributeConverter<X, Number> {

	@Override
	public Number convertToDatabaseColumn(X attribute) {
		return attribute;
	}
}
