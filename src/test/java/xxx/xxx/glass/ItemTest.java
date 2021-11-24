package xxx.xxx.glass;

import xxx.xxx.glass.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ItemTest {

    private final static Logger LOGGER = Logger.getLogger("test");

    @Test
    public void testItemToJsonSerialization() {

        final ItemStack item = new ItemStack(Material.AIR);
        final String serializedItem = ItemUtils.itemToJson(item);

        assertEquals("{id:\"minecraft:air\",Count:1b,Damage:0s}", serializedItem);

    }

}
