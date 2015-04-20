package coloredlightscore.src.api;

import coloredlightscore.src.helper.CLEntityRendererHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.world.World;

/**
 * Public API for ColoredLightsCore
 * 
 * @author CptSpaceToaster
 *
 */
public class CLApi {
    public static float l[] = new float[] { 0F, 1F / 15, 2F / 15, 3F / 15, 4F / 15, 5F / 15, 6F / 15, 7F / 15, 8F / 15, 9F / 15, 10F / 15, 11F / 15, 12F / 15, 13F / 15, 14F / 15, 1F };

    public static int r[] = new int[] { 0, 15, 0, 8, 0, 10, 0, 10, 5, 15, 8, 15, 0, 15, 15, 15 };
    public static int g[] = new int[] { 0, 0, 15, 3, 0, 0, 15, 10, 5, 10, 15, 15, 8, 0, 12, 15 };
    public static int b[] = new int[] { 0, 0, 0, 0, 15, 15, 15, 10, 5, 13, 0, 0, 15, 15, 10, 15 };

    /**
     * Computes a 20-bit lighting word, containing red, green, blue values.
     * Automatically computes the Minecraft brightness value using the brightest of the r, g and b channels.
     * This value can be used directly for Block.lightValue 
     * 
     * Word format: 0BBBB 0GGGG 0RRRR 0LLLL
     * 
     * @param r Red intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param g Green intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param b Blue intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @return Integer describing RGBL value for a block
     */
    public static int makeRGBLightValue(float r, float g, float b) {
        // Clamp color channels
        if (r < 0.0f)
            r = 0.0f;
        else if (r > 1.0f)
            r = 1.0f;

        if (g < 0.0f)
            g = 0.0f;
        else if (g > 1.0f)
            g = 1.0f;

        if (b < 0.0f)
            b = 0.0f;
        else if (b > 1.0f)
            b = 1.0f;

        int brightness = (int) (15.0f * Math.max(Math.max(r, g), b));
        return brightness | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
    }

    /**
     * Computes a 20-bit lighting word, containing red, green, blue values.
     * Automatically computes the Minecraft brightness value using the brightest of the r, g and b channels.
     * This value can be used directly for Block.lightValue 
     * 
     * Word format: 0BBBB 0GGGG 0RRRR 0LLLL
     * 
     * @param r Red intensity, 0 to 15. Resolution is 4 bits.
     * @param g Green intensity, 0 to 15. Resolution is 4 bits.
     * @param b Blue intensity, 0 to 15. Resolution is 4 bits.
     * @return Integer describing RGBL value for a block
     */
    public static int makeRGBLightValue(int r, int g, int b) {
        // Clamp color channels
        if (r < 0)
            r = 0;
        else if (r > 15)
            r = 15;

        if (g < 0)
            g = 0;
        else if (g > 15)
            g = 15;

        if (b < 0)
            b = 0;
        else if (b > 15)
            b = 15;

        int brightness = Math.max(Math.max(r, g), b);
        return brightness | ((b << 15) + (g << 10) + (r << 5));
    }

    /**
     * It is not recommended that you mess with lightValue yourself
     *
     * Computes a 20-bit lighting word, containing red, green, blue, and brightness values.
     * Allows overriding of the Minecraft brightness value, This may cause unexpected behavior.
     * This value can be used directly for Block.lightValue
     *
     * Word format: 0BBBB 0GGGG 0RRRR 0LLLL
     *
     * @param r Red intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param g Green intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param b Blue intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param brightness The new lightValue of a block. Only the lower-most 4 bits of this parameter are used.
     * @return Integer describing RGBL value for a block
     */
    @Deprecated
    public static int makeRGBLightValue(float r, float g, float b, float brightness) {
        // Clamp color channels
        if (r < 0.0f)
            r = 0.0f;
        else if (r > 1.0f)
            r = 1.0f;

        if (g < 0.0f)
            g = 0.0f;
        else if (g > 1.0f)
            g = 1.0f;

        if (b < 0.0f)
            b = 0.0f;
        else if (b > 1.0f)
            b = 1.0f;

        int vanilla_brightness = (int) (brightness * 15.0f);
        vanilla_brightness &= 0xf;

        return vanilla_brightness | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
    }

    /**
     * It is not recommended that you mess with lightValue yourself
     *
     * Computes a 20-bit lighting word, containing red, green, blue, and brightness values.
     * Allows overriding of the Minecraft brightness value, This may cause unexpected behavior.
     * This value can be used directly for Block.lightValue
     *
     * Word format: 0BBBB 0GGGG 0RRRR 0LLLL
     *
     * @param r Red intensity, 0 to 15. Resolution is 4 bits.
     * @param g Green intensity, 0 to 15. Resolution is 4 bits.
     * @param b Blue intensity, 0 to 15. Resolution is 4 bits.
     * @param brightness The new lightValue of a block. Only the lower-most 4 bits of this parameter are used.
     * @return Integer describing RGBL value for a block
     */
    @Deprecated
    public static int makeRGBLightValue(int r, int g, int b, int brightness) {
        // Clamp color channels
        if (r < 0)
            r = 0;
        else if (r > 15)
            r = 15;

        if (g < 0)
            g = 0;
        else if (g > 15)
            g = 15;

        if (b < 0)
            b = 0;
        else if (b > 15)
            b = 15;

        brightness &= 0xf;

        return brightness | ((b << 15) + (g << 10) + (r << 5));
    }

