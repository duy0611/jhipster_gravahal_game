package fi.vaasa.dnguyen.service.util;

public class StringUtil {

	public static boolean isNullOrEmpty(String value) {
		return value == null || (value != null && value.isEmpty());
	}
}
