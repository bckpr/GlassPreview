package xxx.xxx.glass;

import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.data.entry.Action;
import xxx.xxx.glass.data.entry.PickupItemEntry;
import xxx.xxx.glass.database.MongoFilterQueryParser;
import xxx.xxx.glass.utils.ItemUtils;
import net.minecraft.server.v1_12_R1.DispenserRegistry;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    private final static Logger LOGGER = Logger.getLogger("test");

    static {

        DispenserRegistry.c();

    }

    @Test
    public void testPickupItemEntryParser() {

        final UUID uuid = UUID.randomUUID();
        final UUID userUUID = UUID.randomUUID();
        final long timestamp = System.currentTimeMillis();

        final Document document = new Document()
                .append("uuid", uuid)
                .append("user", new Document()
                        .append("username", "Notch")
                        .append("uuid", userUUID))
                .append("action", Action.PICKUP_ITEM.name())
                .append("timestamp", timestamp)
                .append("position", new Document()
                        .append("world", "world")
                        .append("x", 100)
                        .append("y", 10)
                        .append("z", 100))
                .append("identifier", Material.STONE.name())
                .append("item", ItemUtils.itemToJson(new ItemStack(Material.AIR)))
                .append("amount", 1);

        final PickupItemEntry pickupItemEntry = new PickupItemEntry.Parser().parse(document);

        assertNotNull(pickupItemEntry);
        assertEquals(uuid, pickupItemEntry.getUniqueId());
        assertEquals("Notch", pickupItemEntry.getUser().getUsername());
        assertEquals(userUUID, pickupItemEntry.getUser().getUniqueId());
        assertEquals(Action.PICKUP_ITEM, pickupItemEntry.getAction());
        assertEquals(timestamp, pickupItemEntry.getTimestamp());
        assertEquals(new Position("world", 100, 10, 100), pickupItemEntry.getPosition());
        assertEquals("STONE", pickupItemEntry.getIdentifier());
        assertEquals(Material.AIR, pickupItemEntry.getItem().getType());
        assertEquals(1, pickupItemEntry.getAmount());

    }

    @Test
    public void testMongoQueryParser() {

        final String query = "username=Notch x=50#100 y=10 z=50#100";
        final Document document = new MongoFilterQueryParser().parse(new Document(), query);

        assertNotNull(document);
        assertEquals("{\"user.username\": \"Notch\", \"position.x\": {\"$gte\": 50, \"$lte\": 100}, \"position.y\": 10, \"position.z\": {\"$gte\": 50, \"$lte\": 100}}", document.toJson());

    }

}
