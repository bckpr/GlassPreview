package xxx.xxx.glass.command.commands;

import xxx.xxx.glass.common.FluentBuilder;

/**
 * A sub command for commands.
 */

public class SubCommand {

    private String name;
    private String description;

    private SubCommand() {

    }

    public SubCommand(final String name, final String description) {

        this.name = name;
        this.description = description;

    }

    /**
     * Used to get the name of the sub command.
     *
     * @return The sub command name.
     */

    public String getName() {

        return name;

    }

    /**
     * Used to set the name of the sub command.
     *
     * @param name The sub command name.
     */

    public void setName(final String name) {

        this.name = name;

    }

    /**
     * Used to get the description of the sub command.
     *
     * @return The sub command description.
     */

    public String getDescription() {

        return description;

    }

    /**
     * Used to set the description of the sub command.
     *
     * @param description The sub command description.
     */

    public void setDescription(final String description) {

        this.description = description;

    }

    public static class Builder implements FluentBuilder<SubCommand> {

        private final SubCommand subCommand = new SubCommand();

        public Builder name(final String name) {

            subCommand.setName(name);
            return this;

        }

        public Builder description(final String description) {

            subCommand.setDescription(description);
            return this;

        }

        public SubCommand build() {

            return subCommand;

        }

    }

}
