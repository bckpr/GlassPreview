package xxx.xxx.glass.data.entry;

import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.common.serialization.StringDeserializer;
import xxx.xxx.glass.common.serialization.StringSerializer;
import xxx.xxx.glass.data.parser.ClassParser;
import xxx.xxx.glass.exceptions.NotImplementedException;
import xxx.xxx.glass.wrapped.WrappedMaterialData;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockPlaceEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final WrappedMaterialData oldBlockType;
    private final WrappedMaterialData newBlockType;

    public BlockPlaceEntry(final BaseEntry baseEntry, final WrappedMaterialData oldBlockType, final WrappedMaterialData newBlockType) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.oldBlockType = oldBlockType;
        this.newBlockType = newBlockType;

    }

    public BlockPlaceEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                           final WrappedMaterialData oldBlockType, final WrappedMaterialData newBlockType) {

        super(uuid, user, Action.BLOCK_PLACE, timestamp, position, identifier);

        this.oldBlockType = oldBlockType;
        this.newBlockType = newBlockType;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("oldBlockType", StringSerializer.serializeUnsafe(oldBlockType))
                .append("newBlockType", StringSerializer.serializeUnsafe(newBlockType));

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public WrappedMaterialData getOldBlockType() {

        return oldBlockType;

    }

    public WrappedMaterialData getNewBlockType() {

        return newBlockType;

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nOld Block Type: " + oldBlockType.toString());
                componentBuilder.append("\nNew Block Type: " + newBlockType.toString());
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
            embed.addField("Old Block Type", oldBlockType.toString(), false);
            embed.addField("New Block Type", newBlockType.toString(), true);
        };

    }

    public static class Parser extends ClassParser<BlockPlaceEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        private BlockPlaceEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final WrappedMaterialData oldBlockType = StringDeserializer.deserializeUnsafe(document.getString("oldBlockType"));
            final WrappedMaterialData newBlockType = StringDeserializer.deserializeUnsafe(document.getString("newBlockType"));

            return new BlockPlaceEntry(baseEntry, oldBlockType, newBlockType);

        }

        private BlockPlaceEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

}
