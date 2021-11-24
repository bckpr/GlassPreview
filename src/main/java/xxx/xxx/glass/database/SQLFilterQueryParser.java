package xxx.xxx.glass.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Query parser for SQL databases, not fully implemented since SQL support isn't necessary at the moment.
 */

@Deprecated
public class SQLFilterQueryParser extends FilterQueryParser<PreparedStatement, Connection> {

    {

    }

    private final StringBuilder builder = new StringBuilder();
    private final Map<Integer, Object> values = new HashMap<>();

    public SQLFilterQueryParser(final String startQuery) {

        builder.append(startQuery);

    }

    /**
     * Used to parse a user provided input string to a valid database query.
     *
     * @param workObj The object to work on.
     * @param input   The input data.
     * @return The output object.
     */

    @Override
    public PreparedStatement parse(final Connection workObj, final String input) {

        final Matcher matcher = PART_PATTERN.matcher(input);
        while (matcher.find()) {
            final String identifier = matcher.group(1);
            final Function<String, Boolean> function = getParser(identifier);
            if (function == null) return null;
            final boolean success = function.apply(matcher.group(2));
            if (!success) return null;
        }

        PreparedStatement statement;

        try {
            statement = workObj.prepareStatement(builder.toString().substring(0, builder.length() - 5));
            for (final Map.Entry<Integer, Object> entry : values.entrySet())
                statement.setObject(entry.getKey(), entry.getValue());
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return null;
        }

        return statement;

    }

}
