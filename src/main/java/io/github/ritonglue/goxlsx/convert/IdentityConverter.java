package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for no conversion object
 * </p>
 */

public class IdentityConverter<X> implements AttributeConverter<X,X> {

	@Override
	public X convertToDatabaseColumn(X attribute) {
		return attribute;
	}

	@Override
	public X convertToEntityAttribute(X dbData) {
		return dbData;
	}
}
