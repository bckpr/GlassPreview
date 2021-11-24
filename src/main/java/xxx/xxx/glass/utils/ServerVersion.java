package xxx.xxx.glass.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to make working with version dependent code easier.
 */

public enum ServerVersion {

    V1_17(117, "1.17"),
    V1_16(116, "1.16"),
    V1_15(115, "1.15"),
    V1_14(114, "1.14"),
    V1_13(113, "1.13"),
    V1_12(112, "1.12"),
    UNKNOWN(0, "Unknown");

    public static String PACKAGE_VERSION;
    public static ServerVersion DETECTED_VERSION;

    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]{1,2}.[0-9]{1,2})(.[0-9]{1,2})?");

    static {

        PACKAGE_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        final String version = Bukkit.getServer().getBukkitVersion().split("-")[0];

        try {
            final Matcher matcher = VERSION_PATTERN.matcher(version);
            if (matcher.find()) {
                DETECTED_VERSION = ServerVersion.fromIdUnsafe(Integer.parseInt(matcher.group(1).replace(".", "")));
            } else {
                DETECTED_VERSION = ServerVersion.UNKNOWN;
            }
        } catch (final NumberFormatException | ServerVersionNotFoundException ex) {
            ex.printStackTrace();
            DETECTED_VERSION = ServerVersion.UNKNOWN;
        }

    }

    /**
     * Used to check if the detected version is older than the provided version.
     *
     * @param serverVersion The version to compare to.
     * @return <code>true</code> if the detected version is older than the provided version, <code>false</code> otherwise.
     */

    public static boolean isDetectedOlderThan(final ServerVersion serverVersion) {

        return DETECTED_VERSION.isOlderThan(serverVersion);

    }

    /**
     * Used to check if the detected version is younger than the provided version.
     *
     * @param serverVersion The version to compare to.
     * @return <code>true</code> if the detected version is younger than the provided version, <code>false</code> otherwise.
     */

    public static boolean isDetectedYoungerThan(final ServerVersion serverVersion) {

        return DETECTED_VERSION.isYoungerThan(serverVersion);

    }

    /**
     * Used to check if the current version is older than the provided version.
     *
     * @param serverVersion The version to compare to.
     * @return <code>true</code> if the current version is older than the provided version, <code>false</code> otherwise.
     */

    public boolean isOlderThan(final ServerVersion serverVersion) {

        return id < serverVersion.id;

    }

    /**
     * Used to check if the current version is younger than the provided version.
     *
     * @param serverVersion The version to compare to.
     * @return <code>true</code> if the current version is younger than the provided version, <code>false</code> otherwise.
     */

    public boolean isYoungerThan(final ServerVersion serverVersion) {

        return id > serverVersion.id;

    }

    private final int id;
    private final String name;

    ServerVersion(final int id, final String name) {

        this.id = id;
        this.name = name;

    }

    /**
     * Used to return the version id.
     *
     * @return The version id.
     */

    public int getId() {

        return id;

    }

    /**
     * Used to return the version name.
     *
     * @return The version name.
     */

    public String getName() {

        return name;

    }

    /**
     * Used to get an enum value based on the provided version id.
     *
     * @param id The version id.
     * @return The found enum value or null.
     */

    @Nullable
    public static ServerVersion fromId(final int id) {

        for (final ServerVersion version : ServerVersion.values())
            if (version.getId() == id) return version;
        return null;

    }

    /**
     * Used to get an enum value based on the provided version id, returns
     * the provided default value if not found.
     *
     * @param id  The version id.
     * @param def The default value.
     * @return The found enum value or the provided default.
     */

    public static ServerVersion fromId(final int id, final ServerVersion def) {

        final ServerVersion serverVersion = fromId(id);
        return (serverVersion != null) ? serverVersion : def;

    }

    /**
     * Used to get an enum value based on the provided version id, this
     * method will throw an exception if the id doesn't exist.
     *
     * @param id The version id.
     * @return The found enum value.
     * @throws ServerVersionNotFoundException Gets thrown if the provided version id couldn't be found.
     */

    public static ServerVersion fromIdUnsafe(final int id) throws ServerVersionNotFoundException {

        final ServerVersion serverVersion = fromId(id);
        if (serverVersion != null) return serverVersion;
        else throw new ServerVersionNotFoundException(String.format("Couldn't find any version with id '%d'!", id));

    }

    /**
     * Used to get an enum value based on the provided name.
     *
     * @param name The version name.
     * @return The found enum value or null.
     */

    @Nullable
    public static ServerVersion fromName(final String name) {

        for (final ServerVersion version : ServerVersion.values())
            if (version.getName().equals(name)) return version;
        return null;

    }

    /**
     * Used to get an enum value based on the provided name, returns
     * the provided default value if not found.
     *
     * @param name The version name.
     * @param def  The default value.
     * @return The found enum value or the provided default.
     */

    public static ServerVersion fromName(final String name, final ServerVersion def) {

        final ServerVersion serverVersion = fromName(name);
        return (serverVersion != null) ? serverVersion : def;

    }

    /**
     * Used to get an enum value based on the provided version name, this
     * method will throw an exception if the name doesn't exist.
     *
     * @param name The version name.
     * @return The found enum value.
     * @throws ServerVersionNotFoundException Gets thrown if the provided version name couldn't be found.
     */

    public static ServerVersion fromNameUnsafe(final String name) throws ServerVersionNotFoundException {

        final ServerVersion serverVersion = fromName(name);
        if (serverVersion != null) return serverVersion;
        else throw new ServerVersionNotFoundException(String.format("Couldn't find any version with name '%s'!", name));

    }

    static class ServerVersionNotFoundException extends Exception {

        public ServerVersionNotFoundException() {
        }

        public ServerVersionNotFoundException(final String message) {

            super(message);

        }

    }

}
