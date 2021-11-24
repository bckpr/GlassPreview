package xxx.xxx.glass.utils;

import discord4j.core.object.component.*;
import discord4j.core.object.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides utility methods for working with discord.
 */

public class DiscordUtils {

    /**
     * Used to remove a specific button based on its custom id from a message
     * and return the remaining LayoutComponents in a list.
     *
     * @param message  The input message.
     * @param customId The id of the button.
     * @return The remaining LayoutComponents in a list.
     */

    public static List<LayoutComponent> removeButtonFromMessage(final Message message, final String customId) {

        final List<LayoutComponent> layoutComponents = message.getComponents()
                .stream()
                .filter(messageComponent -> messageComponent instanceof LayoutComponent)
                .map(messageComponent -> ((LayoutComponent) messageComponent))
                .collect(Collectors.toList());

        final List<LayoutComponent> newLayoutComponents = new ArrayList<>();
        for (final LayoutComponent layoutComponent : layoutComponents) {
            if (!(layoutComponent instanceof ActionRow)) {
                newLayoutComponents.add(layoutComponent);
                continue;
            }

            final List<ActionComponent> actionComponents = new ArrayList<>();
            final ActionRow actionRow = (ActionRow) layoutComponent;
            for (final MessageComponent messageComponent : actionRow.getChildren()) {
                if (!(messageComponent instanceof ActionComponent)) continue;
                final ActionComponent actionComponent = (ActionComponent) messageComponent;
                if (actionComponent instanceof Button) {
                    final Button button = (Button) actionComponent;
                    if (!button.getCustomId().isPresent() || !button.getCustomId().get().equals(customId))
                        actionComponents.add(actionComponent);
                } else {
                    actionComponents.add(actionComponent);
                }
            }

            if (actionComponents.size() > 0)
                newLayoutComponents.add(ActionRow.of(actionComponents));
        }

        return newLayoutComponents;

    }

}
