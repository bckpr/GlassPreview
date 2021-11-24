package xxx.xxx.glass.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utility methods related to working with time.
 */

public class TimeUtils {

    public final static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    /**
     * Used to convert seconds to a formatted time string, example:
     * 120 -> 2m (longVersion = false)
     * 120 -> 2 minutes (longVersion = true)
     *
     * @param seconds     The number of seconds.
     * @param longVersion Long version or not.
     * @return The converted string.
     */

    public static String convertSecondsToFormattedTime(long seconds, boolean longVersion) {

        final int days = (int) Math.round(seconds / 86400.0);
        seconds = seconds - (days * 86400L);
        final int hours = (int) Math.round(seconds / 3600.0);
        seconds = seconds - (hours * 3600L);
        final int minutes = (int) Math.round(seconds / 60.0);
        seconds = seconds - (minutes * 60L);

        final StringBuilder timeStringBuilder = new StringBuilder();

        if (days > 0)
            timeStringBuilder
                    .append(days)
                    .append(!longVersion ? "d " : (days > 1 ? " days " : " day "));
        if (hours > 0)
            timeStringBuilder
                    .append(hours)
                    .append(!longVersion ? "h " : (hours > 1 ? " hours " : " hour "));
        if (minutes > 0)
            timeStringBuilder
                    .append(minutes)
                    .append(!longVersion ? "m " : (minutes > 1 ? " minutes " : " minute "));
        if (seconds > 0)
            timeStringBuilder
                    .append(seconds)
                    .append(!longVersion ? "s" : (seconds > 1 ? " seconds " : " second "));

        return timeStringBuilder.toString().trim();

    }

    /**
     * Converts a unix timestamp to a fix SimpleDateFormat.
     *
     * @param timestamp The timestamp.
     * @return The formatted date as a string.
     */

    public static String convertTimestampToFormattedDate(final long timestamp) {

        return DEFAULT_DATE_FORMAT.format(new Date(timestamp));

    }

}
