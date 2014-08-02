package coloredlightscore.src.helper;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import coloredlightscore.src.types.CLDynamicTexture3D;
import coloredlightscore.src.types.CLEntityRendererInterface;   

public class CLEntityRendererHelper {

    public static float f = (1.0F/256.0F);
    public static float t = 8.0f; 
    
    public static void Initialize() {
    }
    
    public static void updateLightmap(EntityRenderer instance, float par1) {
        WorldClient worldclient = instance.mc.theWorld;
        
        float min = 0.05F;
        float max = 0.2F;
        float setting = instance.mc.gameSettings.gammaSetting * (max-min) + min;
        
        if (worldclient != null) {
            int[] map = new int[16*16*16];
            float sunlightBase = worldclient.getSunBrightness(1.0F) * (1-setting) + setting;
            float sunlight;
            
            for (int s = 0; s < 16; s++) {
                sunlight = ((float)s * sunlightBase / 15F) * (1-setting) + setting;
                
                for (int b = 0; b < 16; b++) {
                    for (int r = 0; r < 16; r++) {
                        if (worldclient.lastLightningBolt > 0) {
                            //sunlight = worldclient.provider.lightBrightnessTable[s];
                        }
                        map[b << 8 | s << 4 | r] = 255 << 24 | (int)(sunlight*255) << 16 | (int)(sunlight*255) << 8 | (int)(sunlight*255);
                        //map[b << 8 | (s-1) << 4 | r] = 0xFF000000;
                    }
                }
            }
            ((CLEntityRendererInterface)instance).setLightmapTexture(map);
            
            ((CLDynamicTexture3D)(instance.lightmapTexture)).updateDynamicTexture();
            ((CLDynamicTexture3D)((CLEntityRendererInterface)instance).getLightmapTexture2()).updateDynamicTexture();
            instance.lightmapUpdateNeeded = false;
        }
    }
    
    public static void enableLightmap(EntityRenderer instance, double par1) {        
        OpenGlHelper.setActiveTexture(GL_TEXTURE0);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glEnable(GL_TEXTURE_2D);
        //GlStateManager.enableTexture();
        //glBindTexture(GL_TEXTURE_2D, <SOMETHING_GOES_HERE>);
        //TODO: What's the call to make it look like this ^
        instance.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE1);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);

        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE1);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
        
        /**********************************************************/
        
        //glActiveTexture(GL_TEXTURE1)
        OpenGlHelper.setActiveTexture(GL_TEXTURE1);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glEnable(GL_TEXTURE_3D);
        //GlStateManager.enableTexture();
        glMatrixMode(GL_TEXTURE);
        glLoadIdentity();
        glScalef(f, f, f);
        glTranslatef(t, t, t);
        glMatrixMode(GL_MODELVIEW);
        //instance.mc.getTextureManager().bindTexture(instance.locationLightMap);
        glBindTexture(GL_TEXTURE_3D, ((CLDynamicTexture3D)(instance.lightmapTexture)).getGlTextureId());
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP);
        
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_ADD);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE2);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_RGB, GL_PREVIOUS);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
 
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_PREVIOUS);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
            
        /**********************************************************/
        
        //glActiveTexture(GL_TEXTURE2)
        OpenGlHelper.setActiveTexture(GL_TEXTURE2);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glEnable(GL_TEXTURE_3D);
        //GlStateManager.enableTexture();
        glMatrixMode(GL_TEXTURE);
        glLoadIdentity();
        glScalef(f, f, f);
        glTranslatef(t, t, t);
        glMatrixMode(GL_MODELVIEW);
        glBindTexture(GL_TEXTURE_3D, ((CLDynamicTexture3D)((CLEntityRendererInterface)instance).getLightmapTexture2()).getGlTextureId());
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);   
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP);
        
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_MODULATE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE0);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_RGB, GL_PREVIOUS);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
 
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE0);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
        
        /**********************************************************/
        
        OpenGlHelper.setActiveTexture(GL_TEXTURE3); 
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glEnable(GL_TEXTURE_2D);
        //GlStateManager.enableTexture();
        //minecraft.getTextureManager().bind(whiteTextureLocation);
        glBindTexture(GL_TEXTURE_2D, ((CLEntityRendererInterface)instance).getLightmapTexture3().getGlTextureId());
        
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_MODULATE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_PRIMARY_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_RGB, GL_PREVIOUS);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
        
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_MODULATE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_PRIMARY_COLOR);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE1_ALPHA, GL_PREVIOUS);   
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_ALPHA, GL_SRC_ALPHA);
        
        /**********************************************************/
        
        OpenGlHelper.setActiveTexture(GL_TEXTURE0); 
    }

    public static void disableLightmap(EntityRenderer instance, double par1) {
        OpenGlHelper.setActiveTexture(GL_TEXTURE3);
        glDisable(GL_TEXTURE_3D);
        OpenGlHelper.setActiveTexture(GL_TEXTURE2);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE2_RGB, GL_TEXTURE2);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);

        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE2);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND2_ALPHA, GL_SRC_ALPHA);
        glDisable(GL_TEXTURE_3D);
        OpenGlHelper.setActiveTexture(GL_TEXTURE1);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE1);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);

        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE1);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_ALPHA, GL_SRC_ALPHA);
        glDisable(GL_TEXTURE_3D);
        
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE0);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);

        glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
        glTexEnvi(GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE0);
        glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
    }
}
