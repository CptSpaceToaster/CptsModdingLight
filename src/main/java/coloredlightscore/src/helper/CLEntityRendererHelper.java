package coloredlightscore.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import coloredlightscore.src.types.CLDynamicTexture1D;
import coloredlightscore.src.types.CLDynamicTexture3D;
import coloredlightscore.src.types.CLEntityRendererInterface;

public class CLEntityRendererHelper {

    public static void Initialize() {
    }

    public static void updateLightmap(EntityRenderer instance, float par1) {
        WorldClient worldclient = instance.mc.theWorld;

        if (worldclient != null) {
            int sunlightIntArray[] = new int[16];
            for (int s = 0; s < 16; s++) {
                float sunlightBase = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float sunlight = worldclient.provider.lightBrightnessTable[s] * sunlightBase;
                
                if (worldclient.lastLightningBolt > 0) {
                    sunlight = worldclient.provider.lightBrightnessTable[s];
                }
                sunlightIntArray[s] =  255 << 24 | (int)sunlight*255 << 16 | (int)sunlight*255 << 8 | (int)sunlight*255;
            }
            
            ((CLEntityRendererInterface)instance).setLightmapTextureData2(sunlightIntArray);
            
            ((CLDynamicTexture3D)(instance.lightmapTexture)).updateDynamicTexture();
            ((CLDynamicTexture1D)((CLEntityRendererInterface)instance).getLightmapTexture2()).updateDynamicTexture();
            instance.lightmapUpdateNeeded = false;
        }
    }
    
    public static void enableLightmap(EntityRenderer instance, double par1) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        float f = (1/256F);
        float t = 8.0f;
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(t, t, t);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, ((CLDynamicTexture3D) instance.lightmapTexture).getGlTextureId());

        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_CLAMP);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_TEXTURE_3D);
        //TODO: Setup 1D texture combination scheme 

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void disableLightmap(EntityRenderer instance, double par1) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL12.GL_TEXTURE_3D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /* This might not be necessary... as in, you can just call it directly... */
    public static void bindTexture(int textureID) {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureID);
    }
}
