package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Float</code> (and int primitive) values.
 * </p>
 */

public class FloatConverter extends NumberConverter<Float> {

	@Override
	public Float convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : dbData.floatValue();
	}
}