    /**
     * Sets the lighting colors for a given block. Vanilla brightness is recomputed.
     *
     * @param block The block to set color on
     * @param r Red intensity, 0 to 15. Resolution is 4 bits.
     * @param g Green intensity, 0 to 15. Resolution is 4 bits.
     * @param b Blue intensity, 0 to 15. Resolution is 4 bits.
     * @return Reference to the block passed in.
     */
    public static Block setBlockColorRGB(Block block, int r, int g, int b) {
        // The default vanilla method will just multiply by 15. So we reverse it here.
        block.setLightLevel(((float)makeRGBLightValue(r, g, b))/15F);
        return block;
    }

    /**
     * Sets the lighting colors for a given block. Vanilla brightness is recomputed.
     *
     * @param block The block to set color on
     * @param r Red intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param g Green intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param b Blue intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @return Reference to the block passed in.
     */
    public static Block setBlockColorRGB(Block block, float r, float g, float b) {
        // The default vanilla method will just multiply by 15. So we reverse it here.
        block.setLightLevel(((float)makeRGBLightValue(r, g, b))/15F);
        return block;
    }

    /**
     * It is not recommended that you mess with brightness yourself
     *
     * Sets the lighting colors for a given block.
     *
     * @param block The block to set color on
     * @param r Red intensity, 0 to 15. Resolution is 4 bits.
     * @param g Green intensity, 0 to 15. Resolution is 4 bits.
     * @param b Blue intensity, 0 to 15. Resolution is 4 bits.
     * @param brightness The Minecraft brightness to set for the block, 0 to 15.
     * @return Reference to the block passed in.
     */
    @Deprecated
    public static Block setBlockColorRGB(Block block, int r, int g, int b, int brightness) {
        // The default vanilla method will just multiply by 15. So we reverse it here.
        block.setLightLevel(((float)makeRGBLightValue(r, g, b, brightness))/15F);
        return block;
    }

    /**
     * It is not recommended that you mess with brightness yourself
     *
     * Sets the lighting colors for a given block.
     *
     * @param block The block to set color on
     * @param r Red intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param g Green intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param b Blue intensity, 0.0f to 1.0f. Resolution is 4 bits.
     * @param brightness The Minecraft brightness to set for the block, 0.0f to 1.0f.
     * @return Reference to the block passed in.
     */
    @Deprecated
    public static Block setBlockColorRGB(Block block, float r, float g, float b, float brightness) {
        // The default vanilla method will just multiply by 15. So we reverse it here.
        block.setLightLevel(((float)makeRGBLightValue(r, g, b, brightness))/15F);
        return block;
    }

    /**
     * Combine two lighting words, by choosing the brightest component for each color channel
     * @param a Light A
     * @param b Light B
     * @return The combined light from A and B
     */
    public static int combine(int a, int b) {
        int a_lgh = a & 0x0000F;
        int a_red = a & 0x001E0;
        int a_grn = a & 0x03C00;
        int a_blu = a & 0x78000;

        int b_lgh = b & 0x0000F;
        int b_red = b & 0x001E0;
        int b_grn = b & 0x03C00;
        int b_blu = b & 0x78000;

        return (Math.max(a_blu, b_blu) | Math.max(a_grn, b_grn) | Math.max(a_red, b_red) | Math.max(a_lgh, b_lgh));
    }

    /**
     * Sets the minimum brightness value for the world. This is best observed within
     * a completely dark cave... higher values mean brighter caves.
     *
     * @param brightness A value between 0.0 and 1.0 for the brightness. Vanilla uses 0.05.
     */
    public static void setWorldMinimumBrightness(float brightness) {

        if (brightness < 0.0f)
            brightness = 0.0f;
        else if (brightness >= 1.0f)
            brightness = 1.0f;

        CLEntityRendererHelper.minLightLevel = brightness;
    }

    /**
     * Sets the maximum brightness value for the world. i.e. broad daylight.
     *
     * @param brightness A value between 0.0 and 1.0 for the brightness. Vanilla uses 1.0.
     */
    public static void setWorldMaximumBrightness(float brightness) {

        if (brightness < 0.0f)
            brightness = 0.0f;
        else if (brightness >= 1.0f)
            brightness = 1.0f;

        CLEntityRendererHelper.maxLightLevel = brightness;
    }

    /**
     * Sets the color of moonlight. This really should be dimmer than sunlight, or
     * weird things may happen. A lightmap update will be required after setting this.
     *
     * @param r Amount of red, 0.0 - 1.0
     * @param g Amount of green, 0.0 - 1.0
     * @param b Amount of blue, 0.0 - 1.0
     */
    public static boolean setWorldMoonlightColor(float r, float g, float b) {

        WorldClient worldclient = Minecraft.getMinecraft().theWorld;

        if (worldclient != null) {

            // Clamp RGB values to 0.0-1.0
            worldclient.clMoonColor[0] = Math.min(Math.max(r, 0.0f), 1.0f);
            worldclient.clMoonColor[1] = Math.min(Math.max(g, 0.0f), 1.0f);
            worldclient.clMoonColor[2] = Math.min(Math.max(b, 0.0f), 1.0f);

            return true;
        }

        return false;
    }

    /**
     * Sets the color of normal daylight
     *
     * @param r Amount of red, 0.0 - 1.0
     * @param g Amount of green, 0.0 - 1.0
     * @param b Amount of blue, 0.0 - 1.0
     */
    public static boolean setWorldDaylightColor(float r, float g, float b) {

        WorldClient worldclient = Minecraft.getMinecraft().theWorld;

        if (worldclient != null) {
            // Clamp RGB values to 0.0-1.0
            worldclient.clSunColor[0] = Math.min(Math.max(r, 0.0f), 1.0f);
            worldclient.clSunColor[1] = Math.min(Math.max(g, 0.0f), 1.0f);
            worldclient.clSunColor[2] = Math.min(Math.max(b, 0.0f), 1.0f);

            return true;
        }

        return false;
    }
}
