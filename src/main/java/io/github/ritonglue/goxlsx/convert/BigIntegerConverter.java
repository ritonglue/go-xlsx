package io.github.ritonglue.goxlsx.convert;

import java.math.BigInteger;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.math.BigInteger</code> values.
 * </p>
 */

public class BigIntegerConverter extends NumberConverter<BigInteger> {

	@Override
	public BigInteger convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : BigInteger.valueOf(dbData.longValue());
	}
}
