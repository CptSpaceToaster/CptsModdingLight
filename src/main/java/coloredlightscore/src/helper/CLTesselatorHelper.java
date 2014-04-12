package coloredlightscore.src.helper;

import net.minecraft.client.renderer.Tessellator;

public class CLTesselatorHelper {

	private static int nativeBufferSize = 0x200000;
	public static float sunlightBrightness = 1.0f;
	
	public static int test = 0;
	
	public CLTesselatorHelper() {
		
	}
	
    public static void setBrightness(Tessellator instance, int par1)
    {
    	// Lightmap Texture Coords are ........ 00000000 XXXXXXXX 00000000 YYYYYYYY
    	//
    	// Incoming brightness value is in form 00000000 SSSSRRRR GGGGBBBB LLLL0000
    	// Convert this to..................... 00000000 SSSSRRRR 00000000 GGGGBBBB
    	    	
    	instance.hasBrightness = true;    	    	
    	//instance.brightness = ((par1 & 1044480) << 4) | ((par1 & 240) >> 4);
    	instance.brightness = ((par1 & 16711680)) | ((par1 & 65280)>>8);
    	//instance.brightness = test << 16; // << 20 | 15;
    	    	
    	//test = (test + 1) & 15;
    }
}
