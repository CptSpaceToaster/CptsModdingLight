package coloredlightscore.src.helper;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/*
 * An odd mess to gain control over the load of the lightmap into GL11...
 * 
 * 
 */
public class DynamicTextureHelper {
	
	public static void updateDynamicTexture(DynamicTexture instance)
    {
        uploadTexture(instance.getGlTextureId(), instance.getTextureData(), instance.width, instance.height);
    }
	
	public static void uploadTexture(int par0, int[] par1ArrayOfInteger, int par2, int par3)
    {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, par0);
        uploadTextureSub(0, par1ArrayOfInteger, par2, par3, 0, 0, false, false, false);
    }
	
	private static void uploadTextureSub(int target, int[] data, int width, int p_147947_3_, int level, int xoffsetInit, boolean p_147947_6_, boolean p_147947_7_, boolean isMoreThanOnePixel)
    {	
        int j1 = 4194304 / width;
        TextureUtil.func_147954_b(p_147947_6_, isMoreThanOnePixel);
        TextureUtil.setTextureClamped(p_147947_7_);
        int height;
        
        for (int k1 = 0; k1 < width * p_147947_3_; k1 += width * height)
        {
            int additionalXOffset = k1 / width;
            height = Math.min(j1, p_147947_3_ - additionalXOffset);
            int j2 = width * height;
            TextureUtil.copyToBufferPos(data, k1, j2);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, target, level, xoffsetInit + additionalXOffset, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, TextureUtil.dataBuffer);
        }
    }
}
