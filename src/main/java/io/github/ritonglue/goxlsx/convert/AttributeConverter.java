package io.github.ritonglue.goxlsx.convert;

/**
 * A class that implements this interface can be used to convert
 * bean attribute state into spreadsheet column representation
 * and back again.
 * Note that the X and Y types may be the same Java type.
 *
 * @param <X>  the type of the bean attribute
 * @param <Y>  the type of the spreadsheet column
 */
public interface AttributeConverter<X,Y> {

	/**
	 * Converts the value stored in the bean attribute into the
	 * data representation to be stored in the spreadsheet.
	 *
	 * @param attribute  the bean attribute value to be converted
	 * @return  the converted data to be stored in the spreadsheet column
	 */

	public Y convertToDatabaseColumn (X attribute);

	/**
	 * Converts the data stored in the spreadsheet column into the
	 * value to be stored in the bean attribute.
	 *
	 * @param dbData  the data from the spreadsheet column to be converted
	 * @return  the converted value to be stored in the bean attribute
	 */
	public X convertToEntityAttribute (Y dbData);
}