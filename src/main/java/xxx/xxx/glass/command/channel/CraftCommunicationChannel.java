package xxx.xxx.glass.command.channel;

import xxx.xxx.glass.command.message.ComplexMessage;
import xxx.xxx.glass.command.message.CraftComplexMessage;
import org.bukkit.command.CommandSender;

/**
 * CommunicationChannel implementation for craft server implementations.
 */

public class CraftCommunicationChannel implements CommunicationChannel {

    private final CommandSender sender;

    public CraftCommunicationChannel(final CommandSender sender) {

        this.sender = sender;

    }

    /**
     * Used to send a simple message to a craft
     * communication channel.
     *
     * @param message The simple message.
     */

    @Override
    public void sendMessage(final String message) {

        sender.sendMessage(message);

    }

    /**
     * Used to send a complex message to a craft
     * communication channel.
     *
     * @param complexMessage The complex message.
     */

    @Override
    public void sendComplexMessage(final ComplexMessage complexMessage) {

        final CraftComplexMessage craftComplexMessage = (CraftComplexMessage) complexMessage;
        sender.spigot().sendMessage(craftComplexMessage.getComponents());

    }

    /**
     * Used to get the sender.
     *
     * @return The sender.
     */

    public CommandSender getSender() {

        return sender;

    }

}
