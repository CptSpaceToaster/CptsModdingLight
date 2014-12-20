package coloredlightscore.src.helper;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * Created by Murray on 12/9/2014.
 */
public class CLFontRendererHelper {
    public static boolean optifineUpInThisFontRenderer = false;

    public static float renderDefaultChar(FontRenderer instance, int c, boolean italics) {
        float tx = (float)(c % 16 * 8);
        float ty = (float)(c / 16 * 8);
        float slant = italics ? 1.0F : 0.0F;
        instance.renderEngine.bindTexture(instance.locationFontTexture);
        float width;
        if (optifineUpInThisFontRenderer) {
            width = instance.d[c] - 0.01F;
        } else {
            width = (float)instance.charWidth[c] - 0.01F;
        }
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
        tessellator.addVertexWithUV(instance.posX + slant, instance.posY, 0.0F, tx / 128.0F, ty / 128.0F);
        tessellator.addVertexWithUV(instance.posX - slant, instance.posY + 7.99F, 0.0F, tx / 128.0F, (ty + 7.99F) / 128.0F);
        tessellator.addVertexWithUV(instance.posX + width - 1.0F + slant, instance.posY, 0.0F, (tx + width - 1.0F) / 128.0F, ty / 128.0F);
        tessellator.addVertexWithUV(instance.posX + width - 1.0F - slant, instance.posY + 7.99F, 0.0F, (tx + width - 1.0F) / 128.0F, (ty + 7.99F) / 128.0F);
        tessellator.draw();
        return optifineUpInThisFontRenderer ? instance.d[c] : (float)instance.charWidth[c];
    }

    public static float renderUnicodeChar(FontRenderer instance, char c, boolean flag) {
        if (instance.glyphWidth[c] == 0)
        {
            return 0.0F;
        }
        else
        {
            int i = c / 256;
            instance.loadGlyphTexture(i);
            int j = instance.glyphWidth[c] >>> 4;
            int k = instance.glyphWidth[c] & 15;
            float f = (float)j;
            float f1 = (float)(k + 1);
            float f2 = (float)(c % 16 * 16) + f;
            float f3 = (float)((c & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = flag ? 1.0F : 0.0F;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
            tessellator.addVertexWithUV(instance.posX + f5, instance.posY, 0.0F, f2 / 256.0F, f3 / 256.0F);
            tessellator.addVertexWithUV(instance.posX - f5, instance.posY + 7.99F, 0.0F, f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            tessellator.addVertexWithUV(instance.posX + f4 / 2.0F + f5, instance.posY, 0.0F, (f2 + f4) / 256.0F, f3 / 256.0F);
            tessellator.addVertexWithUV(instance.posX + f4 / 2.0F - f5, instance.posY + 7.99F, 0.0F, (f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            tessellator.draw();
            return (f1 - f) / 2.0F + 1.0F;
        }
    }
}
