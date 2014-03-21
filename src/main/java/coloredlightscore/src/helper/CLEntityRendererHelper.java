package kovukore.coloredlights.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.potion.Potion;

public class CLEntityRendererHelper {
	public static void updateLightmap(EntityRenderer er, float par1)
    {
        WorldClient worldclient = er.mc.theWorld;

        if (worldclient != null)
        {
            for (int i = 0; i < 256; ++i)
            {
                float f1 = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float f2 = worldclient.provider.lightBrightnessTable[i / 16] * f1;
                float f3 = worldclient.provider.lightBrightnessTable[i % 16] * (er.torchFlickerX * 0.1F + 1.5F);

                if (worldclient.lastLightningBolt > 0)
                {
                    f2 = worldclient.provider.lightBrightnessTable[i / 16];
                }

                float f4 = f2 * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float f5 = f2 * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float f6 = f3;// * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                float f7 = f3;// * (f3 * f3 * 0.6F + 0.4F);
                float f8 = f4 + f3;
                float f9 = f5 + f6;
                float f10 = f2 + f7;
                f8 = f8 * 0.96F + 0.03F;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;
                float f11;

                if (er.bossColorModifier > 0.0F)
                {
                    f11 = er.bossColorModifierPrev + (er.bossColorModifier - er.bossColorModifierPrev) * par1;
                    f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                    f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                    f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
                }

                if (worldclient.provider.dimensionId == 1)
                {
                    f8 = 0.22F + f3 * 0.75F;
                    f9 = 0.28F + f6 * 0.75F;
                    f10 = 0.25F + f7 * 0.75F;
                }

                float f12;

                if (er.mc.thePlayer.isPotionActive(Potion.nightVision))
                {
                    f11 = er.getNightVisionBrightness(er.mc.thePlayer, par1);
                    f12 = 1.0F / f8;

                    if (f12 > 1.0F / f9)
                    {
                        f12 = 1.0F / f9;
                    }

                    if (f12 > 1.0F / f10)
                    {
                        f12 = 1.0F / f10;
                    }

                    f8 = f8 * (1.0F - f11) + f8 * f12 * f11;
                    f9 = f9 * (1.0F - f11) + f9 * f12 * f11;
                    f10 = f10 * (1.0F - f11) + f10 * f12 * f11;
                }

                if (f8 > 1.0F)
                {
                    f8 = 1.0F;
                }

                if (f9 > 1.0F)
                {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F)
                {
                    f10 = 1.0F;
                }

                f11 = er.mc.gameSettings.gammaSetting;
                f12 = 1.0F - f8;
                float f13 = 1.0F - f9;
                float f14 = 1.0F - f10;
                f12 = 1.0F - f12 * f12 * f12 * f12;
                f13 = 1.0F - f13 * f13 * f13 * f13;
                f14 = 1.0F - f14 * f14 * f14 * f14;
                f8 = f8 * (1.0F - f11) + f12 * f11;
                f9 = f9 * (1.0F - f11) + f13 * f11;
                f10 = f10 * (1.0F - f11) + f14 * f11;
                f8 = f8 * 0.96F + 0.03F;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;

                if (f8 > 1.0F)
                {
                    f8 = 1.0F;
                }

                if (f9 > 1.0F)
                {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F)
                {
                    f10 = 1.0F;
                }

                if (f8 < 0.0F)
                {
                    f8 = 0.0F;
                }

                if (f9 < 0.0F)
                {
                    f9 = 0.0F;
                }

                if (f10 < 0.0F)
                {
                    f10 = 0.0F;
                }

                short short1 = 255;
                int j = (int)(f8 * 255.0F);
                int k = (int)(f9 * 255.0F);
                int l = (int)(f10 * 255.0F);
                er.lightmapColors[i] = short1 << 24 | j << 16 | k << 8 | l;
            }

            er.lightmapTexture.updateDynamicTexture();
            er.lightmapUpdateNeeded = false;
        }
    }
}
