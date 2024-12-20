package io.github.ritonglue.goxlsx.processor;

import io.github.ritonglue.goxlsx.convert.AttributeConverter;

public class Data {
	private AttributeConverter<?,?> converter;
	private String header;
	private String header2;
	private int order;
	private String format;
	private String headerStyle;
	private String header2Style;
	private int width;
	private boolean isHidden;

	Data() {}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public int getWidth() {
		return width;
	}

	public String getHeader2Style() {
		return header2Style;
	}

	public void setHeader2Style(String header2Style) {
		this.header2Style = header2Style;
	}

	public void setWidth(int width) {
		this.width = width;
	}

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
	public String getHeader2() {
		return header2;
	}

	public void setHeader2(String header2) {
		this.header2 = header2;
	}
}
