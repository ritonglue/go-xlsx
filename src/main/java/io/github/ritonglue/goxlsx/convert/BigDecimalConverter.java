package io.github.ritonglue.goxlsx.convert;

import java.math.BigDecimal;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.math.BigDecimal</code> values.
 * </p>
 */

public class BigDecimalConverter extends NumberConverter<BigDecimal> {

	@Override
	public BigDecimal convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : BigDecimal.valueOf(dbData.doubleValue());
	}
}
