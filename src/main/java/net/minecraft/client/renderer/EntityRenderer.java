package net.minecraft.client.renderer;

import coloredlightscore.src.types.CLDynamicTexture2D;
import coloredlightscore.src.types.CLDynamicTexture3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

/**
 * Dummy class to enable access to runtime-added methods
 */
public class EntityRenderer {
    public Minecraft mc;
    public DynamicTexture lightmapTexture;
    public boolean lightmapUpdateNeeded;

    // Added by TransformEntityRenderer
    public CLDynamicTexture3D getLightmapTexture2() {
        return null;
    }

    // Added by TransformEntityRenderer
    public CLDynamicTexture2D getLightmapTexture3() {
        return null;
    }

    // Added by TransformEntityRenderer
    public void setLightmapTexture(int[] map) {}
}
