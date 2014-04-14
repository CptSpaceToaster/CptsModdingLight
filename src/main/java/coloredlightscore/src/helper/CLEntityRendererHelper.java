package coloredlightscore.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

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
            float sunlightBase = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
            int ptr = 0;
                        
			for (int s=0;s<16;s++)
				for (int b=0;b<16;b++)
            	{ 
            		float sunlight = worldclient.provider.lightBrightnessTable[s] * sunlightBase;            		
            		
                    if (worldclient.lastLightningBolt > 0)
                    {
                    	// Restore to 100% sun brightness
                    	sunlight = worldclient.provider.lightBrightnessTable[s];
                    }
                    
	            	for (int g=0;g<16;g++)
		            	for (int r=0;r<16;r++)
            			{
                            //float lightBrightnessWithTorchFlicker = worldclient.provider.lightBrightnessTable[s] * (er.torchFlickerX * 0.1F + 1.5F);
            				
		            		int short1 = 255;
                            // Mix sunlight into each color channel
		            				            				            		
		            		float rc = Math.max((float)r / 15f, sunlight);
		            		float gc = Math.max((float)g / 15f, sunlight);
		            		float bc = Math.max((float)b / 15f, sunlight);
		            		
                            int red = (int)(rc * 255f);
                            int green = (int)(gc * 255f);
                            int blue = (int)(bc * 255f);
                            
                            if (red > 255) red = 255;
                            if (green > 255) green = 255;
                            if (blue > 255) blue = 255;
                            if (red < 0) red = 0;
                            if (green < 0) green = 0;
                            if (blue < 0) blue = 0;
                            
                            //ptr = b | g << 4 | r << 8 | s << 12;
                            
                            er.lightmapColors[ptr++] = short1 << 24 | red << 16 | green << 8 | blue;            				            				
            			}
            	}
			           
			DynamicTextureHelper.updateDynamicTexture(er.lightmapTexture);
            er.lightmapUpdateNeeded = false;
            lightMapTexId = er.lightmapTexture.getGlTextureId();
        }
    }
	
	public static void enableLightmap(EntityRenderer instance, double par1)
	{
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        float f = 0.00390625F; // 1/256
        float t = 0.5f;
        GL11.glScalef(f, f, f);
        //GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glTranslatef(t, t, t);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        instance.mc.getTextureManager().bindTexture(instance.locationLightMap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11 .GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);		
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
