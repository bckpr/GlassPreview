package xxx.xxx.glass;

import xxx.xxx.glass.common.Position;
import xxx.xxx.glass.data.entry.Action;
import xxx.xxx.glass.data.entry.ContainerModificationEntry;
import xxx.xxx.glass.data.entry.SubAction;
import xxx.xxx.glass.data.entry.User;
import net.minecraft.server.v1_12_R1.DispenserRegistry;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class DataExportTest {

    private final static Logger LOGGER = Logger.getLogger("test");

    static {

        DispenserRegistry.c();

    }

    @Test
    public void testContainerModificationEntryExport() {

        final UUID uuid = UUID.randomUUID();
        final UUID userUUID = UUID.randomUUID();
        final long timestamp = System.currentTimeMillis();

        final ContainerModificationEntry entry = new ContainerModificationEntry(
                uuid,
                new User("Notch", userUUID),
                timestamp,
                new Position("world", 100, 10, 100),
                "STONE",
                SubAction.ADD_ITEM,
                InventoryType.CHEST,
                new ItemStack(Material.AIR),
                1
        );

        final Document document = new Document();
        entry.exportData(document);
        assertEquals(uuid, document.get("uuid", UUID.class));

        final Document userDocument = document.get("user", Document.class);
        assertEquals("Notch", userDocument.getString("username"));
        assertEquals(userUUID, userDocument.get("uuid", UUID.class));

        assertEquals(Action.CONTAINER_MODIFICATION.name(), document.getString("action"));
        assertEquals(timestamp, document.get("timestamp"));

        final Document positionDocument = document.get("position", Document.class);
        assertEquals("world", positionDocument.getString("world"));
        assertEquals(100, positionDocument.getInteger("x", -1));
        assertEquals(10, positionDocument.getInteger("y", -1));
        assertEquals(100, positionDocument.getInteger("z", -1));

        assertEquals("STONE", document.getString("identifier"));
        assertEquals(SubAction.ADD_ITEM.name(), document.getString("subAction"));
        assertEquals(InventoryType.CHEST.name(), document.getString("invType"));
        assertEquals("{id:\"minecraft:air\",Count:1b,Damage:0s}", document.getString("item"));
        assertEquals(1, document.getInteger("amount", -1));

    }

}
