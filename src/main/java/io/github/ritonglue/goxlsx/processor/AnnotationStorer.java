package io.github.ritonglue.goxlsx.processor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import io.github.ritonglue.goxlsx.convert.AttributeConverter;

public class AnnotationStorer {
	private final AttributeConverter<?,?> converter;
	private final int order;
	private final String header;
	private final String header2;
	private final String format;
	private final Class<?> clazz;
	private final Field field;
	private final Method getter;
	private final Method setter;
	private final int width;
	private final String headerStyle;
	private final String header2Style;
	private final boolean isHidden;

	public AnnotationStorer(Field field, Data d) {
		this(null, field, d);
	}

	public AnnotationStorer(PropertyDescriptor pd, Data d) {
		this(pd, null, d);
	}

	private AnnotationStorer(PropertyDescriptor pd, Field field, Data d) {
		this.order = d.getOrder();
		this.header = d.getHeader();
		this.header2 = d.getHeader2();
		if(pd != null) {
			this.getter = pd.getReadMethod();
			this.setter = pd.getWriteMethod();
			this.clazz = getter.getReturnType();
			this.field = null;
		} else if(field != null) {
			this.getter = null;
			this.setter = null;
			this.clazz = field.getType();
			this.field = field;
		} else {
			throw new AssertionError();
		}
		this.converter = d.getConverter();
		Objects.requireNonNull(converter, "converter null: " + order);
		this.format = d.getFormat();
		this.headerStyle = d.getHeaderStyle();
		this.header2Style = d.getHeader2Style();
		this.width = d.getWidth();
		this.isHidden = d.isHidden();
	}

	public Field getField() {
		return field;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public AttributeConverter<?,?> getConverter() {
		return converter;
	}

	public int getOrder() {
		return order;
	}

	public String getHeader() {
		return header;
	}

	public String getHeader2() {
		return header2;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getFormat() {
		return format;
	}

	public int getWidth() {
		return width;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public String getHeader2Style() {
		return header2Style;
	}
}
