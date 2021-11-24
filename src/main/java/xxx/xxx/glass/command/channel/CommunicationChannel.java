package xxx.xxx.glass.command.channel;

import xxx.xxx.glass.command.message.ComplexMessage;

/**
 * Contract for CommunicationChannel implementations.
 */

public interface CommunicationChannel {

    void sendMessage(final String message);

    void sendComplexMessage(final ComplexMessage complexMessage);

}
