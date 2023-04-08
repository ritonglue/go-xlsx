package io.github.ritonglue.goxlsx.convert;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Short</code> (and int primitive) values.
 * </p>
 */

public class LocalDateConverter implements AttributeConverter<LocalDate, LocalDateTime> {

	@Override
	public LocalDate convertToEntityAttribute(LocalDateTime dbData) {
		return dbData == null ? null : dbData.toLocalDate();
	}

	@Override
	public LocalDateTime convertToDatabaseColumn(LocalDate attribute) {
		return attribute == null ? null : attribute.atStartOfDay();
	}

}
