package xxx.xxx.glass.command.converter;

import xxx.xxx.glass.command.CommandRegistry;
import xxx.xxx.glass.command.commands.Command;

/**
 * Contract for necessary converters if the standard command implementation can't be used
 * for whatever reason.
 *
 * @param <T> The target type.
 */

public interface CommandConverter<T> {

    T convert(final CommandRegistry commandRegistry, final Command command);

}
