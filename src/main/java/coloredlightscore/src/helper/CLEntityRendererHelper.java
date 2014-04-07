package coloredlightscore.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;

import org.lwjgl.opengl.GL11;

public class CLEntityRendererHelper {
		
	private static int lightMapTexId;
	
	public static void Initialize()
	{
		// Redefine daylight
	}
		    
	public static void updateLightmap(EntityRenderer er, float par1)
    {
        WorldClient worldclient = er.mc.theWorld;

        if (worldclient != null)
        {            
            CLTesselatorHelper.sunlightBrightness = worldclient.getSunBrightness(1.0F);
            float sunlightBase = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
            int ptr = 0;
            
			for (int s=0;s<16;s++)
				for (int r=0;r<16;r++)
            	{                    
	            	for (int g=0;g<16;g++)
	    	            for (int b=0;b<16;b++)
            			{
                    		float sunlight = 0;//worldclient.provider.lightBrightnessTable[s] * sunlightBase;
                            //float lightBrightnessWithTorchFlicker = worldclient.provider.lightBrightnessTable[s] * (er.torchFlickerX * 0.1F + 1.5F);

                            if (worldclient.lastLightningBolt > 0)
                            {
                            	// Restore to 100% sun brightness
                            	sunlight = worldclient.provider.lightBrightnessTable[s];
                            }
            				
                            short short1 = 255;
                            
                            // Mix sunlight into each color channel
                            short red = (short)((((float)r / 15f) + sunlight) * 255f);
                            short green = (short)((((float)g / 15f) + sunlight) * 255f);
                            short blue = (short)((((float)b / 15f) + sunlight) * 255f);
                            
                            if (red > 255) red = 255;
                            if (green > 255) green = 255;
                            if (blue > 255) blue = 255;
                            
                            //ptr = b | g << 4 | r << 8 | s << 12;
                            
                            er.lightmapColors[ptr++] = short1 << 24 | red << 16 | green << 8 | blue;            				            				
            			}
            	}
           
            er.lightmapTexture.updateDynamicTexture();
            er.lightmapUpdateNeeded = false;
            lightMapTexId = er.lightmapTexture.getGlTextureId();
        }
    }
	
	public static void debugLightmap()
	{
        // Render it on the screen
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();            
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, 300, 0.0, 300, -1.0, 1.0);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(GL11.GL_LIGHTING);


        GL11.glColor3f(1,1,1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lightMapTexId);

        // Draw a textured quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(0, 0, 0);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(0, 100, 0);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(100, 100, 0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(100, 0, 0);
        GL11.glEnd();


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();


        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);            		
	}
}
