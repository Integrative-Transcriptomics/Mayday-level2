package mayday.Reveal.utilities;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeProvider {

	public static String getCurrentData() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+2:00"));
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		String date = dayOfMonth + "." + (month+1) + "." + year;
		return date;
	}
	
	public static String getCurrentTime() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+2:00"));
		int hour12 = cal.get(Calendar.HOUR);         // 0..11
		int minutes = cal.get(Calendar.MINUTE);      // 0..59
		int seconds = cal.get(Calendar.SECOND);      // 0..59
		boolean am = cal.get(Calendar.AM_PM) == Calendar.AM;
		String time = hour12+":" + minutes + ":" + seconds + " " + (am ? "AM" : "PM");
		return time;
	}
	
	public static void main(String[] args) {
		String currentTime = getCurrentTime();
		String currentDate = getCurrentData();
		System.out.println(currentTime);
		System.out.println(currentDate);
		String name1 = "Apfelsaft";
		String name2 = "Orangensaft";
		extendByTime(name1);
		extendByDate(name2);
		System.out.println(name1);
		System.out.println(name2);
	}
	
	public static String extendByTime(String s) {
		s += " (" + getCurrentTime() + ")";
		return s;
	}
	
	public static String extendByDate(String s) {
		s+= " [" + getCurrentData() + "]";
		return s;
	}
}
