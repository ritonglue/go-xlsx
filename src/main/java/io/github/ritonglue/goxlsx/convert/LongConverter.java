package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Long</code> (and long primitive) values.
 * </p>
 */

public class LongConverter extends NumberConverter<Long> {

	@Override
	public Long convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : dbData.longValue();
	}
}
