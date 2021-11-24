package xxx.xxx.glass.common;

/**
 * Makes it easier to work with millisecond based time spans.
 */

public class TimeSpan {

    private final long millis;

    public TimeSpan(final long millis) {

        this.millis = millis;

    }

    public TimeSpan(final int hours, final int minutes, final int seconds) {

        this.millis = convertTimeToMillis(hours, minutes, seconds);

    }

    /**
     * Internal method to convert the provided time into milliseconds.
     *
     * @param hours   The hours.
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @return The total milliseconds.
     */

    private long convertTimeToMillis(final int hours, final int minutes, final int seconds) {

        final long totalSeconds = (long) hours * 3600 + (long) minutes * 60 + (long) seconds;
        return totalSeconds * 1000;

    }

    /**
     * Used to get the total milliseconds.
     *
     * @return The total milliseconds.
     */

    public long getMillis() {

        return millis;

    }

    /**
     * Used to calculate and get the total seconds.
     *
     * @return The total seconds.
     */

    public int getSeconds() {

        return (int) millis / 1000;

    }

    /**
     * Used to calculate and get the total minutes.
     *
     * @return The total minutes.
     */

    public int getMinutes() {

        return (int) millis / (1000 * 60);

    }

    /**
     * Used to calculate and get the total hours.
     *
     * @return The total hours.
     */

    public int getHours() {

        return (int) millis / (1000 * 60 * 60);

    }

    /**
     * Static factory method used to create a new instance base
     * on the provided milliseconds.
     *
     * @param millis The milliseconds.
     * @return The created instance.
     */

    public static TimeSpan fromMillis(final long millis) {

        return new TimeSpan(millis);

    }

    /**
     * Static factory method used to create a new instance based
     * on the provided seconds.
     *
     * @param seconds The seconds.
     * @return The created instance.
     */

    public static TimeSpan fromSeconds(final int seconds) {

        return new TimeSpan(0, 0, seconds);

    }

    /**
     * Static factory method used to create a new instance based
     * on the provided minutes.
     *
     * @param minutes The minutes.
     * @return The created instance.
     */

    public static TimeSpan fromMinutes(final int minutes) {

        return new TimeSpan(0, minutes, 0);

    }

    /**
     * Static factory method used to create a new instance based
     * on the provided hours.
     *
     * @param hours The hours.
     * @return The created instance.
     */

    public static TimeSpan fromHours(final int hours) {

        return new TimeSpan(hours, 0, 0);

    }

}
