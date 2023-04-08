package io.github.ritonglue.goxlsx.convert;

import java.util.Currency;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.util.Currency</code> values.
 * </p>
 */

public class CurrencyConverter implements AttributeConverter<Currency, String> {

	@Override
	public String convertToDatabaseColumn(Currency attribute) {
		return attribute == null ? null : attribute.getCurrencyCode();
	}

	@Override
	public Currency convertToEntityAttribute(String dbData) {
		return dbData == null ? null : Currency.getInstance(dbData);
	}
}
