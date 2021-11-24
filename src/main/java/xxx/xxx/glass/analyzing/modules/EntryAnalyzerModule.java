package xxx.xxx.glass.analyzing.modules;

import xxx.xxx.glass.analyzing.EntrySubscriber;
import xxx.xxx.glass.analyzing.actions.Countermeasure;
import xxx.xxx.glass.command.channel.CommunicationChannel;
import xxx.xxx.glass.common.ClassHandler;
import xxx.xxx.glass.data.entry.Entry;
import xxx.xxx.glass.utils.DiscordUtils;
import discord4j.core.event.domain.interaction.ButtonInteractEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Base for all analyzing modules.
 */

public abstract class EntryAnalyzerModule extends ClassHandler<Entry> implements EntrySubscriber {

    private final CommunicationChannel notificationChannel;
    private final Countermeasure countermeasure;

    public EntryAnalyzerModule(final CommunicationChannel notificationChannel, final Countermeasure countermeasure) {

        this.notificationChannel = notificationChannel;
        this.countermeasure = countermeasure;

    }

    @Override
    public void onNewEntry(final Entry entry) {

        if (contains(entry.getClass()))
            getHandler(entry.getClass()).accept(entry);

    }

    public CommunicationChannel getNotificationChannel() {

        return notificationChannel;

    }

    public Countermeasure getCountermeasure() {

        return countermeasure;

    }

    public MessageCreateSpec getMessageCreateSpec(final String title, final String description) {

        final MessageCreateSpec messageCreateSpec = new MessageCreateSpec();
        messageCreateSpec.addEmbed(embed -> {
            embed.setTitle(title);
            embed.setDescription(description);
            embed.addField("Suggested action", getCountermeasure().getDescription(), true);
            embed.setFooter("Glass will reward you with a Good Noodle star for claiming this report.", "http://i.epvpimg.com/DGjifab.png");
            embed.setColor(Color.of(9, 132, 227));
        });

        return messageCreateSpec;

    }

    public void handleClaimButton(final ButtonInteractEvent buttonInteractEvent) {

        buttonInteractEvent.acknowledge().subscribe();

        final Message message = buttonInteractEvent.getMessage();
        message.edit(messageEditSpec ->
                messageEditSpec.setComponents(DiscordUtils.removeButtonFromMessage(message, buttonInteractEvent.getCustomId()))
        ).subscribe();

        final Optional<Member> member = buttonInteractEvent.getInteraction().getMember();
        message.getChannel().doOnNext(messageChannel -> messageChannel.createMessage(messageCreateSpec -> {
            messageCreateSpec.setContent(String.format("This report has been claimed by <@%s>", member.map(value -> value.getId().asLong()).orElse(0L)));
            messageCreateSpec.setMessageReference(message.getId());
        }).subscribe()).subscribe();

    }

    public void handlePerformActionButton(final ButtonInteractEvent buttonInteractEvent) {

        buttonInteractEvent.acknowledge().subscribe();
        buttonInteractEvent.getMessage().edit(messageEditSpec ->
                messageEditSpec.setComponents(DiscordUtils.removeButtonFromMessage(buttonInteractEvent.getMessage(), buttonInteractEvent.getCustomId()))
        ).subscribe();

    }

    public abstract void sendNotification(final Player target, final Map<String, String> placeholders);

}
