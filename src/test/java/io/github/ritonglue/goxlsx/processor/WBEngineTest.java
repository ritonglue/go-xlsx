package io.github.ritonglue.goxlsx.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;

import io.github.ritonglue.goxlsx.annotation.WBBinding;

public class WBEngineTest {
	@Test
	public void testPrimitive() throws IOException {
		Pojo p = new Pojo();
		p.setBooleanP(true);
		p.setIsBooleanB(true);

		p.setShortP((short) 1);
		p.setShortB((short) -1);

		p.setIntP(2);
		p.setIntB(-2);

		p.setLongP((long) 3);
		p.setLongB((long) -3);

		p.setCharP((char) 'c');
		p.setCharB((char) 'C');

		p.setByteP((byte) 127);
		p.setByteB((byte) -127);

		p.setDoubleP(1.1);
		p.setDoubleB(-1.1);

		p.setFloatP((float) 2.2);
		p.setFloatB((float) -2.2);

		p.setBigDecimal(new BigDecimal("99.9"));
		p.setBigInteger(BigInteger.valueOf(123));
		p.setCurrency(Currency.getInstance("USD"));

		try(ByteArrayOutputStream os = new ByteArrayOutputStream();
			Workbook wb = new SXSSFWorkbook()) {
			Sheet sheet = wb.createSheet();
			WBEngine<Pojo> engine = WBEngine.builder(Pojo.class).mode(Mode.ORDER).build();
			engine.write(List.of(p), sheet, null);
			wb.write(os);
			wb.close();
			os.close();
			byte[] bytes = os.toByteArray();
			try(ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Workbook wb2 = WorkbookFactory.create(is)) {
				sheet = wb2.getSheetAt(0);
				List<Pojo> list = engine.parseAsStream(sheet).collect(Collectors.toList());
				assertEquals(1, list.size());
				p = list.get(0);
				assertTrue(p.isBooleanP());
				assertEquals(Boolean.TRUE, p.getIsBooleanB());
				assertNull(p.getIsBooleanN());

				assertEquals((short)1, p.getShortP());
				assertEquals((short)-1, (short)p.getShortB());
				assertNull(p.getShortN());

				assertEquals(2, p.getIntP());
				assertEquals(-2, (int)p.getIntB());
				assertNull(p.getIntN());

				assertEquals(3L, p.getLongP());
				assertEquals(-3L, (long)p.getLongB());
				assertNull(p.getLongN());

				assertEquals('c', p.getCharP());
				assertEquals('C', (char)p.getCharB());
				assertNull(p.getCharN());

				assertEquals((byte)127, p.getByteP());
				assertEquals((byte)-127, (byte)p.getByteB());
				assertNull(p.getByteN());

				double eps = 0.001;
				assertEquals(1.1, p.getDoubleP(), eps);
				assertEquals(-1.1, p.getDoubleB(), eps);
				assertNull(p.getDoubleN());

				assertEquals(2.2f, p.getFloatP(), eps);
				assertEquals(-2.2f, p.getFloatB(), eps);
				assertNull(p.getFloatN());

				assertEquals(new BigDecimal("99.9"), p.getBigDecimal());
				assertNull(p.getBigDecimalN());

				assertEquals(BigInteger.valueOf(123), p.getBigInteger());
				assertNull(p.getBigIntegerN());

				assertEquals("USD", p.getCurrency().getCurrencyCode());
				assertNull(p.getCurrencyN());
			}
		}
	}

	public static class Pojo {
		@WBBinding(order = 0)
		private boolean isBooleanP;
		@WBBinding(order = 1)
		private Boolean isBooleanB;
		@WBBinding(order = 2)
		private Boolean isBooleanN;
		@WBBinding(order = 3)
		private short shortP;
		@WBBinding(order = 4)
		private Short shortB;
		@WBBinding(order = 5)
		private Short shortN;
		@WBBinding(order = 6)
		private int intP;
		@WBBinding(order = 7)
		private Integer intB;
		@WBBinding(order = 8)
		private Integer intN;
		@WBBinding(order = 9)
		private long longP;
		@WBBinding(order = 10)
		private Long longB;
		@WBBinding(order = 11)
		private Long longN;
		@WBBinding(order = 12)
		private char charP;
		@WBBinding(order = 13)
		private Character charB;
		@WBBinding(order = 14)
		private Character charN;
		@WBBinding(order = 15)
		private byte byteP;
		@WBBinding(order = 16)
		private Byte byteB;
		@WBBinding(order = 17)
		private Byte byteN;
		@WBBinding(order = 18)
		private double doubleP;
		@WBBinding(order = 19)
		private Double doubleB;
		@WBBinding(order = 20)
		private Double doubleN;
		@WBBinding(order = 21)
		private float floatP;
		@WBBinding(order = 22)
		private Float floatB;
		@WBBinding(order = 23)
		private Float floatN;
		@WBBinding(order = 24)
		private BigDecimal bigDecimal;
		@WBBinding(order = 25)
		private BigDecimal bigDecimalN;
		@WBBinding(order = 26)
		private BigInteger bigInteger;
		@WBBinding(order = 27)
		private BigInteger bigIntegerN;
		@WBBinding(order = 28)
		private Currency currency;
		@WBBinding(order = 29)
		private Currency currencyN;

