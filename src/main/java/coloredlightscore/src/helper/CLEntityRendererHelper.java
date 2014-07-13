package coloredlightscore.src.helper;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import coloredlightscore.src.types.CLDynamicTexture3D;

public class CLEntityRendererHelper {

    // public static int lightMap1 = -1;
    // public static int lightMap2 = -1; 

    public static void Initialize() {
        // Redefine daylight
    }

    public static void updateLightmap(EntityRenderer instance, float par1) {
        WorldClient worldclient = instance.mc.theWorld;

        if (worldclient != null) {
            float sunlightBase = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
            int ptr1 = 0;
            // int ptr2 = 0;

            for (int s = 0; s < 16; s++) {
                float sunlight = worldclient.provider.lightBrightnessTable[s] * sunlightBase;
                int alpha = 255; // Solid Color

                if (worldclient.lastLightningBolt > 0) {
                    // Restore to 100% sun brightness, because lightning...
                    sunlight = worldclient.provider.lightBrightnessTable[s];
                }

                /* RSL - Red, Sunlight, Light(Rave/Rainbow) */
                for (int r = 0; r < 16; r++) {
                    for (int rave = 0; rave < 16; rave++) {
                        // float lightBrightnessWithTorchFlicker =
                        // worldclient.provider.lightBrightnessTable[s] *
                        // (er.torchFlickerX * 0.1F + 1.5F);

                        // Mix sunlight into red color channel
                        float rc = Math.max((float) r / 15f, sunlight);
                        int red = (int) (rc * 255f);
                        // Clamp red
                        if (red > 255)
                            red = 255;
                        if (red < 0)
                            red = 0;

                        // Ignore Rave for now... It will have to mess with RGB
                        // though...

                        // Place Does this work?
                        ptr1 = rave << 8 | s << 4 | r;
                        // instance.lightmapColors[ptr1] = alpha << 24 | red <<
                        // 16 | 255 << 8 | 255;
                        // ((CLDynamicTexture3D)(instance.lightmapTexture)).dynamicTextureData[ptr1]
                        // = 255 << 24 | 255 << 16 | red << 8 | alpha;
                        ((CLDynamicTexture3D) (instance.lightmapTexture)).dynamicTextureData[ptr1] = 0xFFFFFFFF;
                    }
                }

                /* RSL - Red, Sunlight, Light(Rave/Rainbow) */
                for (int g = 0; g < 16; g++) {
                    for (int b = 0; b < 16; b++) {
                        // Mix
                        float gc = Math.max((float) g / 15f, sunlight);
                        int green = (int) (gc * 255f);
                        // Clamp
                        if (green > 255)
                            green = 255;
                        if (green < 0)
                            green = 0;

                        // Mix
                        float bc = Math.max((float) b / 15f, sunlight);
                        int blue = (int) (bc * 255f);
                        // Clamp
                        if (blue > 255)
                            blue = 255;
                        if (blue < 0)
                            blue = 0;

                        // ptr2 = b << 16 | s << 8 | g;
                        // er.lightmapColors[ptr2] = blue << 24 | green << 16 |
                        // 255 << 8 | alpha;
                        // ((CLDynamicTexture3D)(instance.lightmapTexture)).dynamicTextureData[ptr1]
                        // = blue << 24 | green << 16 | 255 << 8 | alpha;
                    }
                }
            }

            ((CLDynamicTexture3D) (instance.lightmapTexture)).updateDynamicTexture();
            instance.lightmapUpdateNeeded = false;
        }
    }

    public static void enableLightmap(EntityRenderer instance, double par1) {
        //FMLLog.info("" + OpenGlHelper.lightmapTexUnit);
        //OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        float f = 0.00390625F; // 1/4096 = 0.00024414062F 1/256 = 0.00390625F
                               // 1/16 = 0.0625F
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
        
        //New Code
        
        GL11.glEnable(GL12.GL_TEXTURE_3D);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_REPLACE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_TEXTURE1);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);

        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL13.GL_TEXTURE1);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        //End New Code
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_TEXTURE_3D);

        /*
         * We were fooling and found that we could get an image on screen by
         * doing this
         */
        //OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public static void disableLightmap(EntityRenderer instance, double par1) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL12.GL_TEXTURE_3D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /* This might not be necessary */
    public static void bindTexture(int textureID) {
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureID);
    }
}
