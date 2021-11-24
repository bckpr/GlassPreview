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
import xxx.xxx.glass.wrapped.WrappedItemStack;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bson.Document;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InventoryTransactionEntry extends Entry {

    {
        registerExporter(Document.class, this::exportIntoDocument);
        registerExporter(PreparedStatement.class, this::exportIntoPreparedStatement);
    }

    private final InventoryType inventoryType;
    private final String inventoryTitle;
    private final List<WrappedItemStack> addedItems;
    private final List<WrappedItemStack> removedItems;

    public InventoryTransactionEntry(final BaseEntry baseEntry, final InventoryType inventoryType, final String inventoryTitle,
                                     final List<WrappedItemStack> addedItems, final List<WrappedItemStack> removedItems) {

        super(baseEntry.getUniqueId(), baseEntry.getUser(), baseEntry.getAction(), baseEntry.getTimestamp(), baseEntry.getPosition(), baseEntry.getIdentifier());

        this.inventoryType = inventoryType;
        this.inventoryTitle = inventoryTitle;
        this.addedItems = addedItems;
        this.removedItems = removedItems;

    }

    public InventoryTransactionEntry(final UUID uuid, final User user, final long timestamp, final Position position, final String identifier,
                                     final InventoryType inventoryType, final String inventoryTitle, final List<WrappedItemStack> addedItems,
                                     final List<WrappedItemStack> removedItems) {

        super(uuid, user, Action.INVENTORY_TRANSACTION, timestamp, position, identifier);

        this.inventoryType = inventoryType;
        this.inventoryTitle = inventoryTitle;
        this.addedItems = addedItems;
        this.removedItems = removedItems;

    }

    private void exportIntoDocument(final Document document) {

        getBaseEntry().exportData(document);

        document
                .append("invType", inventoryType.name())
                .append("invTitle", inventoryTitle)
                .append("addedItems", addedItems.stream()
                        .map(wrappedItemStack -> new Document("item", ItemUtils.itemToJson(wrappedItemStack.getItem())).append("amount", wrappedItemStack.getAmount()))
                        .collect(Collectors.toList()))
                .append("removedItems", removedItems.stream()
                        .map(wrappedItemStack -> new Document("item", ItemUtils.itemToJson(wrappedItemStack.getItem())).append("amount", wrappedItemStack.getAmount()))
                        .collect(Collectors.toList()));

    }

    private void exportIntoPreparedStatement(final PreparedStatement preparedStatement) {

        throw new NotImplementedException();

    }

    public InventoryType getInventoryType() {

        return inventoryType;

    }

    public String getInventoryTitle() {

        return inventoryTitle;

    }

    public List<WrappedItemStack> getAddedItems() {

        return addedItems;

    }

    public List<WrappedItemStack> getRemovedItems() {

        return removedItems;

    }

    @Override
    public ComplexMessage toComplexMessage(final CommandType commandType) {


        ComplexMessage complexMessage;
        switch (commandType) {
            case CRAFT:
                final ComponentBuilder componentBuilder = new ComponentBuilder("");
                getBaseEntry().populateComplexMessage(componentBuilder);
                componentBuilder.append("\nInventory Type: " + Beautifier.beautifyEnum(inventoryType));
                componentBuilder.append("\nInventory Title: " + inventoryTitle);

                componentBuilder.append("\nAdded Items:");
                for (final WrappedItemStack addedItem : addedItems)
                    componentBuilder.append("\n" + addedItem.getAmount() + "x " + addedItem);

                componentBuilder.append("\nRemoved Items:");
                for (final WrappedItemStack removedItem : removedItems)
                    componentBuilder.append("\n" + removedItem.getAmount() + "x " + removedItem);

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
            embed.addField("Inventory Type", Beautifier.beautifyEnum(inventoryType), false);
            embed.addField("Inventory Title", inventoryTitle, true);
            embed.addField("Added Items", addedItems.stream()
                    .map(wrappedItemStack -> wrappedItemStack.getAmount() + "x " + wrappedItemStack)
                    .collect(Collectors.joining("\n")), true);
            embed.addField("Removed Items", removedItems.stream()
                    .map(wrappedItemStack -> wrappedItemStack.getAmount() + "x " + wrappedItemStack)
                    .collect(Collectors.joining("\n")), true);
        };

    }

    public static class Parser extends ClassParser<InventoryTransactionEntry> {

        {
            register(Document.class, this::parseDocument);
            register(ResultSet.class, this::parseResultSet);
        }

        @Nullable
        private InventoryTransactionEntry parseDocument(final Document document) {

            final BaseEntry baseEntry = new BaseEntry.Parser().parse(document);
            if (baseEntry == null) return null;

            final InventoryType inventoryType = InventoryType.valueOf(document.getString("invType"));
            final String inventoryTitle = document.getString("invTitle");
            final List<WrappedItemStack> addedItems = document.getList("addedItems", Document.class)
                    .stream()
                    .map(subDocument -> new WrappedItemStack(ItemUtils.jsonToItem(subDocument.getString("item")), subDocument.getInteger("amount")))
                    .collect(Collectors.toList());
            final List<WrappedItemStack> removedItems = document.getList("removedItems", Document.class)
                    .stream()
                    .map(subDocument -> new WrappedItemStack(ItemUtils.jsonToItem(subDocument.getString("item")), subDocument.getInteger("amount")))
                    .collect(Collectors.toList());

            return new InventoryTransactionEntry(baseEntry, inventoryType, inventoryTitle, addedItems, removedItems);

        }

        private InventoryTransactionEntry parseResultSet(final ResultSet resultSet) {

            throw new NotImplementedException();

        }

    }

}