		public Currency getCurrencyN() {
			return currencyN;
		}
		public void setCurrencyN(Currency currencyN) {
			this.currencyN = currencyN;
		}
		public Currency getCurrency() {
			return currency;
		}
		public void setCurrency(Currency currency) {
			this.currency = currency;
		}
		public BigDecimal getBigDecimalN() {
			return bigDecimalN;
		}
		public void setBigDecimalN(BigDecimal bigDecimalN) {
			this.bigDecimalN = bigDecimalN;
		}
		public BigInteger getBigIntegerN() {
			return bigIntegerN;
		}
		public void setBigIntegerN(BigInteger bigIntegerN) {
			this.bigIntegerN = bigIntegerN;
		}

		public boolean isBooleanP() {
			return isBooleanP;
		}
		public void setBooleanP(boolean isBooleanP) {
			this.isBooleanP = isBooleanP;
		}
		public Boolean getIsBooleanB() {
			return isBooleanB;
		}
		public void setIsBooleanB(Boolean isBooleanB) {
			this.isBooleanB = isBooleanB;
		}
		public Boolean getIsBooleanN() {
			return isBooleanN;
		}
		public void setIsBooleanN(Boolean isBooleanN) {
			this.isBooleanN = isBooleanN;
		}
		public Short getShortN() {
			return shortN;
		}
		public void setShortN(Short shortN) {
			this.shortN = shortN;
		}
		public Integer getIntN() {
			return intN;
		}
		public void setIntN(Integer intN) {
			this.intN = intN;
		}
		public Long getLongN() {
			return longN;
		}
		public void setLongN(Long longN) {
			this.longN = longN;
		}
		public Character getCharN() {
			return charN;
		}
		public void setCharN(Character charN) {
			this.charN = charN;
		}
		public Byte getByteN() {
			return byteN;
		}
		public void setByteN(Byte byteN) {
			this.byteN = byteN;
		}
		public Double getDoubleN() {
			return doubleN;
		}
		public void setDoubleN(Double doubleN) {
			this.doubleN = doubleN;
		}
		public Float getFloatN() {
			return floatN;
		}
		public void setFloatN(Float floatN) {
			this.floatN = floatN;
		}
		public int getIntP() {
			return intP;
		}
		public void setIntP(int intP) {
			this.intP = intP;
		}
		public Integer getIntB() {
			return intB;
		}
		public void setIntB(Integer intB) {
			this.intB = intB;
		}
		public short getShortP() {
			return shortP;
		}
		public void setShortP(short shortP) {
			this.shortP = shortP;
		}
		public Short getShortB() {
			return shortB;
		}
		public void setShortB(Short shortB) {
			this.shortB = shortB;
		}
		public long getLongP() {
			return longP;
		}
		public void setLongP(long longP) {
			this.longP = longP;
		}
		public Long getLongB() {
			return longB;
		}
		public void setLongB(Long longB) {
			this.longB = longB;
		}
		public char getCharP() {
			return charP;
		}
		public void setCharP(char charP) {
			this.charP = charP;
		}
		public Character getCharB() {
			return charB;
		}
		public void setCharB(Character charB) {
			this.charB = charB;
		}
		public byte getByteP() {
			return byteP;
		}
		public void setByteP(byte byteP) {
			this.byteP = byteP;
		}
		public Byte getByteB() {
			return byteB;
		}
		public void setByteB(Byte byteB) {
			this.byteB = byteB;
		}
		public double getDoubleP() {
			return doubleP;
		}
		public void setDoubleP(double doubleP) {
			this.doubleP = doubleP;
		}
		public Double getDoubleB() {
			return doubleB;
		}
		public void setDoubleB(Double doubleB) {
			this.doubleB = doubleB;
		}
		public float getFloatP() {
			return floatP;
		}
		public void setFloatP(float floatP) {
			this.floatP = floatP;
		}
		public Float getFloatB() {
			return floatB;
		}
		public void setFloatB(Float floatB) {
			this.floatB = floatB;
		}
		public BigDecimal getBigDecimal() {
			return bigDecimal;
		}
		public void setBigDecimal(BigDecimal bigDecimal) {
			this.bigDecimal = bigDecimal;
		}
		public BigInteger getBigInteger() {
			return bigInteger;
		}
		public void setBigInteger(BigInteger bigInteger) {
			this.bigInteger = bigInteger;
		}
	}
}
