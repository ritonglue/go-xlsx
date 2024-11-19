package io.github.ritonglue.goxlsx.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;

public class WBContext {
	private final Map<String, CellStyle> styles = new HashMap<>();
	private boolean isApplyFilter = true;
	private String headerStyle = null;
	private String header2Style = null;

	public boolean isApplyFilter() {
		return isApplyFilter;
	}

	public void setApplyFilter(boolean isApplyFilter) {
		this.isApplyFilter = isApplyFilter;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public String getHeader2Style() {
		return header2Style;
	}

	public CellStyle putHeaderStyle(String headerStyle, CellStyle style) {
		this.headerStyle = headerStyle;
		return putStyle(headerStyle, style);
	}

	public CellStyle putHeader2Style(String header2Style, CellStyle style) {
		this.header2Style = header2Style;
		return putStyle(header2Style, style);
	}

	public CellStyle putStyle(String format, CellStyle style) {
		return format == null ? null : styles.put(format, style);
	}

	public CellStyle getStyle(String format) {
		return styles.get(format);
	}
}
