package xxx.xxx.glass.database;

import xxx.xxx.glass.common.serialization.StringSerializer;
import xxx.xxx.glass.wrapped.WrappedMaterialData;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Used to parse a string input into a valid mongo query.
 */

public class MongoFilterQueryParser extends FilterQueryParser<Document, Document> {

    {
        registerParser("_id", this::parseId);
        registerParser("uuid", this::parseUniqueId);
        registerParser("username", this::parseUsername);
        registerParser("userUuid", this::parseUserUniqueId);
        registerParser("action", this::parseAction);
        registerParser("timeSpan", this::parseTimeSpan);
        registerParser("world", this::parseWorld);
        registerParser("x", this::parseX);
        registerParser("y", this::parseY);
        registerParser("z", this::parseZ);
        registerParser("identifier", this::parseIdentifier);
        registerParser("item", this::parseItem);
        registerParser("amount", this::parseAmount);
        registerParser("invType", this::parseInventoryType);
        registerParser("invTitle", this::parseInventoryTitle);
        registerParser("addedItems", this::parseAddedItems);
        registerParser("removedItems", this::parseRemovedItems);
        registerParser("message", this::parseMessage);
        registerParser("blockType", this::parseBlockType);
        registerParser("xp", this::parseXp);
        registerParser("oldBlockType", this::parseOldBlockType);
        registerParser("newBlockType", this::parseNewBlockType);
        registerParser("command", this::parseCommand);
        registerParser("ip", this::parseIp);
    }

    private Document document;

    /**
     * Used to parse a user provided input string to a valid database query.
     *
     * @param workObj The object to work on.
     * @param input   The input data.
     * @return The output object.
     */

    @Override
    public Document parse(final Document workObj, final String input) {

        this.document = workObj;

        int matched = 0;
        final Matcher matcher = PART_PATTERN.matcher(input);
        while (matcher.find()) {
            final String identifier = matcher.group(1);
            final Function<String, Boolean> function = getParser(identifier);
            if (function == null) return null;
            final boolean success = function.apply(matcher.group(2));
            if (!success) return null;
            matched++;
        }

        if (matched == 0) return null;

        return document;

    }

    /**
     * Used to parse the provided input to an ObjectId and
     * appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseId(final String input) {

        document.append("_id", new ObjectId(input));

        return true;

    }

    /**
     * Used to parse the provided input to an UUID and
     * appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseUniqueId(final String input) {

        document.append("uuid", UUID.fromString(input));

        return true;

    }

    /**
     * Used to parse the provided input to an username
     * and appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseUsername(final String input) {

        if (document.containsKey("user")) document.get("user", Document.class).append("username", input);
        else document.append("user.username", input);

        return true;

    }

    /**
     * Used to parse the provided input to an user UUID
     * and appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseUserUniqueId(final String input) {

        if (document.containsKey("user")) document.get("user", Document.class).append("uuid", UUID.fromString(input));
        else document.append("user.uuid", UUID.fromString(input));

        return true;

    }

    /**
     * Used to parse the provided input into an Action
     * and appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseAction(final String input) {

        document.append("action", input.toUpperCase());

        return true;

    }

    /**
     * Used to parse the provided input into a TimeSpan
     * and appends it to the query document. It currently
     * only supports the "yyyy-MM-dd'T'HH:mm:ss" format.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked, <code>false</code> otherwise.
     */

    private boolean parseTimeSpan(final String input) {

        try {
            final String[] parts = input.split("#");
            if (parts.length < 2) return false;
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            final Date start = dateFormat.parse(parts[0]);
            final Date stop = dateFormat.parse(parts[1]);
            document.append("timestamp", new Document("$gte", start.getTime()).append("$lte", stop.getTime()));
        } catch (final ParseException ex) {
            return false;
        }

        return true;

    }

    /**
     * Used to parse the provided input into a World
     * and appends it to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseWorld(final String input) {

        if (document.containsKey("position")) document.get("position", Document.class).append("world", input);
        else document.append("position.world", input);

        return true;

    }

    /**
     * Used to parse the provided input into a coordinate
     * part and appends it to the query document. A range
     * can be specified by separating two numbers with a
     * "#" symbol.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseX(final String input) {

        if (input.contains("#")) {
            final String[] parts = input.split("#");
            if (parts.length < 2) return false;
            return parseCoordinateRange("x", parts);
        } else {
            try {
                final int number = Integer.parseInt(input);
                if (document.containsKey("position")) document.get("position", Document.class).append("x", number);
                else document.append("position.x", number);
            } catch (final NumberFormatException ex) {
                return false;
            }
        }

        return true;

    }

    /**
     * Used to parse the provided input into a coordinate
     * part and appends it to the query document. A range
     * can be specified by separating two numbers with a
     * "#" symbol.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseY(final String input) {

        if (input.contains("#")) {
            final String[] parts = input.split("#");
            if (parts.length < 2) return false;
            return parseCoordinateRange("y", parts);
        } else {
            try {
                final int number = Integer.parseInt(input);
                if (document.containsKey("position")) document.get("position", Document.class).append("y", number);
                else document.append("position.y", number);
            } catch (final NumberFormatException ex) {
                return false;
            }
        }

        return true;

    }

    /**
     * Used to parse the provided input into a coordinate
     * part and appends it to the query document. A range
     * can be specified by separating two numbers with a
     * "#" symbol.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseZ(final String input) {

        if (input.contains("#")) {
            final String[] parts = input.split("#");
            if (parts.length < 2) return false;
            return parseCoordinateRange("z", parts);
        } else {
            try {
                final int number = Integer.parseInt(input);
                if (document.containsKey("position")) document.get("position", Document.class).append("z", number);
                else document.append("position.z", number);
            } catch (final NumberFormatException ex) {
                return false;
            }
        }

        return true;

    }

    /**
     * Used to parse the provided range coordinate part to
     * a valid coordinate range and appends it to the query
     * document.
     *
     * @param coordinate The input coordinate.
     * @param parts      The input value parts.
     * @return <code>true</code> if the parsing process worked, <code>false</code> otherwise.
     */

