package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Integer</code> (and int primitive) values.
 * </p>
 */

public class IntegerConverter extends NumberConverter<Integer> {

	@Override
	public Integer convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : dbData.intValue();
	}
}
