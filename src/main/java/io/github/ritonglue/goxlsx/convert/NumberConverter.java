package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Number</code> values.
 * </p>
 */

public class NumberConverter<X extends Number> implements AttributeConverter<X, Number> {

	@Override
	public Number convertToDatabaseColumn(X attribute) {
		return attribute;
	}

	@Override
	public X convertToEntityAttribute(Number dbData) {
		throw new UnsupportedOperationException();
	}
}
