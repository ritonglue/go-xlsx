package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Object</code> values.
 * </p>
 */

public abstract class ObjectConverter<T> implements AttributeConverter<T, String> {

	@Override
	public String convertToDatabaseColumn(T attribute) {
		return attribute == null ? null : attribute.toString();
	}
}
