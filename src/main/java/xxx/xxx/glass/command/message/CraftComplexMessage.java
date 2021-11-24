package xxx.xxx.glass.command.message;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Craft implementation of a ComplexMessage.
 */

public class CraftComplexMessage implements ComplexMessage {

    private final BaseComponent[] components;

    public CraftComplexMessage(final BaseComponent[] components) {

        this.components = components;

    }

    /**
     * Used to return the underlying base component array which can be used
     * to send a complex message to a craft server implementation channel.
     *
     * @return The base component array.
     */

    public BaseComponent[] getComponents() {

        return components;

    }

}
