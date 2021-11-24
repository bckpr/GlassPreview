package xxx.xxx.glass.command.context;

import xxx.xxx.glass.command.channel.CommunicationChannel;

/**
 * Contract for CommandExecutionContext implementations.
 */

public interface CommandExecutionContext {

    CommunicationChannel getCommunicationChannel();

}
