package xxx.xxx.glass.data.entry;

import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.wrapped.WrappedMaterialData;
import xxx.xxx.glass.common.serialization.StringDeserializer;
import xxx.xxx.glass.common.serialization.StringSerializer;
import xxx.xxx.glass.data.parser.ClassParser;
import xxx.xxx.glass.exceptions.NotImplementedException;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockBreakEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final WrappedMaterialData blockType;
    private final int xp;

    public BlockBreakEntry(final BaseEntry baseEntry, final WrappedMaterialData blockType, final int xp) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.blockType = blockType;
        this.xp = xp;

    }

    public BlockBreakEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                           final WrappedMaterialData blockType, final int xp) {

        super(uuid, user, Action.BLOCK_BREAK, timestamp, position, identifier);

        this.blockType = blockType;
        this.xp = xp;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("blockType", StringSerializer.serializeUnsafe(blockType))
                .append("xp", xp);

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public WrappedMaterialData getBlockType() {

        return blockType;

    }

    public int getXp() {

        return xp;

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nBlock Type: " + blockType.toString());
                componentBuilder.append("\nXp: " + xp);
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
            embed.addField("Block Type", blockType.toString(), false);
            embed.addField("Xp", Integer.toString(xp), true);
        };

    }

    public static class Parser extends ClassParser<BlockBreakEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        private BlockBreakEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final WrappedMaterialData blockType = StringDeserializer.deserializeUnsafe(document.getString("blockType"));
            final int xp = document.getInteger("xp");

            return new BlockBreakEntry(baseEntry, blockType, xp);

        }

        private BlockBreakEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

}
