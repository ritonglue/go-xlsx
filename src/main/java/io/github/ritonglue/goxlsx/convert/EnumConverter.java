package io.github.ritonglue.goxlsx.convert;

import java.util.Objects;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Enum</code> (and enum primitive) values.
 * </p>
 */
public class EnumConverter<T extends Enum<T>> implements AttributeConverter<T, String> {


	private Class<T> targetClass;

	/**
	 * Instantiates an enum converter with a class where enum constants are taken from.
	 *
	 * @param targetClass Class where the enum constants are taken from by the converter methods.
	 */
	public EnumConverter(Class<T> targetClass) {
		this.targetClass = Objects.requireNonNull(targetClass, "targetClass null");
	}

	@Override
	public String convertToDatabaseColumn(T attribute) {
		return attribute == null ? null : attribute.name();
	}

	@Override
	public T convertToEntityAttribute(String dbData) {
		if(dbData == null) return null;

		dbData = dbData.strip();
		if (dbData.isEmpty()) {
			return null;
		}

		try {
			return Enum.valueOf(targetClass, dbData);
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}
}
