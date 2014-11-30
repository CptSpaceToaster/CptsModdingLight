package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

/**
 * Dummy class to enable access to runtime-added methods
 */
public class EntityRenderer {
    public Minecraft mc;
    public DynamicTexture lightmapTexture;
    public boolean lightmapUpdateNeeded;
    public ResourceLocation locationLightMap;

    // Added by TransformEntityRenderer
    public void setLightmapTexture(int[] map) {}
}
