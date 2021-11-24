package xxx.xxx.glass.data.entry;

import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.data.parser.ClassParser;
import xxx.xxx.glass.exceptions.NotImplementedException;
import xxx.xxx.glass.utils.Beautifier;
import xxx.xxx.glass.utils.TimeUtils;
import xxx.xxx.glass.utils.UUIDUtils;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstract entry object
 */

public abstract class Entry {

    private final UUID uuid;
    private final User user;
    private final Action action;
    private final long timestamp;
    private final Position position;
    private final String identifier;
    private BaseEntry baseEntry;

    private final Map<Class<?>, Consumer> exporters = new HashMap<>();

    public Entry(final UUID uuid, final User user, final Action action, final long timestamp, final Position position, final String identifier) {

        this.uuid = uuid;
        this.user = user;
        this.action = action;
        this.timestamp = timestamp;
        this.position = position;
        this.identifier = identifier;

    }

    /**
     * Used to register a export consumer for a specific class.
     *
     * @param clazz    The class type.
     * @param consumer The export consumer
     * @param <I>      The type of the input data.
     */

    <I> void registerExporter(final Class<I> clazz, final Consumer<I> consumer) {

        exporters.put(clazz, consumer);

    }

    /**
     * Used to get a export consumer from the internal map for the specified
     * class if present.
     *
     * @param clazz The class.
     * @return The export consumer if found.
     */

    @Nullable
    public Consumer get(final Class<?> clazz) {

        return exporters.get(clazz);

    }

    /**
     * Export the objects data into the provided object of possible.
     *
     * @param input The input object the data should be filled in.
     * @param <I>   The type of the input object.
     * @return <code>true</code> if a valid exporter was found.
     */

    @SuppressWarnings("unchecked")
    public <I> boolean exportData(final I input) {

        final Consumer<I> consumer = get(input.getClass());
        if (consumer != null) {
            consumer.accept(input);
            return true;
        }

        return false;

    }

    public UUID getUniqueId() {

        return uuid;

    }

    public User getUser() {

        return user;

    }

    public Action getAction() {

        return action;

    }

    public long getTimestamp() {

        return timestamp;

    }

    public Position getPosition() {

        return position;

    }

    public String getIdentifier() {

        return identifier;

    }

    /**
     * Lazy load BaseEntry to avoid infinite loop.
     *
     * @return The BaseEntry instance
     * @see BaseEntry
     */

    @NotNull
    BaseEntry getBaseEntry() {

        if (baseEntry == null)
            baseEntry = new BaseEntry(this);

        return baseEntry;

    }

    public String toInfoString() {

        return String.format(
                "(%s ago) Player: %s - Action: %s",
                TimeUtils.convertSecondsToFormattedTime((System.currentTimeMillis() - timestamp) / 1000, false),
                user.getUsername(),
                Beautifier.beautifyEnum(action)
        );

    }

    public abstract ComplexMessage toComplexMessage(final CommandType commandType);

    public String getDatabaseIdentifier() {

        return action.name().toLowerCase();

    }

    public static class BaseEntry extends Entry {

        {
            registerExporter(Document.class, this::exportIntoDocument);
            registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
        }

        BaseEntry(final Entry entry) {

            super(entry.getUniqueId(), entry.getUser(), entry.getAction(), entry.getTimestamp(), entry.getPosition(), entry.getIdentifier());

        }

        BaseEntry(final UUID uuid, final User user, final Action action, final long timestamp, final Position position, final String identifier) {

            super(uuid, user, action, timestamp, position, identifier);

        }

        private void exportIntoDocument(final Document document) {

            document
                    .append("uuid", getUniqueId())
                    .append("user", new Document()
                            .append("username", getUser().getUsername())
                            .append("uuid", getUser().getUniqueId()))
                    .append("action", getAction().name())
                    .append("timestamp", getTimestamp())
                    .append("position", new Document()
                            .append("world", getPosition().getWorld())
                            .append("x", getPosition().getX())
                            .append("y", getPosition().getY())
                            .append("z", getPosition().getZ()))
                    .append("identifier", getIdentifier());

        }

        private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

            try {
                preparedStatement.setBinaryStream(1, UUIDUtils.uuidToStream(getUniqueId()));
                preparedStatement.setString(2, getUser().getUsername());
                preparedStatement.setBinaryStream(3, UUIDUtils.uuidToStream(getUser().getUniqueId()));
                preparedStatement.setString(4, getAction().name());
                preparedStatement.setLong(5, getTimestamp());
                preparedStatement.setString(6, getPosition().getWorld());
                preparedStatement.setInt(7, getPosition().getX());
                preparedStatement.setInt(8, getPosition().getY());
                preparedStatement.setInt(9, getPosition().getZ());
                preparedStatement.setString(10, getIdentifier());
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public ComplexMessage toComplexMessage(final CommandType commandType) {

            return null;

        }

        public void populateComplexMessage(final ComponentBuilder componentBuilder) {

            componentBuilder.append(Beautifier.beautifyEnum(getAction()) + " (" + getUniqueId().toString() + ")");
            componentBuilder.append("\nThis is a description!");
            componentBuilder.append("\nPlayer: " + getUser().getUsername() + " (" + getUser().getUniqueId().toString() + ")");
            componentBuilder.append("\nDate: " + TimeUtils.convertTimestampToFormattedDate(getTimestamp()));
            componentBuilder.append("\nLocation: " + getPosition().toString());

        }

        public void populateComplexMessage(final EmbedCreateSpec embed) {

            embed.setTitle(Beautifier.beautifyEnum(getAction()) + " (" + getUniqueId().toString() + ")");
            embed.setDescription("This is a description!");
            embed.setColor(Color.of(9, 132, 227));
            embed.addField("Player", getUser().getUsername() + " (" + getUser().getUniqueId().toString() + ")", false);
            embed.addField("Date", TimeUtils.convertTimestampToFormattedDate(getTimestamp()), true);
            embed.addField("Location", getPosition().toString(), true);

        }

        public static class Parser extends ClassParser<BaseEntry> {

            {
                register(Document.class, this::parseDocument);
                register(ResultSet.class, this::parseResultSet);
            }

            private BaseEntry parseDocument(final Document document) {

                final UUID uuid = document.get("uuid", UUID.class);

                final Document userDocument = (Document) document.get("user");
                final String username = userDocument.getString("username");
                final UUID userUUID = userDocument.get("uuid", UUID.class);
                final User user = new User(username, userUUID);

                final Action action = Action.parseSafe(document.getString("action"));
                final long timestamp = document.getLong("timestamp");

                final Document positionDocument = (Document) document.get("position");
                final String world = positionDocument.getString("world");
                final int x = positionDocument.getInteger("x");
                final int y = positionDocument.getInteger("y");
                final int z = positionDocument.getInteger("z");
                final Position position = new Position(world, x, y, z);
                final String identifier = document.getString("identifier");

                return new BaseEntry(uuid, user, action, timestamp, position, identifier);

            }

            private BaseEntry parseResultSet(final ResultSet resultSet) {

                throw new NotImplementedException();

            }

        }

    }

}
