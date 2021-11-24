package xxx.xxx.glass.command;

import xxx.xxx.glass.command.arguments.CommandArgument;
import xxx.xxx.glass.command.commands.SubCommand;
import xxx.xxx.glass.common.FluentBuilder;

/**
 * Holds the options for registered commands.
 */

public class CommandOptions {

    private String name = "";
    private SubCommand[] subCommands = new SubCommand[0];
    private String description = "";
    private String usage = "";
    private int minArgs = 0;
    private int maxArgs = 0;
    private CommandArgument[] arguments = new CommandArgument[0];

    private CommandOptions() {
    }

    public CommandOptions(final String name,
                          final SubCommand[] subCommands,
                          final String description,
                          final String usage,
                          final int minArgs,
                          final int maxArgs,
                          final CommandArgument[] arguments) {

        this.name = name;
        this.subCommands = subCommands;
        this.description = description;
        this.usage = usage;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.arguments = arguments;

    }

    /**
     * Used to get the name of the command.
     *
     * @return The command name.
     */

    public String getName() {

        return name;

    }

    /**
     * Used to set the name of the command.
     *
     * @param name The command name.
     */

    public void setName(final String name) {

        this.name = name;

    }

    /**
     * Used to get the sub commands of the command.
     *
     * @return The sub commands array.
     */

    public SubCommand[] getSubCommands() {

        return subCommands;

    }

    /**
     * Used to set the sub commands of the command.
     *
     * @param subCommands The sub commands array.
     */

    public void setSubCommands(final SubCommand[] subCommands) {

        this.subCommands = subCommands;

    }

    /**
     * Used to get the description of the command.
     *
     * @return The description.
     */

    public String getDescription() {

        return description;

    }

    /**
     * Used to set the description of the command.
     *
     * @param description The description.
     */

    public void setDescription(final String description) {

        this.description = description;

    }

    /**
     * Used to get the usage of the command.
     *
     * @return The usage.
     */

    public String getUsage() {

        return usage;

    }

    /**
     * Used to set the usage of the command.
     *
     * @param usage The usage.
     */

    public void setUsage(final String usage) {

        this.usage = usage;

    }

    /**
     * Used to get the minimum count of arguments.
     *
     * @return The minimum argument count.
     */

    public int getMinArgs() {

        return minArgs;

    }

    /**
     * Used to set the minimum count of arguments.
     *
     * @param minArgs The minimum argument count.
     */

    public void setMinArgs(final int minArgs) {

        this.minArgs = minArgs;

    }

    /**
     * Used to get the maximum count of arguments.
     *
     * @return The maximum argument count.
     */

    public int getMaxArgs() {

        return maxArgs;

    }

    /**
     * Used to set the maximum count of arguments.
     *
     * @param maxArgs The maximum argument count.
     */

    public void setMaxArgs(final int maxArgs) {

        this.maxArgs = maxArgs;

    }

    /**
     * Used to get the command arguments.
     *
     * @return The command arguments.
     */

    public CommandArgument[] getArguments() {

        return arguments;

    }

    /**
     * Used to set the command arguments.
     *
     * @param arguments The command arguments.
     */

    public void setArguments(final CommandArgument[] arguments) {

        this.arguments = arguments;

    }

    public static class Builder implements FluentBuilder<CommandOptions> {

        private final CommandOptions commandOptions = new CommandOptions();

        public Builder name(final String name) {

            commandOptions.setName(name);
            return this;

        }

        public Builder subCommands(SubCommand... subNames) {

            commandOptions.setSubCommands(subNames);
            return this;

        }

        public Builder description(final String description) {

            commandOptions.setDescription(description);
            return this;

        }

        public Builder usage(final String usage) {

            commandOptions.setUsage(usage);
            return this;

        }

        public Builder minArgs(final int minArgs) {

            commandOptions.setMinArgs(minArgs);
            return this;

        }

        public Builder maxArgs(final int maxArgs) {

            commandOptions.setMaxArgs(maxArgs);
            return this;

        }

        public Builder arguments(CommandArgument... arguments) {

            commandOptions.setArguments(arguments);
            return this;

        }

        public CommandOptions build() {

            return commandOptions;

        }

    }

}