package xxx.xxx.glass;

import xxx.xxx.glass.utils.UUIDUtils;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ConversionTest {

    @Test
    public void convertB64ToUUID() {

        final UUID uuid = UUIDUtils.bsonB64ToUUID("2E7qXkKBTD4uzS7/rLLIng==");
        assertEquals("3e4c8142-5eea-4ed8-9ec8-b2acff2ecd2e", uuid.toString());

    }

}
