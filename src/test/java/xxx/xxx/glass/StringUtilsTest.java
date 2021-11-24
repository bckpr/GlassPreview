package xxx.xxx.glass;

import xxx.xxx.glass.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testHashing() {

        assertEquals("098f6bcd4621d373cade4e832627b4f6", StringUtils.hash("test"));

    }

}
