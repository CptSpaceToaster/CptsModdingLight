package coloredlightscore.src.helper;

import static org.lwjgl.opengl.GL11.*;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class CLEntityRendererHelper {

    public static float f = (1.0F/4096.0F);
    public static float t = 8.0f;
    public static float[] sunlightColor = {1.0F, 1.0F, 1.0F}; // RGB TODO:Move this to dimension?
    
    public static void Initialize() {
    }
    
    public static void updateLightmap(EntityRenderer instance, float par1) {
        WorldClient worldclient = instance.mc.theWorld;
        
        float min = 0.05F;
        float max = 1.0F;
        
        if (worldclient != null) {
            int[] map = new int[16*16*16*16];
            float sunlightBase = worldclient.getSunBrightness(par1);
            float sunlight, bSunlight, gSunlight, rSunlight, bLight, gLight, rLight;
            
            for (int s = 0; s < 16; s++) {
                sunlight = sunlightBase * worldclient.provider.lightBrightnessTable[s] * (max - min) + min;
                if (worldclient.lastLightningBolt > 0) {
                    sunlight = worldclient.provider.lightBrightnessTable[s] * (max - min) + min;
                }
                rSunlight = sunlight * sunlightColor[0];
                gSunlight = sunlight * sunlightColor[1];
                bSunlight = sunlight * sunlightColor[2];
                
                for (int b = 0; b < 16; b++) {
                    bLight = worldclient.provider.lightBrightnessTable[b] + bSunlight;
                    if (bLight > 1.0) {
                        bLight = 1.0f;
                    }
                    for (int g = 0; g < 16; g++) {
                        gLight = worldclient.provider.lightBrightnessTable[g] + gSunlight;
                        if (gLight > 1.0) {
                            gLight = 1.0f;
                        }
                        for (int r = 0; r < 16; r++) {
                            rLight = worldclient.provider.lightBrightnessTable[r] + rSunlight;
                            if (rLight > 1.0) {
                                rLight = 1.0f;
                            }
                            map[g << 12 | s << 8 | r << 4 | b] = 255 << 24 | (int) (rLight * 255) << 16 | (int) (gLight * 255) << 8 | (int) (bLight * 255);
                        }
                    }
                }
            }
            instance.setLightmapTexture(map);
            
            instance.lightmapTexture.updateDynamicTexture();
            instance.lightmapUpdateNeeded = false;
        }
    }
    
    public static void enableLightmap(EntityRenderer instance, double par1) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(t, t, t);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        instance.mc.getTextureManager().bindTexture(instance.locationLightMap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        CLTessellatorHelper.enableShader();
    }

    public static void disableLightmap(EntityRenderer instance, double par1) {
        CLTessellatorHelper.disableShader();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        glDisable(GL_TEXTURE_2D);
        
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
