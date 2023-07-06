package io.github.ritonglue.goxlsx.processor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import io.github.ritonglue.goxlsx.annotation.Access;
import io.github.ritonglue.goxlsx.annotation.AccessType;
import io.github.ritonglue.goxlsx.annotation.Convert;
import io.github.ritonglue.goxlsx.annotation.Transient;
import io.github.ritonglue.goxlsx.annotation.WBBinding;
import io.github.ritonglue.goxlsx.convert.AttributeConverter;
import io.github.ritonglue.goxlsx.convert.DefaultConverters;
import io.github.ritonglue.goxlsx.convert.EnumConverter;

public class WBEngine<T> {
	private final Mode mode;
	private final Class<T> clazz;
	private final boolean ignoreHeaderCase;
	private final List<AnnotationStorer> storers = new ArrayList<>();
	private final Map<Class<?>, AttributeConverter<?,?>> converters;
	private final Map<CallbackEnum, Consumer<? super T>> callbacks = new EnumMap<>(CallbackEnum.class);

	public final static String DATE_FORMAT = "yyyy-mm-dd";
	public final static String DATE_TIME_FORMAT = "yyyy-mm-dd hh:mm:ss";
	public final static String DATE_FORMAT_KEY= "localDate";
	public final static String DATE_TIME_FORMAT_KEY = "localDateTime";

	public static class Builder<T> {
		private Mode mode;
		private Class<T> clazz;
		private boolean ignoreHeaderCase;
		private Map<Class<?>, AttributeConverter<?,?>> converters;

