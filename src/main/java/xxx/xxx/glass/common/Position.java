package xxx.xxx.glass.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

/**
 * A lightweight version of the Location class used for database storage.
 *
 * @see Location
 */

public class Position {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public Position(final String world, final int x, final int y, final int z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    /**
     * Used to get the world name.
     *
     * @return The world name.
     */

    public String getWorld() {

        return world;

    }

    /**
     * Used to get the x coordinate.
     *
     * @return The x coordinate.
     */

    public int getX() {

        return x;

    }

    /**
     * Used to get the y coordinate.
     *
     * @return The y coordinate.
     */

    public int getY() {

        return y;

    }

    /**
     * Used to get the z coordinate.
     *
     * @return The z coordinate.
     */

    public int getZ() {

        return z;

    }

    /**
     * Used to convert the current instance to a bukkit location.
     *
     * @return The converted location.
     */

    public Location toBukkitLocation() {

        return new Location(Bukkit.getWorld(world), x, y, z);

    }

    /**
     * Static factory method to create a new instance based on the
     * provided bukkit location.
     *
     * @param location The bukkit location.
     * @return The created instance.
     */

    public static Position fromLocation(final Location location) {

        return new Position(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

    }

    /**
     * Used to convert the current instance to a readable string.
     *
     * @return The readable string.
     */

    @Override
    public String toString() {

        return String.format("(%s) X: %d Y: %d Z: %d", world, x, y, z);

    }

    /**
     * Simple hash code generation implementation.
     *
     * @return The hash code.
     */

    @Override
    public int hashCode() {

        return Objects.hash(world, x, y, z);

    }

    /**
     * Simple equals implementations to check if the current and the provided
     * instance are considered equal.
     *
     * @param obj The object to compare to.
     * @return <code>true</code> if the objects are considered equal, <code>false</code> otherwise.
     */

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final Position position = (Position) obj;

        return x == position.x &&
                y == position.y &&
                z == position.z &&
                world.equals(position.world);

    }

}
