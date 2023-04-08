package io.github.ritonglue.goxlsx.convert;

/**
 * <p>
 * {@link AttributeConverter} implementation for <code>java.lang.Byte</code> (and byte primitive) values.
 * </p>
 */

public class ByteConverter extends NumberConverter<Byte> {

	@Override
	public Byte convertToEntityAttribute(Number dbData) {
		return dbData == null ? null : Byte.valueOf(dbData.byteValue());
	}

}
