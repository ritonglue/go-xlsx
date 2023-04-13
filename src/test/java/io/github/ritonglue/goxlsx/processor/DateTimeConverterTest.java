package io.github.ritonglue.goxlsx.processor;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import io.github.ritonglue.goxlsx.annotation.Convert;

public class DateTimeConverterTest {

	@Test
	public void testLocalDate() throws IOException {
		LocalDate date = LocalDate.of(1974, Month.APRIL, 14);
		try(ByteArrayOutputStream os = new ByteArrayOutputStream();
			Workbook wb = new SXSSFWorkbook()) {
			Sheet sheet = wb.createSheet();
			WBEngine<P1> engine = WBEngine.builder(P1.class).mode(Mode.ORDER).build();
			P1 p = new P1();
			p.setValue(date);

			WBContext context = new WBContext();
			CellStyle style = wb.createCellStyle();
			style.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
			context.putStyle("localDate", style);

			engine.write(List.of(p), sheet, context);
			wb.write(os);
			wb.close();
			os.close();
			byte[] bytes = os.toByteArray();
			try(ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Workbook wb2 = WorkbookFactory.create(is)) {
				sheet = wb2.getSheetAt(0);
				List<P1> list = engine.parseAsStream(sheet).collect(Collectors.toList());
				assertEquals(1, list.size());
				p = list.get(0);
				assertEquals(date, p.getValue());
			}
		}
	}

	public static class P1 {
		@Convert(format="localDate")
		private LocalDate value;

		public LocalDate getValue() {
			return value;
		}

		public void setValue(LocalDate value) {
			this.value = value;
		}
	}

	@Test
	public void testLocalDateTime() throws IOException {
		LocalDateTime date = LocalDateTime.of(1974, Month.APRIL, 14, 10, 59, 59);
		try(ByteArrayOutputStream os = new ByteArrayOutputStream();
			Workbook wb = new SXSSFWorkbook()) {
			Sheet sheet = wb.createSheet();
			WBEngine<P2> engine = WBEngine.builder(P2.class).mode(Mode.ORDER).build();
			P2 p = new P2();
			p.setValue(date);

			WBContext context = new WBContext();
			CellStyle style = wb.createCellStyle();
			style.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd HH:mm:ss"));
			context.putStyle("localDateTime", style);

			engine.write(List.of(p), sheet, context);
			wb.write(os);
			wb.close();
			os.close();
			byte[] bytes = os.toByteArray();
			try(ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Workbook wb2 = WorkbookFactory.create(is)) {
				sheet = wb2.getSheetAt(0);
				List<P2> list = engine.parseAsStream(sheet).collect(Collectors.toList());
				assertEquals(1, list.size());
				p = list.get(0);
				assertEquals(date, p.getValue());
			}
		}
	}

	public static class P2 {
		@Convert(format="localDateTime")
		private LocalDateTime value;

		public LocalDateTime getValue() {
			return value;
		}

		public void setValue(LocalDateTime value) {
			this.value = value;
		}
	}

	@Test
	public void testLocalDateNoFormat() throws IOException {
		LocalDate date = LocalDate.of(1974, Month.APRIL, 14);
		try(ByteArrayOutputStream os = new ByteArrayOutputStream();
			Workbook wb = new SXSSFWorkbook()) {
			Sheet sheet = wb.createSheet();
			WBEngine<P3> engine = WBEngine.builder(P3.class).mode(Mode.ORDER).build();
			P3 p = new P3();
			p.setValue(date);

			WBContext context = new WBContext();
			CellStyle style = wb.createCellStyle();
			style.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
			context.putStyle("localDate", style);

			engine.write(List.of(p), sheet, context);
			wb.write(os);
			wb.close();
			os.close();
			byte[] bytes = os.toByteArray();
			try(ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Workbook wb2 = WorkbookFactory.create(is)) {
				sheet = wb2.getSheetAt(0);
				List<P3> list = engine.parseAsStream(sheet).collect(Collectors.toList());
				assertEquals(1, list.size());
				p = list.get(0);
				assertEquals(date, p.getValue());
			}
		}
	}

	public static class P3 {
		private LocalDate value;

		public LocalDate getValue() {
			return value;
		}

		public void setValue(LocalDate value) {
			this.value = value;
		}
	}

	@Test
	public void testLocalDateTimeNoFormat() throws IOException {
		LocalDateTime date = LocalDateTime.of(1974, Month.APRIL, 14, 10, 59, 59);
		try(ByteArrayOutputStream os = new ByteArrayOutputStream();
			Workbook wb = new SXSSFWorkbook()) {
			Sheet sheet = wb.createSheet();
			WBEngine<P4> engine = WBEngine.builder(P4.class).mode(Mode.ORDER).build();
			P4 p = new P4();
			p.setValue(date);

			WBContext context = new WBContext();
			CellStyle style = wb.createCellStyle();
			style.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd HH:mm:ss"));
			context.putStyle("localDateTime", style);

			engine.write(List.of(p), sheet, context);
			wb.write(os);
			wb.close();
			os.close();
			byte[] bytes = os.toByteArray();
			try(ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Workbook wb2 = WorkbookFactory.create(is)) {
				sheet = wb2.getSheetAt(0);
				List<P4> list = engine.parseAsStream(sheet).collect(Collectors.toList());
				assertEquals(1, list.size());
				p = list.get(0);
				assertEquals(date, p.getValue());
			}
		}
	}

	public static class P4 {
		private LocalDateTime value;

		public LocalDateTime getValue() {
			return value;
		}

		public void setValue(LocalDateTime value) {
			this.value = value;
		}
	}
}
