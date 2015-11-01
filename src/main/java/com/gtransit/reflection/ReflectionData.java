package com.gtransit.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import com.gtransit.csv.annotations.CsvIgnore;
import com.gtransit.graph.annotations.GraphProperty;
import com.gtransit.raw.data.RawData;

/**
 * Class to handle reflection jobs
 * 
 * @author Renato Barbosa
 * 
 * */
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

		List<String> fieldsList = new ArrayList<String>();

		Field[] allFields = FieldUtils.getAllFields(clazz);

		for (int i = 0; i < allFields.length; i++) {
			Field field = allFields[i];
			CsvIgnore annotation = field.getAnnotation(CsvIgnore.class);
			if (annotation == null) {
				fieldsList.add(allFields[i].getName());
			}
		}

		String[] fieldsName = new String[fieldsList.size()];

		return fieldsList.toArray(fieldsName);
	}

	public <T> List<T> buildList(Class<?> clazz,
			List<Map<String, String>> entries) throws Exception {

		List<T> list = new ArrayList<T>();

		Method[] methods = clazz.getMethods();

		for (Map<String, String> fields : entries) {

			Set<String> keySet = fields.keySet();

			T object = (T) clazz.newInstance();

			for (String field : keySet) {

				String value = fields.get(field);

				Method publicMethod = getMethod(methods,
						"set" + WordUtils.capitalize(field));

				publicMethod.invoke(object, value);
			}

			list.add(object);
		}

		return list;
	}

	public Map<String, Object> getFieldValue(RawData data, Class clazz)
			throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Method method = getMethod(methods,
					"get" + WordUtils.capitalize(field.getName()));
			if (method != null) {
				GraphProperty annotation = field
						.getAnnotation(GraphProperty.class);
				if (annotation != null) {
					properties.put(annotation.value(),
							method.invoke(data, null));
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
