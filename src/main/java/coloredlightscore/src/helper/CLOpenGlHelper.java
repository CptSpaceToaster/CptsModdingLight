package coloredlightscore.src.helper;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

/**
 * Created by Murray on 11/30/2014.
 */
public class CLOpenGlHelper {
    public static void setLightmapTextureCoords(int textureId, float x, float y) {
        int brightness = (int)y * 65536 + (int)x;
        /*
            brightness is of the form
            0000 0000 SSSS BBBB GGGG RRRR LLLL 0000
            and needs to be decomposed.
         */
        byte s = (byte)(brightness >> 20 & 0xF);
        byte b = (byte)(brightness >> 16 & 0xF);
        byte g = (byte)(brightness >> 12 & 0xF);
        byte r = (byte)(brightness >> 8 & 0xF);
        byte l = (byte)(brightness >> 4 & 0xF);
        if (l > r && l > g && l > b) {
            r = g = b = l;
        }

        GL20.glUniform4i(CLTessellatorHelper.lightCoordUniform, r, g, b, s);
        //System.out.println(s + ", " + b + ", " + g + ", " + r);

        OpenGlHelper.lastBrightnessX = x;
        OpenGlHelper.lastBrightnessY = y;
    }
}
