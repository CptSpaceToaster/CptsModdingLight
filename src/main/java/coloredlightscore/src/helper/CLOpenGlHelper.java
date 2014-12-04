package coloredlightscore.src.helper;

import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL20;

/**
 * Created by Murray on 11/30/2014.
 */
public class CLOpenGlHelper {
    public static void setLightmapTextureCoords(int textureId, float x, float y) {
        if (CLTessellatorHelper.isProgramInUse()) {
            int brightness = ((int) y << 16) + (int) x;
            /*
                brightness is of the form
                0000 0000 SSSS BBBB GGGG RRRR LLLL 0000
                and needs to be decomposed.
             */
            int s = brightness >> 20 & 0xF;
            int b = brightness >> 16 & 0xF;
            int g = brightness >> 12 & 0xF;
            int r = brightness >> 8 & 0xF;
            int l = brightness >> 4 & 0xF;
            if (l > r && l > g && l > b) {
                r = g = b = l;
            }

            GL20.glUniform4i(CLTessellatorHelper.lightCoordUniform, r, g, b, s);
        } // else noop; why is this ever called if enableLightmap hasn't been called?

        OpenGlHelper.lastBrightnessX = x;
        OpenGlHelper.lastBrightnessY = y;
    }
}
