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
import xxx.xxx.glass.utils.ItemUtils;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * @see InventoryTransactionEntry
 * @deprecated The InventoryTransactionEntry is the replacement candidate.
 */

@Deprecated
public class ContainerModificationEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final SubAction subAction;
    private final InventoryType inventoryType;
    private final ItemStack item;
    private final int amount;

    public ContainerModificationEntry(final BaseEntry baseEntry, final SubAction subAction, final InventoryType inventoryType, final ItemStack item, final int amount) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.subAction = subAction;
        this.inventoryType = inventoryType;
        this.item = item;
        this.amount = amount;

    }

    public ContainerModificationEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                                      final SubAction subAction, final InventoryType inventoryType, final ItemStack item, final int amount) {

        super(uuid, user, Action.CONTAINER_MODIFICATION, timestamp, position, identifier);

        this.subAction = subAction;
        this.inventoryType = inventoryType;
        this.item = item;
        this.amount = amount;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("subAction", subAction.name())
                .append("invType", inventoryType.name())
                .append("item", ItemUtils.itemToJson(item))
                .append("amount", amount);

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public SubAction getSubAction() {

        return subAction;

    }

    public InventoryType getInventoryType() {

        return inventoryType;

    }

    public ItemStack getItem() {

        return item;

    }

    public int getAmount() {

        return amount;

    }

    @Override
    public String toInfoString() {

        return super.toInfoString() + String.format(" (%s)", Beautifier.beautifyEnum(subAction));

    }

    @Override
    public String getDatabaseIdentifier() {

        return getAction().name().toLowerCase() + "_" + subAction.name().toLowerCase();

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nAction: " + Beautifier.beautifyEnum(subAction));
                componentBuilder.append("\nInventory Type: " + Beautifier.beautifyEnum(inventoryType));
                componentBuilder.append("\nItem: <Hover>")
                        .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                new BaseComponent[]{
                                        new TextComponent(ItemUtils.itemToJson(item))
                                }
                        ));
                componentBuilder.append("\nAmount: " + amount);
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
            embed.addField("Action", Beautifier.beautifyEnum(subAction), false);
            embed.addField("Inventory Type", Beautifier.beautifyEnum(inventoryType), true);
            embed.addField("Item", ItemUtils.itemToJson(item), true);
            embed.addField("Amount", Integer.toString(amount), true);
        };

    }

    public static class Parser extends ClassParser<ContainerModificationEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        @Nullable
        private ContainerModificationEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final SubAction subAction = SubAction.parseSafe(document.getString("subAction"));
            final InventoryType inventoryType = InventoryType.valueOf(document.getString("invType"));
            final ItemStack item = ItemUtils.jsonToItem(document.getString("item"));
            final int amount = document.getInteger("amount");

            return new ContainerModificationEntry(baseEntry, subAction, inventoryType, item, amount);

        }

        private ContainerModificationEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

}
