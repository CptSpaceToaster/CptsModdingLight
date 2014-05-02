	package coloredlightscore.src.helper;
	
	import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
	
	import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
	
	/*
	 * An odd mess to gain control over the load of the lightmap into GL11...
	 * 
	 */
	public class CLDynamicTextureHelper {
		
		public static void updateDynamicTexture(DynamicTexture instance)
	    {
	        uploadTexture(instance.getGlTextureId(), instance.getTextureData(), instance.width, instance.height);
	    }
		
		public static void uploadTexture(int par0, int[] data, int width, int height)
	    {
			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, par0);
			GL11.glBindTexture(GL12.GL_TEXTURE_3D, par0);
	        
			int level = 0;
			
			boolean p_147947_6_ = false;
			boolean isMoreThanOnePixel = false;
			boolean p_147947_7_ = false;
			
			
			TextureUtil.func_147954_b(p_147947_6_, isMoreThanOnePixel);
	        TextureUtil.setTextureClamped(p_147947_7_);
	        //So, we're just going to upload the texture in one lump sump
	        GL12.glTexSubImage3D(GL12.GL_TEXTURE_3D, level, 0, 0, 0, 16, 16, 16, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, TextureUtil.dataBuffer);
	    }
	}
	
	
