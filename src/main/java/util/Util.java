package util;

/**
 * @author Christian Bargmann <christian.bargmann@haw-hamburg.de>
 * @see util
 * @since 12.12.2017 , 13:05:30
 *
 */
public class Util {

	public Util() {
	}

	/** faster util method that avoids creation of array for single-arg cases */
	public static <T> T throwIfNull(T obj) {
		if (obj == null) {
			throw new IllegalArgumentException();
		}
		return obj;
	}

	/** faster util method that avoids creation of array for two-arg cases */
	public static void throwIfNull(Object obj1, Object obj2) {
		if (obj1 == null) {
			throw new IllegalArgumentException();
		}
		if (obj2 == null) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Helper function that throws an IllegalArgumentException if one of the
	 * parameters is null.
	 * 
	 * @param objects
	 *            the paramters to
	 */
	public static void throwIfNull(Object... objects) {
		for (Object obj : objects) {
			throwIfNull(obj);
		}
	}

	/** faster util method that avoids creation of array for single-arg cases */
	public static void throwIfEmpty(String string) {
		if (string != null && string.isEmpty()) { // FIXME - so... null isn't empty?
			throw new IllegalArgumentException();
		}
	}

	public static void throwIfEmpty(String... strings) {
		for (String string : strings) {
			throwIfEmpty(string);
		}
	}
}