		public Builder<T> mode(Mode mode) {this.mode = mode; return this;}
		public Builder<T> ignoreHeaderCase(boolean ignoreHeaderCase) {this.ignoreHeaderCase = ignoreHeaderCase; return this;}
		public Builder<T> clazz(Class<T> clazz) {this.clazz = clazz; return this;}
		public Builder<T> register(Map<Class<?>, AttributeConverter<?,?>> converters) {this.converters = converters; return this;}
		public <U> Builder<T> register(Class<U> clazz, Class<? extends AttributeConverter<U,?>> converterClazz) {
			try {
				AttributeConverter<U,?> converter = converterClazz.getDeclaredConstructor().newInstance();
				return register(clazz, converter);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		public <U> Builder<T> register(Class<U> clazz, AttributeConverter<U,?> converter) {
			if(this.converters == null) this.converters = new HashMap<>();
			this.converters.put(clazz, converter);
			return this;
		}

		public WBEngine<T> build() {
			return new WBEngine<>(this);
		}
	}

	public static <T> Builder<T> builder(Class<T> clazz) {
		return new Builder<T>().clazz(clazz);
	}

	private WBEngine(Builder<T> b) {
		this.mode = Objects.requireNonNull(b.mode, "mode null");
		this.clazz = Objects.requireNonNull(b.clazz, "clazz null");
		this.ignoreHeaderCase = b.ignoreHeaderCase;
		this.converters = new HashMap<>();
		this.converters.putAll(DefaultConverters.getConverters());
		//replace or add new converters
		if(b.converters != null) {
			this.converters.putAll(b.converters);
		}
		init();
	}

	public boolean isIgnoreHeaderCase() {
		return ignoreHeaderCase;
	}

	/**
	 * Creates a new instance of the T class object
	 * @return  a newly allocated instance of the class represented by this object.
	 */
	protected T newInstance() {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void init() {
		try {
			initImpl();
		} catch (ReflectiveOperationException | IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isFieldAccess(Class<?> clazz, PropertyDescriptor[] pds) {
		boolean isFieldAccess = true;
		if(clazz.isAnnotationPresent(Access.class)) {
			AccessType accessType = clazz.getAnnotation(Access.class).value();
			if(accessType == null) accessType = AccessType.FIELD;
			switch(accessType) {
			case FIELD:
				isFieldAccess = true;
				break;
			case PROPERTY:
				isFieldAccess = false;
				break;
			}
		} else {
			for(PropertyDescriptor pd : pds) {
				Method getter = pd.getReadMethod();
				if(getter == null) continue;
				if(getter.isAnnotationPresent(WBBinding.class)) {
					isFieldAccess = false;
					break;
				}
				if(getter.isAnnotationPresent(Convert.class)) {
					isFieldAccess = false;
					break;
				}
			}
		}
		return isFieldAccess;
	}

	private Data getData(AccessibleObject o, String name, Class<?> type) throws ReflectiveOperationException {
		if(o == null) return null;
		int order = 0;
		int width = 0;
		boolean isHidden = false;
		String header = name;
		String format = "";
		AttributeConverter<?,?> converter = null;
		if(o.isAnnotationPresent(Transient.class)) {
			return null;
		}
		if(o.isAnnotationPresent(WBBinding.class)) {
			WBBinding binding = o.getAnnotation(WBBinding.class);
			order = binding.order();
			header = binding.header();
			width = binding.width();
			isHidden = binding.hidden();
		}
		if(header == null || header.isEmpty()) {
			header = name;
		}
		if(o.isAnnotationPresent(Convert.class)) {
			Convert aConvert = o.getAnnotation(Convert.class);
			format = aConvert.format();
			Class<?> converterClazz = aConvert.converter();
			try {
				converter = (AttributeConverter<?,?>) converterClazz.getDeclaredConstructor().newInstance();
			} catch(NoSuchMethodException e) {
				//no default constructor
			}
		}
		if(converter == null) {
			if(type.isEnum()) {
				//converter registered ?
				converter = this.converters.get(type);
				if(converter == null) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					EnumConverter tmp = new EnumConverter(type);
					this.converters.put(type, tmp);
				}
			}
			//auto-apply
			converter = this.converters.get(type);
		}
		Objects.requireNonNull(converter, "converter null: " + name);
		Data data = new Data();
		data.setOrder(order);
		data.setConverter(converter);
		data.setHeader(header);
		data.setFormat(format);
		data.setWidth(width);
		data.setHidden(isHidden);
		return data;
	}

	private void initImpl() throws ReflectiveOperationException, IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		boolean isFieldAccess = isFieldAccess(clazz, pds);

		if(isFieldAccess) {
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields) {
				int modifiers = field.getModifiers();
				if(Modifier.isStatic(modifiers)) continue;
				String name = field.getName();
				Data data = getData(field, name, field.getType());
				if(data != null) {
					AnnotationStorer storer = new AnnotationStorer(field, data);
					storers.add(storer);
				}
			}
		} else {
			for(PropertyDescriptor pd : pds) {
				String name = pd.getName();
				Method getter = pd.getReadMethod();
				if(getter == null) continue;
				int modifiers = getter.getModifiers();
				if(Modifier.isStatic(modifiers)) continue;
				Data data = getData(getter, name, getter.getReturnType());
				if(data != null) {
					AnnotationStorer storer = new AnnotationStorer(pd, data);
					storers.add(storer);
				}
			}
		}

		//sort : only for writing
		Collections.sort(storers, Comparator.comparingInt(AnnotationStorer::getOrder));

		//check duplicate
		Map<String, AnnotationStorer> mapHeader = new HashMap<>();
		Map<Integer, AnnotationStorer> mapOrder = new HashMap<>();
		for(AnnotationStorer storer : storers) {
			String header = storer.getHeader();
			int order = storer.getOrder();
			Object o = null;
			switch(mode) {
			case NAMED:
				Objects.requireNonNull(header, "header null");
				break;
			case ORDER:
				o = mapOrder.put(order, storer);
				if(o != null) {
					throw new IllegalArgumentException("duplicate order: " + order);
				}
				break;
			}
			if(header != null) {
				o = mapHeader.put(header, storer);
				if(o != null) {
					throw new IllegalArgumentException("duplicate header: " + header);
				}
			}
		}
		readLifeCycle();
	}

	private void readLifeCycle() {
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			addLifeCycle(CallbackEnum.POST_LOAD, method, "postLoad");
			addLifeCycle(CallbackEnum.POST_PERSIST, method, "postPersist");
			addLifeCycle(CallbackEnum.PRE_PERSIST, method, "prePersist");
		}
	}

	private void addLifeCycle(CallbackEnum callback, Method method, String text) {
		if(callback.hasAnnotation(method)) {
			method.setAccessible(true);
			var o = this.callbacks.put(callback, new Callback(method));
			if(o != null) {
				throw new IllegalStateException("multiple "+ text + " annotation");
			}
		}
	}

	/**
	 * lifeCycle method consumer
	 */
	private static class Callback implements Consumer<Object> {
		private final Method method;

		private Callback(Method method) {
			this.method = method;
		}

