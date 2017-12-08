package com.iss.core.generate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCriteria<T extends BaseEntity> {
	private String upperCaseFirstChar(String property) {
		String c = property.charAt(0) + "";
		property = property.substring(1);
		return c.toUpperCase() + property;
	}

	private Class<T> baseEntityClass;

	@SuppressWarnings("unchecked")
	public AbstractCriteria() {
		ParameterizedType type = (ParameterizedType) this.getClass().getSuperclass().getGenericSuperclass();
		baseEntityClass = (Class<T>) type.getActualTypeArguments()[0];
	}

	private Field getField(Class<?> clazz, String property) throws Exception {
		try {
			Field field = clazz.getDeclaredField(property);
			return field;
		} catch (Exception e) {
			clazz = clazz.getSuperclass();
			if (clazz != null) {
				return getField(clazz, property);
			}
		}
		return null;
	}

	private Field getField(String property) throws Exception {
		Field field = null;
		if (property.contains(".")) {
			Class<?> propertyClazz = null;
			String[] properties = property.split("\\.");
			for (int i = 0; i < properties.length; i++) {
				if (i == 0) {
					field = getField(baseEntityClass, properties[i]);
				} else {
					field = getField(propertyClazz, properties[i]);
				}
				if (field == null) {
					throw new RuntimeException(baseEntityClass.getSimpleName() + " can't find a property named "
							+ property);
				}
				propertyClazz = field.getType();
			}
		} else {
			field = getField(baseEntityClass, property);
		}
		if (field == null) {
			throw new RuntimeException(baseEntityClass.getSimpleName() + " can't find a property named " + property);
		}
		return field;
	}

	private Field validatePropertyType(String property, Class<?> type) throws Exception {
		Field field = getField(property);
		if (type != field.getType()) {
			throw new RuntimeException(baseEntityClass.getSimpleName() + " can't find a property named ("
					+ type.getSimpleName() + " " + property + ")");
		}
		return field;
	}

	private String calculateMethodStr(String property, String suffix) throws Exception {
		String method = "and";
		if (property.contains(".")) {
			String[] properties = property.split("\\.");
			int length = properties.length - 1;
			if (length == 1) {
				method = method + upperCaseFirstChar(properties[0]);
			} else {
				for (int i = 0; i < length; i++) {
					if (i == 0) {
						method = method + upperCaseFirstChar(properties[i]) + "_";
					} else if (i == length - 1) {
						method = method + properties[i];
					} else {
						method = method + properties[i] + "_";
					}
				}
			}
		} else {
			method = method + upperCaseFirstChar("base");
		}
		method = method + suffix;
		return method;
	}

	private Method getMethod(String methodStr, Class<?>... clazzs) throws Exception {
		Method method = this.getClass().getSuperclass().getDeclaredMethod(methodStr, clazzs);
		if (method == null) {
			String clazzStr = "";
			if (clazzs != null) {
				for (int i = 0; i < clazzs.length; i++) {
					if (i == (clazzs.length - 1)) {
						clazzStr = clazzStr + clazzs[i].getSimpleName();
					} else {
						clazzStr = clazzStr + clazzs[i].getSimpleName() + ", ";
					}
				}
			}
			throw new RuntimeException(methodStr + "(" + clazzStr + ") can't  be found ");
		}
		return method;
	}

	public AbstractCriteria<T> AddCondition(BaseSqlOperator sqlOperator, String property, Object... values) {
		try {
			if (sqlOperator == BaseSqlOperator.SQL) {
				String methodStr = "addCriterion";
				Method method = getMethod(methodStr, String.class);
				method.invoke(this, values[0]);
			} else {
				String methodStr = calculateMethodStr(property, "Condition");
				Method method = getMethod(methodStr, String.class, BaseSqlOperator.class, Object[].class);
				if (sqlOperator.isStringValue()) {
					Field field = validatePropertyType(property, String.class);
					method.invoke(this, field.getName(), sqlOperator, values);
				} else {
					Field field = getField(property);
					if (sqlOperator.isArrayValue()) {
						List<?> valueAry = (List<?>) values[0];
						if (isNumber(field)) {
							List<Number> parsedValues = new ArrayList<Number>();
							for (Object value : valueAry) {
								if (value.getClass() == String.class) {
									Number number = (Number) testNumber(value.toString(), field);
									parsedValues.add(number);
								}
							}
							valueAry = parsedValues;
							values[0] = valueAry;
						}
					} else if (sqlOperator.isBetweenValue()) {
						Object less = values[0];
						Object greater = values[1];
						if (less.getClass() == String.class) {
							less = testNumber(less.toString(), field);
							values[0] = less;
						}
						if (greater.getClass() == String.class) {
							greater = testNumber(greater.toString(), field);
							values[1] = greater;
						}
					} else if (sqlOperator.isNoValue()) {
						values = null;
					} else if (sqlOperator.isSingleValue()) {
						Object value = values[0];
						if (value.getClass() == String.class) {
							value = testNumber(value.toString(), field);
							values[0] = value;
						}
					}
					method.invoke(this, field.getName(), sqlOperator, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private Object testNumber(String value, Field field) {
		Number number = 0;
		Class<?> clazz = field.getType();
		boolean isNumber = true;
		if (clazz == Integer.class) {
			number = Integer.parseInt(value);
		} else if (clazz == Short.class) {
			number = Short.parseShort(value);
		} else if (clazz == Byte.class) {
			number = Byte.parseByte(value);
		} else if (clazz == Long.class) {
			number = Long.parseLong(value);
		} else if (clazz == Float.class) {
			number = Float.parseFloat(value);
		} else if (clazz == Double.class) {
			number = Double.parseDouble(value);
		} else if (clazz == int.class) {
			number = Integer.parseInt(value);
		} else if (clazz == short.class) {
			number = Short.parseShort(value);
		} else if (clazz == byte.class) {
			number = Byte.parseByte(value);
		} else if (clazz == long.class) {
			number = Long.parseLong(value);
		} else if (clazz == float.class) {
			number = Float.parseFloat(value);
		} else if (clazz == double.class) {
			number = Double.parseDouble(value);
		} else {
			isNumber = false;
		}
		if (!isNumber) {
			return value;
		}
		return number;
	}

	private boolean isNumber(Field field) {
		Class<?> clazz = field.getType();
		boolean isNumber = true;
		if (clazz == Integer.class) {
		} else if (clazz == Short.class) {
		} else if (clazz == Byte.class) {
		} else if (clazz == Long.class) {
		} else if (clazz == Float.class) {
		} else if (clazz == Double.class) {
		} else if (clazz == int.class) {
		} else if (clazz == short.class) {
		} else if (clazz == byte.class) {
		} else if (clazz == long.class) {
		} else if (clazz == float.class) {
		} else if (clazz == double.class) {
		} else {
			isNumber = false;
		}
		return isNumber;
	}

}