package coloredlightscore.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;

public class CLEntityRendererHelper {
		
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

//            // This bit makes everything too green... swapped byte order below (RS and BG)
//            for (int r=0;r<16;r++)
//            	for (int s=0;s<16;s++)
//            	{
//            		float sunlight = worldclient.provider.lightBrightnessTable[s] * sunlightBase;
//                    //float lightBrightnessWithTorchFlicker = worldclient.provider.lightBrightnessTable[s] * (er.torchFlickerX * 0.1F + 1.5F);
//
//                    if (worldclient.lastLightningBolt > 0)
//                    {
//                    	// Restore to 100% sun brightness
//                    	sunlight = worldclient.provider.lightBrightnessTable[s];
//                    }
//                    
//            		for (int b=0;b<16;b++)
//            			for (int g=0;g<16;g++)
//            			{
//                            short short1 = 255;
//                            
//                            // Mix sunlight into each color channel
//                            short red = (short)((((float)r / 16f) + sunlight) * 255f);
//                            short green = (short)((((float)g / 16f) + sunlight) * 255f);
//                            short blue = (short)((((float)b / 16f) + sunlight) * 255f);
//                            
//            				if (red > 255) red = 255;
//            				if (green > 255) green = 255;
//            				if (blue > 255) blue = 255;            
//                            er.lightmapColors[ptr++] = short1 << 24 | red << 16 | green << 8 | blue;            				            				
//            			}
//            	}

            for (int b=0;b<16;b++)
            	for (int g=0;g<16;g++)
            	{                    
            		for (int r=0;r<16;r++)
            			for (int s=0;s<16;s++)
            			{
                    		float sunlight = worldclient.provider.lightBrightnessTable[s] * sunlightBase;
                            //float lightBrightnessWithTorchFlicker = worldclient.provider.lightBrightnessTable[s] * (er.torchFlickerX * 0.1F + 1.5F);

                            if (worldclient.lastLightningBolt > 0)
                            {
                            	// Restore to 100% sun brightness
                            	sunlight = worldclient.provider.lightBrightnessTable[s];
                            }
            				
                            short short1 = 255;
                            
                            // Mix sunlight into each color channel
                            short red = (short)((((float)r / 16f) + sunlight) * 255f);
                            short green = (short)((((float)g / 16f) + sunlight) * 255f);
                            short blue = (short)((((float)b / 16f) + sunlight) * 255f);
                            
                            if (red > 255) red = 255;
                            if (green > 255) green = 255;
                            if (blue > 255) blue = 255;
                            
                            er.lightmapColors[ptr++] = short1 << 24 | red << 16 | green << 8 | blue;            				            				
            			}
            	}
            

            er.lightmapTexture.updateDynamicTexture();
            er.lightmapUpdateNeeded = false;
            
        }
    }
}
