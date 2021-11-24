package xxx.xxx.glass.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Provides utility method for working with colors.
 */

public class ColorUtils {

    /**
     * Used to get a R-G color based on a provided level.
     *
     * @param level The input level.
     * @return The color created based on the level.
     */

    @NotNull
    public static Color getColorByLevel(final double level) {

        final double hue = level * 0.4;
        final double saturation = 1.0;
        final double brightness = 1.0;

        return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);

    }

    /**
     * Used to convert a java.awt.Color instance to a discord4j.rest.util.Color instance.
     *
     * @param color The input color.
     * @return The converted output color.
     * @see java.awt.Color
     * @see discord4j.rest.util.Color
     */

    @NotNull
    public static discord4j.rest.util.Color convertColor(final Color color) {

        return discord4j.rest.util.Color.of(color.getRGB());

    }

}
