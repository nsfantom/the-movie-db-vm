package tm.fantom.tmdbvw.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateUtils {

    public static final DateTimeFormatter tmdbFmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
    public static final DateTimeFormatter tmdbFmtRelease = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter fmtRelease = DateTimeFormat.forPattern("MMM-dd',' yyyy");


    public static boolean isExpired(long time) {
        DateTime dateTime = new DateTime(time);
        DateTime dateTimeNow = DateTime.now();
        return dateTime.isBefore(dateTimeNow);
    }

    public static String parseRelease(String date) {
        return DateTime.parse(date, tmdbFmtRelease).toString(fmtRelease);
    }
}