		@Override
		public void accept(Object t) {
			try {
				method.invoke(t);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static <E> Stream<E> streamOf(Iterable<E> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	@SuppressWarnings("StreamToIterable")
	private static <E> Iterable<E> iterableOf(Stream<E> stream) {
		return stream::iterator;
	}

	public Stream<T> parseAsStream(Sheet sheet) {
		Iterator<Row> iterator = sheet.iterator();
		Function<AnnotationStorer, Integer> getterIndex = getterIndex(iterator);
		Iterable<Row> iterable = () -> iterator;
		return streamOf(iterable)
				.filter(Objects::nonNull)
				.map(o -> convert(o, getterIndex));
	}

	public Stream<T> parseAsStream(Iterable<Row> iterable) {
		Iterator<Row> iterator = iterable.iterator();
		Function<AnnotationStorer, Integer> getterIndex = getterIndex(iterator);
		return streamOf(iterable)
				.filter(Objects::nonNull)
				.map(o -> convert(o, getterIndex));
	}

	public Iterable<T> parse(Iterable<Row> iterable) throws IOException {
		return iterableOf(parseAsStream(iterable));
	}

	public Iterable<T> parse(Sheet sheet) throws IOException {
		return iterableOf(parseAsStream(sheet));
	}

	private Map<String, Integer> createEmptyHeaderMap() {
		return this.isIgnoreHeaderCase() ?
			new TreeMap<>(String.CASE_INSENSITIVE_ORDER) :
			new HashMap<>();
	}

		private Function<AnnotationStorer, Integer> getterIndex(Iterator<Row> iterator) {
		Function<AnnotationStorer, Integer> getterIndex = null;
		switch(mode) {
		case NAMED:
			//build headerMap
			Map<String, Integer> headerMap = this.createEmptyHeaderMap();
			if(iterator.hasNext()) {
				Row row = iterator.next();
				int n = 0;
				for(Cell cell : row) {
					CellType type = cell.getCellType();
					switch(type) {
					case STRING:
						headerMap.put(cell.getRichStringCellValue().toString(), n);
						break;
					default:
					}
					++n;
				}
			}
			getterIndex = o -> {
				String header = o.getHeader();
				return headerMap.get(header);
			};
			break;
		case ORDER:
			getterIndex = AnnotationStorer::getOrder;
			break;
		}
		return getterIndex;
	}

	private T convert(Row row, Function<AnnotationStorer,Integer> getterIndex) {
		if(row == null) {
			return null;
		}
		T t = newInstance();
		for(AnnotationStorer storer : storers) {
			Integer index = getterIndex.apply(storer);
			if(index == null) {
				missingIndex(storer);
			} else {
				int i = index;
				Cell cell = row.getCell(i);
				setValue(t, cell, storer);
			}
		}
		Consumer<? super T> postMethod = this.callbacks.get(CallbackEnum.POST_LOAD);
		if(postMethod != null) {
			postMethod.accept(t);
		}
		return t;
	}

	private void missingIndex(AnnotationStorer storer) {
	}

	private void setValue(T t, Cell cell, AnnotationStorer storer) {
		@SuppressWarnings("rawtypes")
		AttributeConverter converter = storer.getConverter();
		CellType type = cell.getCellType();
		Object value = null;
		switch(type) {
		case STRING:
			value = cell.getRichStringCellValue().toString();
			break;
		case NUMERIC:
			if(DateUtil.isCellDateFormatted(cell)) {
				value = cell.getLocalDateTimeCellValue();
			} else {
				value = cell.getNumericCellValue();
			}
			break;
		case BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case FORMULA:
			CellType resultType = cell.getCachedFormulaResultType();
			switch(resultType) {
				case STRING:
					value = cell.getRichStringCellValue().toString();
					break;
				case NUMERIC:
					if(DateUtil.isCellDateFormatted(cell)) {
						value = cell.getLocalDateTimeCellValue();
					} else {
						value = cell.getNumericCellValue();
					}
					break;
				case BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case FORMULA:
				case BLANK:
				case ERROR:
				case _NONE:
					break;
			}
			break;
		case BLANK:
		case ERROR:
		case _NONE:
			break;
		}
		if(LocalDate.class.equals(storer.getClazz())) {
			if(value instanceof LocalDateTime) {
				LocalDateTime tmp = (LocalDateTime) value;
				value = tmp.toLocalDate();
			}
		}
		@SuppressWarnings("unchecked")
		Object object = converter.convertToEntityAttribute(value);
		try {
			Field field = storer.getField();
			Method setter = storer.getSetter();
			if(field != null) {
				field.setAccessible(true);
				field.set(t, object);
			} else if(setter != null) {
				setter.setAccessible(true);
				setter.invoke(t, object);
			} else {
				throw new AssertionError("no field, no setter");
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeImpl(Iterable<? extends T> iterable, Sheet sheet, WBContext context, int nrow) {
		try {
			Consumer<? super T> postPersist = callbacks.get(CallbackEnum.POST_PERSIST);
			Consumer<? super T> prePersist = callbacks.get(CallbackEnum.PRE_PERSIST);
			for(T t : iterable) {

				if(prePersist != null) {
					prePersist.accept(t);
				}

				Row row = sheet.createRow(nrow++);
				int j = 0;
				for(AnnotationStorer a : storers) {
					Field field = a.getField();
					Method getter = a.getGetter();
					@SuppressWarnings("rawtypes")
					AttributeConverter converter = a.getConverter();
					Object value = null;
					Cell cell = row.createCell(j++);
					if(field != null) {
						field.setAccessible(true);
						value = field.get(t);
					} else if(getter != null) {
						getter.setAccessible(true);
						value = getter.invoke(t);
					}
					@SuppressWarnings("unchecked")
					Object dbData = converter.convertToDatabaseColumn(value);
					boolean isDate = false;
					boolean isDateTime = false;
					if(dbData instanceof String) {
						cell.setCellValue((String) dbData);
					} else if(dbData instanceof Number) {
						cell.setCellValue(((Number) dbData).doubleValue());
					} else if(dbData instanceof LocalDate) {
						isDate = true;
						cell.setCellValue((LocalDate) dbData);
					} else if(dbData instanceof LocalDateTime) {
						isDateTime = true;
						cell.setCellValue((LocalDateTime) dbData);
					} else if(dbData instanceof Boolean) {
						cell.setCellValue(((Boolean) dbData));
					} else if(dbData instanceof Date) {
						cell.setCellValue((Date) dbData);
					} else if(dbData instanceof Calendar) {
						cell.setCellValue((Calendar) dbData);
					} else if(dbData instanceof RichTextString) {
						cell.setCellValue((RichTextString) dbData);
					} else if (dbData != null) {
						throw new RuntimeException();
					}
					String format = a.getFormat();
					CellStyle style = null;
					String key = null;
					String dateFormat = null;
					if(format != null && !format.isEmpty()) {
						style = context.getStyle(format);
						if(style == null) {
							throw new RuntimeException(
								String.format("missing style format '%s'. header '%s', order %s"
									, format, a.getHeader(), a.getOrder()));
						}
					} else if(isDate) {
						//auto apply date format
						key = DATE_FORMAT_KEY;
						dateFormat = DATE_FORMAT;
					} else if (isDateTime) {
						//auto apply dateTime format
						key = DATE_TIME_FORMAT_KEY;
						dateFormat = DATE_TIME_FORMAT;
					}

					if(key != null) {
						style = context.getStyle(key);
						if(style == null) {
							Workbook wb = sheet.getWorkbook();
							style = wb.createCellStyle();
							style.setDataFormat(wb.createDataFormat().getFormat(dateFormat));
							context.putStyle(key, style);
						}
					}

					if(style != null) {
						cell.setCellStyle(style);
					}
				}

				if(postPersist != null) {
					postPersist.accept(t);
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void write(Iterable<? extends T> iterable, Sheet sheet, WBContext context) throws IOException {
		if(context == null) context = new WBContext();
		int nrow = 0;
		if(mode == Mode.NAMED) {
			Row row = sheet.createRow(nrow++);
			int i = 0;
			CellStyle headerStyle = context.getStyle(context.getHeaderStyle());
			for(AnnotationStorer storer : storers) {
				String header = storer.getHeader();
				Cell cell = row.createCell(i++);
				cell.setCellValue(header);
				CellStyle style = context.getStyle(storer.getHeaderStyle());
				if(style == null) {
					//no style per column : apply general style
					style = headerStyle;
				}
				if(style != null) {
					cell.setCellStyle(style);
				}
			}
			if(i > 0 && context.isApplyFilter()) {
				sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, i-1));
			}
		}

		int n = 0;
		for(AnnotationStorer storer : storers) {
			int width = storer.getWidth();
			if(width > 0) {
				sheet.setColumnWidth(n, width * 256);
			}
			sheet.setColumnHidden(n, storer.isHidden());
			++n;
		}

		writeImpl(iterable, sheet, context, nrow);
	}

	public Mode getMode() {
		return mode;
	}

	public int getNumberOfColumns() {
		return this.storers.size();
	}

	/**
	 * Adjusts the columns width to fit the contents
	 *
	 * @param sheet
	 * @param useMergedCells whether to use the contents of merged cells when calculating the width of the column
	 */
	public void autoSizeColumn(Sheet sheet, boolean useMergedCells) {
		if(sheet == null) return;
		for(int i = 0, n = getNumberOfColumns() ; i < n ; ++i) {
			sheet.autoSizeColumn(i, useMergedCells);
		}
	}

	/**
	 * Adjusts the columns width to fit the contents
	 *
	 * @param sheet the sheet to apply autoSize
	 */
	public void autoSizeColumn(Sheet sheet) {
		this.autoSizeColumn(sheet, false);
	}
}
