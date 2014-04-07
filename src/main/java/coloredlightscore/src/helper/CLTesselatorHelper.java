package coloredlightscore.src.helper;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class CLTesselatorHelper {

	private static int nativeBufferSize = 0x200000;
	public static float sunlightBrightness = 1.0f;
		
	public CLTesselatorHelper() {
		
	}
	
	// Mock-up to get ASM for replacement method
    public static void setBrightness(Tessellator instance, int par1)
    {
    	int r;
    	int g;
    	int b;
    	int sunlight;
    	
    	// Incoming brightness value is in form 0000SSSSRRRRGGGGBBBBLLLL
    	// Convert this to..................... 00000000SSSSRRRRGGGGBBBB
    	
    	instance.hasBrightness = true;    	
    	instance.brightness = par1 >> 4; // Get rid of torch lighting
//    	
//    	instance.brightness = par1&15728880; // Strip out RGB components so mojang light map works
//    	
//	    if((par1 & 1048320) >0)
//	    {
//	    	// RGB bits are non-zero
//	    	// Extract each channel, and add sunlight as needed
//	    	// Sunlight component of par1 specifies how strong sunlight should be applied to current block
//	    	// THIS IS NOT THE TIME OF DAY
//	    	// Sun intensity is set on sunlightBrightness via CLEntityRendererHelper.updateLightmap
//	    	
//	    	sunlight = (par1 >> 20); // Extract SSSS component from brightness
//	    	sunlight = (int)Math.floor((float)sunlight * sunlightBrightness);
//	    	
//	    	if (sunlight > 15)
//	    		sunlight = 15;
//	    	else if (sunlight < 0)
//	    		sunlight = 0;
//	    	
//	    	r = ((par1 >> 8) & 15) + sunlight;
//	    	g = ((par1 >> 12) & 15) + sunlight;
//	    	b = ((par1 >> 16) & 15) + sunlight;
//	    	
//	    	if (r > 15)
//	    		r = 15;
//
//	    	if (g > 15)
//	    		g = 15;
//
//	    	if (b > 15)
//	    		b = 15;
//	    	
//	    	// Scale 0..15 to 0..240
//	    	r *= 16 + 15;
//	    	g *= 16 + 15;
//	    	b *= 16 + 15;
//	    		    	
//	    	instance.setColorOpaque(r, g, b);
//	    }

    }	

    public static int draw(Tessellator instance)
    {
        if (!instance.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
        	instance.isDrawing = false;

            int offs = 0;
            while (offs < instance.vertexCount)
            {
                int vtc = Math.min(instance.vertexCount - offs, nativeBufferSize >> 5);
                Tessellator.intBuffer.clear();
                Tessellator.intBuffer.put(instance.rawBuffer, offs * 8, vtc * 8);
                Tessellator.byteBuffer.position(0);
                Tessellator.byteBuffer.limit(vtc * 32);
                offs += vtc;

                if (instance.hasTexture)
                {
                    Tessellator.floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 32, Tessellator.floatBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (instance.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit); // GL13.glClientActiveTexture(par0);
                	//OpenGlHelper.setClientActiveTexture(CLEntityRendererHelper.redLightTexture);
                    Tessellator.shortBuffer.position(14);
                    GL11.glTexCoordPointer(2, 32, Tessellator.shortBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);  // GL13.glClientActiveTexture(par0);
                }

                if (instance.hasColor)
                {
                    Tessellator.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, Tessellator.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (instance.hasNormals)
                {
                    Tessellator.byteBuffer.position(24);
                    GL11.glNormalPointer(32, Tessellator.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                Tessellator.floatBuffer.position(0);
                GL11.glVertexPointer(3, 32, Tessellator.floatBuffer);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDrawArrays(instance.drawMode, 0, vtc);
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (instance.hasTexture)
                {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (instance.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                	//OpenGlHelper.setClientActiveTexture(CLEntityRendererHelper.redLightTexture);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (instance.hasColor)
                {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (instance.hasNormals)
                {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            if (instance.rawBufferSize > 0x20000 && instance.rawBufferIndex < (instance.rawBufferSize << 3))
            {
            	instance.rawBufferSize = 0x10000;
            	instance.rawBuffer = new int[instance.rawBufferSize];
            }

            int i = instance.rawBufferIndex * 4;
            instance.reset();
            return i;
        }
    }
}
