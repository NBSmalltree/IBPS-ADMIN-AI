package com.actuary.ibps.common.util;

import com.actuary.ibps.common.constant.IbpsConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {
    }

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(IbpsConstants.DATE_FORMAT));

    private static final ThreadLocal<SimpleDateFormat> DATETIME_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(IbpsConstants.DATETIME_FORMAT));

    public static String formatDate(Date date) {
        return DATE_FORMAT.get().format(date);
    }

    public static String formatDateTime(Date date) {
        return DATETIME_FORMAT.get().format(date);
    }

    public static Date parseDate(String dateStr) throws ParseException {
        return DATE_FORMAT.get().parse(dateStr);
    }

    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        return DATETIME_FORMAT.get().parse(dateTimeStr);
    }

    public static boolean isWithinRange(Date start, Date end, int maxDays) {
        if (start == null || end == null) {
            return false;
        }
        long diffMs = end.getTime() - start.getTime();
        long maxMs = (long) maxDays * 24 * 60 * 60 * 1000;
        return diffMs >= 0 && diffMs <= maxMs;
    }

    public static boolean isWithin7Days(Date start, Date end) {
        return isWithinRange(start, end, IbpsConstants.MAX_QUERY_DAYS);
    }

    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getTodayStart() {
        return getStartOfDay(new Date());
    }

    public static Date getTodayEnd() {
        return getEndOfDay(new Date());
    }
}
