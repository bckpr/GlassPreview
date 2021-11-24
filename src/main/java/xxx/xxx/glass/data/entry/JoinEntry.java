package xxx.xxx.glass.data.entry;

import xxx.xxx.glass.command.CommandType;
import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import xxx.xxx.glass.command.message.DiscordComplexMessage;
import xxx.xxx.glass.command.message.DiscordSlashComplexMessage;
import xxx.xxx.glass.common.Position;
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

public class JoinEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final String ip;
    private final String hashedIp;

    public JoinEntry(final BaseEntry baseEntry, final String ip, final String hashedIp) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.ip = ip;
        this.hashedIp = hashedIp;

    }

    public JoinEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                     final String ip, final String hashedIp) {

        super(uuid, user, Action.JOIN, timestamp, position, identifier);

        this.ip = ip;
        this.hashedIp = hashedIp;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("ip", ip)
                .append("hashedIp", hashedIp);

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public String getIp() {

        return ip;

    }

    public String getHashedIp() {

        return hashedIp;

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nIp: " + hashedIp);
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
            embed.addField("Ip", hashedIp, false);
        };

    }

    public static class Parser extends ClassParser<JoinEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        private JoinEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final String ip = document.getString("ip");
            final String hashedIp = document.getString("hashedIp");

            return new JoinEntry(baseEntry, ip, hashedIp);

        }

        private JoinEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

}
