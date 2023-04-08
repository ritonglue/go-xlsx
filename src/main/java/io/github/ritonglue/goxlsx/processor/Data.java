package io.github.ritonglue.goxlsx.processor;

import io.github.ritonglue.goxlsx.convert.AttributeConverter;

public class Data {
	private AttributeConverter<?,?> converter;
	private String header;
	private int order;
	private String format;
	private String headerStyle;
	private int width;

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	Data() {}

	public AttributeConverter<?,?> getConverter() {
		return converter;
	}
	public void setConverter(AttributeConverter<?,?> converter) {
		this.converter = converter;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
