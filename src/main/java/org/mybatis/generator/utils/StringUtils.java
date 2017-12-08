package org.mybatis.generator.utils;

public class StringUtils {

	public static String upperCaseFirstChar(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String lowerCaseFirstChar(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static boolean isBlank(String str) {
		return org.apache.commons.lang3.StringUtils.isBlank(str);
	}

	public static boolean isNotBlank(String str) {
		return org.apache.commons.lang3.StringUtils.isNotBlank(str);
	}

}
