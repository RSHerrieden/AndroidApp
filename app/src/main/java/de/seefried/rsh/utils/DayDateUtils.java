package de.seefried.rsh.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayDateUtils {

    static String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
    static Calendar calendar = Calendar.getInstance();

    static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    static SimpleDateFormat date_dayFormat = new SimpleDateFormat("EE", Locale.GERMANY);

    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
    static String currentTimeString = timeFormat.format(new Date(System.currentTimeMillis()));
    static String timeForNextDayString = "13:30";

    static Date currentTime;
    static {
        try {
            currentTime = timeFormat.parse(currentTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static Date timeForNextDay;
    static {
        try {
            timeForNextDay = timeFormat.parse(timeForNextDayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String getDate() {
        if (weekday_name.equals("Saturday")) {
            calendar.add(Calendar.DAY_OF_YEAR, 2);
            Date plus2Days = calendar.getTime();
            return dateFormat.format(plus2Days);
        } else if (weekday_name.equals("Sunday")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date plus1Day = calendar.getTime();
            return dateFormat.format(plus1Day);
        } else {
            if (currentTime.before(timeForNextDay)) {
                return dateFormat.format(new Date());
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date plus1Day = calendar.getTime();
                return dateFormat.format(plus1Day);
            }
        }
    }

    public static String getDateDay() {
        if (weekday_name.equals("Saturday")) {
            return "Mo.";
        } else if (weekday_name.equals("Sunday")) {
            return "Mo.";
        } else {
            if (currentTime.before(timeForNextDay)) {
                return date_dayFormat.format(new Date());
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date plus1Day = calendar.getTime();
                return date_dayFormat.format(plus1Day);
            }
        }
    }
}
