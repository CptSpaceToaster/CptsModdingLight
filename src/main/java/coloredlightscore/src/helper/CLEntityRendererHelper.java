package coloredlightscore.src.helper;

import java.nio.ByteBuffer;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.potion.Potion;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CLEntityRendererHelper {
	
	private static int colorCubeTextureName;
	
	public static void Initialize()
	{
		GL11.glEnable(GL12.GL_TEXTURE_3D);
		
		colorCubeTextureName = GL11.glGenTextures();
		
		int texSize = 16;
		ByteBuffer texelData = ByteBuffer.allocate(texSize * texSize * texSize * 3); // 3 bytes/pixel
		
		
		// heaton84: the order of these may need to be adjusted through experimentation
		
		for (int r=0;r<16;r++)
			for (int g=0;g<16;g++)
				for (int b=0;b<16;b++)
				{
					//int offset = (3 * (g * 16 + b)) + (16 * 16 * r * 3);
					
					texelData.put((byte)(255 * ((float)r / 16f)));
					texelData.put((byte)(255 * ((float)g / 16f)));
					texelData.put((byte)(255 * ((float)b / 16f)));
				}
		
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);
		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGB8, texSize, texSize, texSize, 0, GL11.GL_RGB, 
		             GL11.GL_UNSIGNED_BYTE, texelData);		
		
	}
	
	public static void updateLightmap(EntityRenderer er, float par1)
    {
        WorldClient worldclient = er.mc.theWorld;

        if (worldclient != null)
        {
            for (int i = 0; i < 256; ++i)
            {
                float sunlight = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float lightBrightnessWithSunlight = worldclient.provider.lightBrightnessTable[i / 16] * sunlight;
                float lightBrightnessWithTorchFlicker = worldclient.provider.lightBrightnessTable[i % 16] * (er.torchFlickerX * 0.1F + 1.5F);

                if (worldclient.lastLightningBolt > 0)
                {
                	// Restore to 100% sun brightness
                    lightBrightnessWithSunlight = worldclient.provider.lightBrightnessTable[i / 16];
                }

                float sunOrMoonlight = lightBrightnessWithSunlight * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float sunOrMoonlight2 = lightBrightnessWithSunlight * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                //float f6 = lightBrightnessWithTorchFlicker;// * ((lightBrightnessWithTorchFlicker * 0.6F + 0.4F) * 0.6F + 0.4F);
                //float f7 = lightBrightnessWithTorchFlicker;// * (lightBrightnessWithTorchFlicker * lightBrightnessWithTorchFlicker * 0.6F + 0.4F);
                float red = sunOrMoonlight + lightBrightnessWithTorchFlicker;                
                float green = sunOrMoonlight2 + lightBrightnessWithTorchFlicker;               //float green = sunOrMoonlight2 + f6;
                float blue = lightBrightnessWithSunlight + lightBrightnessWithTorchFlicker;  //float blue = lightBrightnessWithSunlight + f7;
                red = red * 0.96F + 0.03F;
                green = green * 0.96F + 0.03F;
                blue = blue * 0.96F + 0.03F;
                float gamma;

                if (er.bossColorModifier > 0.0F)
                {
                    gamma = er.bossColorModifierPrev + (er.bossColorModifier - er.bossColorModifierPrev) * par1;
                    red = red * (1.0F - gamma) + red * 0.7F * gamma;
                    green = green * (1.0F - gamma) + green * 0.6F * gamma;
                    blue = blue * (1.0F - gamma) + blue * 0.6F * gamma;
                }

                if (worldclient.provider.dimensionId == 1)
                {
                    red = 0.22F + lightBrightnessWithTorchFlicker * 0.75F;                    
                    green = 0.28F + lightBrightnessWithTorchFlicker * 0.75F;  //green = 0.28F + f6 * 0.75F;
                    blue = 0.25F + lightBrightnessWithTorchFlicker * 0.75F;  //blue = 0.25F + f7 * 0.75F;
                }

                float f12;

                if (er.mc.thePlayer.isPotionActive(Potion.nightVision))
                {
                    gamma = er.getNightVisionBrightness(er.mc.thePlayer, par1);
                    f12 = 1.0F / red;

                    if (f12 > 1.0F / green)
                    {
                        f12 = 1.0F / green;
                    }

                    if (f12 > 1.0F / blue)
                    {
                        f12 = 1.0F / blue;
                    }

                    red = red * (1.0F - gamma) + red * f12 * gamma;
                    green = green * (1.0F - gamma) + green * f12 * gamma;
                    blue = blue * (1.0F - gamma) + blue * f12 * gamma;
                }

                if (red > 1.0F)
                {
                    red = 1.0F;
                }

                if (green > 1.0F)
                {
                    green = 1.0F;
                }

                if (blue > 1.0F)
                {
                    blue = 1.0F;
                }

                gamma = er.mc.gameSettings.gammaSetting;
                f12 = 1.0F - red;
                float f13 = 1.0F - green;
                float f14 = 1.0F - blue;
                f12 = 1.0F - f12 * f12 * f12 * f12;
                f13 = 1.0F - f13 * f13 * f13 * f13;
                f14 = 1.0F - f14 * f14 * f14 * f14;
                red = red * (1.0F - gamma) + f12 * gamma;
                green = green * (1.0F - gamma) + f13 * gamma;
                blue = blue * (1.0F - gamma) + f14 * gamma;
                red = red * 0.96F + 0.03F;
                green = green * 0.96F + 0.03F;
                blue = blue * 0.96F + 0.03F;

                if (red > 1.0F)
                {
                    red = 1.0F;
                }

                if (green > 1.0F)
                {
                    green = 1.0F;
                }

                if (blue > 1.0F)
                {
                    blue = 1.0F;
                }

                if (red < 0.0F)
                {
                    red = 0.0F;
                }

                if (green < 0.0F)
                {
                    green = 0.0F;
                }

                if (blue < 0.0F)
                {
                    blue = 0.0F;
                }

                short short1 = 255;
                int j = (int)(red * 255.0F);
                int k = (int)(green * 255.0F);
                int l = (int)(blue * 255.0F);
                er.lightmapColors[i] = short1 << 24 | j << 16 | k << 8 | l;
            }

            er.lightmapTexture.updateDynamicTexture();
            er.lightmapUpdateNeeded = false;
        }
    }
}
