package xxx.xxx.glass;

import xxx.xxx.glass.utils.IPUtils;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class IPUtilsTest {

    private final static Logger LOGGER = Logger.getLogger("test");

    @Test
    public void testIpConversion() {

        String words = IPUtils.convertIpToWords("127.0.0.1");
        assertEquals("Tokmela-Rek-Rek-Tok", words);

        words = IPUtils.convertIpToWords("13.83.39.119");
        assertEquals("Tokmi-Enomi-Mogmo-Tokmalo", words);

        words = IPUtils.convertIpToWords("55.203.233.10");
        assertEquals("Fenmu-Legmule-Legmile-Tokmu", words);

        words = IPUtils.convertIpToWords("162.187.254.224");
        assertEquals("Tokmala-Tokmila-Legmuli-Legmeli", words);

    }

}
