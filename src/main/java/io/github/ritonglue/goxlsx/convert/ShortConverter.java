package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Short</code> (and int primitive) values.
 * </p>
 */

public class ShortConverter extends NumberConverter<Short> {

	@Override
	public Short convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : dbData.shortValue();
	}
}