    private boolean parseCoordinateRange(final String coordinate, final String[] parts) {

        try {
            final int min = Integer.parseInt(parts[0]);
            final int max = Integer.parseInt(parts[1]);
            final Document filterDocument = new Document("$gte", min).append("$lte", max);
            if (document.containsKey("position"))
                document.get("position", Document.class).append(coordinate, filterDocument);
            else document.append("position." + coordinate, filterDocument);
        } catch (final NumberFormatException ex) {
            return false;
        }

        return true;

    }

    /**
     * Used to append the provided input as an identifier to
     * the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseIdentifier(final String input) {

        document.append("identifier", input);

        return true;

    }

    /**
     * Used to append the provided input as a regex string
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseItem(final String input) {

        document.append("item", new Document("$regex", String.format(".*%s.*", input)).append("$options", "i"));

        return true;

    }

    /**
     * Used to append the provided input as a exact value or
     * greater/small than range to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked, <code>false</code> otherwise.
     */

    private boolean parseAmount(String input) {

        String action = null;
        if (input.startsWith("<")) {
            action = "$lt";
            input = input.substring(1);
        } else if (input.startsWith(">")) {
            action = "$gt";
            input = input.substring(1);
        }

        try {
            final int amount = Integer.parseInt(input);
            if (action == null) document.append("amount", amount);
            else document.append("amount", new Document(action, amount));
        } catch (final NumberFormatException ex) {
            return false;
        }

        return true;

    }

    /**
     * Used to append the provided input as a message
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseMessage(final String input) {

        document.append("message", new Document("$regex", String.format(".*%s.*", input)).append("$options", "i"));

        return true;

    }

    /**
     * Used to append the provided input as a block type
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseBlockType(final String input) {

        document.append("blockType", StringSerializer.serializeUnsafe(WrappedMaterialData.fromString(input)));

        return true;

    }

    /**
     * Used to parse the provided input to a numeric
     * value and appends it as a xp value to the
     * query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked, <code>false</code> otherwise.
     */

    private boolean parseXp(final String input) {

        try {
            document.append("xp", Integer.parseInt(input));
            return true;
        } catch (final NumberFormatException ex) {
            return false;
        }

    }

    /**
     * Used to parse the provided input into a serialized
     * WrappedMaterialData and appends it as a old block
     * type to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseOldBlockType(final String input) {

        document.append("oldBlockType", StringSerializer.serializeUnsafe(WrappedMaterialData.fromString(input)));

        return true;

    }

    /**
     * Used to parse the provided input into a serialized
     * WrappedMaterialData and appends it as a new block
     * type to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseNewBlockType(final String input) {

        document.append("newBlockType", StringSerializer.serializeUnsafe(WrappedMaterialData.fromString(input)));

        return true;

    }

    /**
     * Used to append the provided input as a regex string
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseCommand(final String input) {

        document.append("command", new Document("$regex", String.format(".*%s.*", input)).append("$options", "i"));

        return true;

    }

    /**
     * Used to append the provided input as a hashed ip
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseIp(final String input) {

        document.append("hashedIp", input);

        return true;

    }

    /**
     * Used to append the provided input as an inventory
     * type to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseInventoryType(final String input) {

        document.append("invType", input.toUpperCase());

        return true;

    }

    /**
     * Used to append the provided input as an inventory
     * title to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseInventoryTitle(final String input) {

        document.append("invTitle", input);

        return true;

    }

    /**
     * Used to append the provided input as a regex string
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseAddedItems(final String input) {

        document.append("addedItems.item", new Document("$regex", String.format(".*%s.*", input)).append("$options", "i"));

        return true;

    }

    /**
     * Used to append the provided input as a regex string
     * to the query document.
     *
     * @param input The input.
     * @return <code>true</code> if the parsing process worked.
     */

    private boolean parseRemovedItems(final String input) {

        document.append("removedItems.item", new Document("$regex", String.format(".*%s.*", input)).append("$options", "i"));

        return true;

    }

}
