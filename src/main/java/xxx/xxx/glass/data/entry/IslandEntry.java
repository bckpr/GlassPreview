package xxx.xxx.glass.data.entry;

import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.data.parser.ClassParser;
import xxx.xxx.glass.exceptions.NotImplementedException;
import xxx.xxx.glass.utils.Beautifier;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Consumer;

public class IslandEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final UUID islandUuid;
    private final IslandAction islandAction;

    public IslandEntry(final BaseEntry baseEntry, final UUID islandUuid, final IslandAction islandAction) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.islandUuid = islandUuid;
        this.islandAction = islandAction;

    }

    public IslandEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                       final UUID islandUuid, final IslandAction islandAction) {

        super(uuid, user, Action.ISLAND, timestamp, position, identifier);

        this.islandUuid = islandUuid;
        this.islandAction = islandAction;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("islandUuid", islandUuid)
                .append("islandAction", islandAction.name());

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public UUID getIslandUuid() {

        return islandUuid;

    }

    public IslandAction getIslandAction() {

        return islandAction;

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nIsland: " + islandUuid.toString());
                componentBuilder.append("\nAction: " + Beautifier.beautifyEnum(islandAction));
                complexMessage = new CraftComplexMessage(componentBuilder.create());
                break;
            case DISCORD:
                complexMessage = new DiscordComplexMessage(new MessageCreateSpec().addEmbed(generateEmbedConsumer()).asRequest());
                break;
            case DISCORD_SLASH:
                complexMessage = new DiscordSlashComplexMessage(interaction -> {
                    interaction.setEphemeral(true);
                    interaction.addEmbed(generateEmbedConsumer());
                });
                break;
            default:
                complexMessage = null;
        }

        return complexMessage;

    }

    private Consumer<EmbedCreateSpec> generateEmbedConsumer() {

        return (embed) -> {
            getBaseEntry().populateComplexMessage(embed);
            embed.addField("Island", islandUuid.toString(), false);
            embed.addField("Action", Beautifier.beautifyEnum(islandAction), true);
        };

    }

    public static class Parser extends ClassParser<IslandEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        private IslandEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final UUID islandUuid = document.get("islandUuid", UUID.class);
            final IslandAction islandAction = IslandAction.parseSafe(document.getString("islandAction"));

            return new IslandEntry(baseEntry, islandUuid, islandAction);

        }

        private IslandEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

    public enum IslandAction {

        REPLACE,
        UNKNOWN;

        /**
         * Used to safely parse a string input, if no value was found it
         * will return a value of type UNKNOWN. The parsing process is
         * case insensitive.
         *
         * @param input The input string to parse.
         * @return The found enum value.
         * @see IslandAction#UNKNOWN
         */

        public static IslandAction parseSafe(final String input) {

            return parseSafe(input, UNKNOWN);

        }

        /**
         * Used to safely parse a string input, if no value was found it
         * will return the specified default value. The parsing process
         * is case insensitive.
         *
         * @param input        The input string to parse.
         * @param defaultValue The default value.
         * @return The found enum value.
         */

        public static IslandAction parseSafe(final String input, final IslandAction defaultValue) {

            for (final IslandAction action : IslandAction.values()) {
                if (action.name().equalsIgnoreCase(input))
                    return action;
            }

            return defaultValue;

        }

    }

}
