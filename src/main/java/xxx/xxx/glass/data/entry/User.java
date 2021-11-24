package xxx.xxx.glass.data.entry;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A lightweight version of the Player class used for database storage.
 * @see Player
 */

public class User {

    private final String username;
    private final UUID uuid;

    public User(final String username, final UUID uuid) {

        this.username = username;
        this.uuid = uuid;

    }

    /**
     * Used to get the username.
     *
     * @return The username.
     */

    public String getUsername() {

        return username;

    }

    /**
     * Used to get the unique id.
     *
     * @return The unique id.
     */

    public UUID getUniqueId() {

        return uuid;

    }

    /**
     * Creates a new instance based on the provided player.
     *
     * @param player The Player input.
     * @return The new instance.
     */

    public static User fromPlayer(final Player player) {

        return new User(player.getName(), player.getUniqueId());

    }

}
