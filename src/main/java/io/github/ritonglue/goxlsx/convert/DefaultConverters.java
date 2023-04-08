package io.github.ritonglue.goxlsx.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.RichTextString;

public class DefaultConverters {
	private final static Map<Class<?>, AttributeConverter<?,?>> MAP;

	static {
		Map<Class<?>, AttributeConverter<?,?>> map = new HashMap<>();
		map.put(byte.class, new ByteConverter());
		map.put(Byte.class, new ByteConverter());
		map.put(boolean.class, new IdentityConverter<Boolean>());
		map.put(Boolean.class, new IdentityConverter<Boolean>());
		map.put(char.class     , new CharacterConverter());
		map.put(Character.class, new CharacterConverter());
		map.put(short.class, new ShortConverter());
		map.put(Short.class, new ShortConverter());
		map.put(int.class, new IntegerConverter());
		map.put(Integer.class, new IntegerConverter());
		map.put(long.class, new LongConverter());
		map.put(Long.class, new LongConverter());
		map.put(double.class, new IdentityConverter<Double>());
		map.put(Double.class, new IdentityConverter<Double>());
		map.put(float.class, new FloatConverter());
		map.put(Float.class, new FloatConverter());
		map.put(BigDecimal.class, new BigDecimalConverter());
		map.put(BigInteger.class, new BigIntegerConverter());
		map.put(String.class, new IdentityConverter<String>());
		map.put(Currency.class, new CurrencyConverter());

		map.put(Calendar.class, new IdentityConverter<Calendar>());
		map.put(Date.class, new IdentityConverter<Date>());
		map.put(LocalDate.class, new LocalDateConverter());
		map.put(LocalDateTime.class, new IdentityConverter<LocalDateTime>());
		map.put(RichTextString.class, new IdentityConverter<RichTextString>());

		MAP = Collections.unmodifiableMap(map);
	}

	public static Map<Class<?>, AttributeConverter<?,?>> getConverters() {
		return MAP;
	}
}
