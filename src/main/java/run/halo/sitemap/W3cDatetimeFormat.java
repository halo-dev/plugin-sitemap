package run.halo.sitemap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

/**
 * Different standards may need different levels of granularity in the date and time, so this
 * profile defines six levels. Standards that reference this profile should specify one or more
 * of these granularities. If a given standard allows more than one granularity, it should
 * specify the meaning of the dates and times with reduced precision, for example, the result of
 * comparing two dates with different precisions.
 * <p>
 * The formats are as follows. Exactly the components shown here must be present, with exactly
 * this punctuation. Note that the "T" appears literally in the string, to indicate the beginning
 * of the time element, as specified in ISO 8601.
 * <p>
 * Year:
 * YYYY (eg 1997)
 * Year and month:
 * YYYY-MM (eg 1997-07)
 * Complete date:
 * YYYY-MM-DD (eg 1997-07-16)
 * Complete date plus hours and minutes:
 * YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
 * Complete date plus hours, minutes and seconds:
 * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
 * Complete date plus hours, minutes, seconds and a decimal fraction of a
 * second
 * YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
 * where:
 * <p>
 * YYYY = four-digit year
 * MM   = two-digit month (01=January, etc.)
 * DD   = two-digit day of month (01 through 31)
 * hh   = two digits of hour (00 through 23) (am/pm NOT allowed)
 * mm   = two digits of minute (00 through 59)
 * ss   = two digits of second (00 through 59)
 * s    = one or more digits representing a decimal fraction of a second
 * TZD  = time zone designator (Z or +hh:mm or -hh:mm)
 * This profile does not specify how many digits may be used to represent the decimal fraction of
 * a second. An adopting standard that permits fractions of a second must specify both the
 * minimum number of digits (a number greater than or equal to one) and the maximum number of
 * digits (the maximum may be stated to be "unlimited").
 * <p>
 * This profile defines two ways of handling time zone offsets:
 * <p>
 * Times are expressed in UTC (Coordinated Universal Time), with a special UTC designator ("Z").
 * Times are expressed in local time, together with a time zone offset in hours and minutes. A
 * time zone offset of "+hh:mm" indicates that the date/time uses a local time zone which is "hh"
 * hours and "mm" minutes ahead of UTC. A time zone offset of "-hh:mm" indicates that the
 * date/time uses a local time zone which is "hh" hours and "mm" minutes behind UTC.
 * A standard referencing this profile should permit one or both of these ways of handling time
 * zone offsets.
 *
 * @author guqing
 * @since 1.0.0
 */
@UtilityClass
public final class W3cDatetimeFormat {
    public static final DateTimeFormatter MILLISECOND_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static final DateTimeFormatter SECOND_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    public static final DateTimeFormatter MINUTE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ");

    public static String format(Instant instant, DateTimeFormatter formatter) {
        return instant.atZone(ZoneId.systemDefault())
            .format(formatter.withZone(ZoneId.systemDefault()));
    }

    public static String format(Instant instant) {
        return format(instant, MILLISECOND_FORMATTER);
    }
}
