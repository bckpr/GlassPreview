package xxx.xxx.glass.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

/**
 * Fluent builder used to easily create complex chat messages.
 */

public class TextMessageBuilder {

    private final TextComponent textComponent = new TextComponent();

    /**
     * Appends plain text to the underlying text component.
     *
     * @param text The text to append.
     * @return The current TextMessageBuilder instance.
     */

    public TextMessageBuilder appendText(final String text) {

        textComponent.addExtra(text);
        return this;

    }

    /**
     * Used to append a hover text component to the underlying text component.
     *
     * @param hoverText The hover text.
     * @param text      The text to append.
     * @return The current TextMessageBuilder instance.
     */

    public TextMessageBuilder appendHoverText(final String[] hoverText, final String text) {

        final TextComponent hoverTextComponent = new TextComponent();
        for (final BaseComponent component : TextComponent.fromLegacyText(text))
            hoverTextComponent.addExtra(component);
        final BaseComponent[] componentArray = new TextComponent[hoverText.length];
        for (int i = 0; i < hoverText.length; i++)
            componentArray[i] = new TextComponent(hoverText[i]);
        hoverTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentArray));
        textComponent.addExtra(hoverTextComponent);

        return this;

    }

    /**
     * Used to append an ItemStack showcase to the underlying text component.
     *
     * @param item The ItemStack to showcase.
     * @param text The text to append.
     * @return The current TextMessageBuilder instance.
     */

    public TextMessageBuilder appendItem(final ItemStack item, final String text) {

        final TextComponent itemComponent = new TextComponent();
        for (final BaseComponent component : TextComponent.fromLegacyText(text))
            itemComponent.addExtra(component);
        itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                new BaseComponent[]{
                        new TextComponent(ItemUtils.itemToJson(item))
                }));
        textComponent.addExtra(itemComponent);

        return this;

    }

    public TextComponent build() {

        return textComponent;

    }

}
