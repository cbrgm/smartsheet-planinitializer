package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateCalculator {

	private static Calendar _calendarInstance = new GregorianCalendar().getInstance();

	/**
	 * Addiert/Subtrahiert eine bestimmte Anzahl von Tagen auf das Datum drauf.
	 * 
	 * @param date
	 * @param days
	 */
	public String calculateDate(String date, Double days) {

		String[] splittedDate = date.split("-");
		int year = Integer.parseInt(splittedDate[0]);
		int month = Integer.parseInt(splittedDate[1]) - 1;
		int day = Integer.parseInt(splittedDate[2]);

		_calendarInstance.clear();
		_calendarInstance.set(year, month, day);
		_calendarInstance.add(Calendar.DATE, days.intValue());

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String result = dateFormat.format(_calendarInstance.getTime());
		return result;
	}

	public void convertString(String date) {
		String[] splittedDate = date.split("T");
		System.out.println(splittedDate[0]);
	}
}
