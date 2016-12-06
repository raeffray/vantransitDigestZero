package com.gtransit.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import com.gtransit.csv.annotations.CsvFieldOrdering;
import com.gtransit.csv.annotations.CsvIgnore;
import com.gtransit.graph.annotations.GraphProperty;
import com.gtransit.raw.data.RawData;

/**
 * Class to handle reflection jobs
 * 
 * @author Renato Barbosa
 * 
 */
public class ReflectionData {

	private static ReflectionData instance = null;

	static Logger logger = Logger.getLogger(ReflectionData.class);

	protected ReflectionData() {

	}

	public static ReflectionData getInstance() {
		if (instance == null) {
			instance = new ReflectionData();
		}
		return instance;
	}

	@SuppressWarnings("rawtypes")
	public String[] extractFieldsNames(Class<? extends RawData> clazz) {

		Field[] allFields = FieldUtils.getAllFields(clazz);

		List<FieldOrdering> fieldsToOrder = new ArrayList<>();

		for (int i = 0; i < allFields.length; i++) {
			Field field = allFields[i];
			CsvIgnore notIgnored = field.getAnnotation(CsvIgnore.class);
			CsvFieldOrdering ordering = field.getAnnotation(CsvFieldOrdering.class);
			if (notIgnored == null) {
				fieldsToOrder.add(new FieldOrdering(field.getName(), ordering.position()));
			}
		}
		return fieldsToOrder.stream().sorted(Comparator.comparing(FieldOrdering::getPosition))
				.map(FieldOrdering::getFieldName).toArray(s -> new String[s]);
	}

	public <T> Collection<T> buildList(Class<?> clazz, Collection<Map<String, String>> entries) throws Exception {

		List<T> list = new ArrayList<T>();

		Method[] methods = clazz.getMethods();

		for (Map<String, String> fields : entries) {

			Set<String> keySet = fields.keySet();

			T object = (T) clazz.newInstance();

			for (String field : keySet) {

				String value = fields.get(field);

				Method publicMethod = getMethod(methods, "set" + WordUtils.capitalize(field));

				try {
					publicMethod.invoke(object, value);
				} catch (Exception e) {
					logger.error("error invoking method for property + " + field);
					throw e;
				}
			}

			list.add(object);
		}

		return list;
	}

	public Map<String, Object> getFieldValue(RawData data, Class clazz) throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Method method = getMethod(methods, "get" + WordUtils.capitalize(field.getName()));
			if (method != null) {
				GraphProperty annotation = field.getAnnotation(GraphProperty.class);
				if (annotation != null) {
					properties.put(annotation.value(), method.invoke(data, null));
				}
			}

		}
		return properties;
	}

	private Method getMethod(Method[] methods, String methodName) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				return methods[i];
			}
		}
		return null;
	}
}

class FieldOrdering {

	public FieldOrdering(String fieldName, int position) {
		super();
		this.fieldName = fieldName;
		this.position = position;
	}

	private String fieldName;

	private int position;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}