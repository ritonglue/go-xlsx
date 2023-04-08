package io.github.ritonglue.goxlsx.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;

public class WBContext {
	private final Map<String, CellStyle> styles = new HashMap<>();
	private boolean isApplyFilter = true;
	private String headerStyle = null;

	public boolean isApplyFilter() {
		return isApplyFilter;
	}

	public void setApplyFilter(boolean isApplyFilter) {
		this.isApplyFilter = isApplyFilter;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public CellStyle putHeaderStyle(String headerStyle, CellStyle style) {
		this.headerStyle = headerStyle;
		return putStyle(headerStyle, style);
	}

	public CellStyle putStyle(String format, CellStyle style) {
		return styles.put(format, style);
	}

	public CellStyle getStyle(String format) {
		return styles.get(format);
	}
}
