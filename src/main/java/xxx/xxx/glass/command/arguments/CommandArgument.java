package xxx.xxx.glass.command.arguments;

import xxx.xxx.glass.command.arguments.validators.ArgumentValidator;

/**
 * Argument that can be used by command implementations.
 */

public class CommandArgument {

    private final String name;
    private final String description;
    private final ArgumentValidator validator;

    public CommandArgument(final String name, final String description, final ArgumentValidator validator) {

        this.name = name;
        this.description = description;
        this.validator = validator;

    }

    /**
     * Used to get the name of the argument.
     *
     * @return The name.
     */

    public String getName() {

        return name;

    }

    /**
     * Used to get the description of the argument.
     *
     * @return The description.
     */

    public String getDescription() {

        return description;

    }

    /**
     * Used to get the validator of the argument.
     *
     * @return The validator.
     */

    public ArgumentValidator getValidator() {

        return validator;

    }

}
