package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Character</code> (and char primitive) values.
 * </p>
 */

public class CharacterConverter extends ObjectConverter<Character> {

	@Override
	public Character convertToEntityAttribute(String dbData) {
		return dbData == null ? null : Character.valueOf(dbData.charAt(0));
	}
}
