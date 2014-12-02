package coloredlightscore.src.helper;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;

public class CLChunkCacheHelper {

    public CLChunkCacheHelper() {
    }

    /**
     * Any Light rendered on a 1.8 Block goes through here
     * Light value returned is SSSS RRRR GGGG BBBB LLLL 0000
     * 
     * Modified by CptSpaceToaster
     */
    public static int getLightBrightnessForSkyBlocks(ChunkCache instance, int x, int y, int z, int lightValue) {
        int skyBrightness = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int blockBrightness = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        lightValue = ((lightValue & 0xf) | ((lightValue & 0x1e0) >> 1) | ((lightValue & 0x3c00) >> 2) | ((lightValue & 0x78000) >> 3));

        blockBrightness = ((blockBrightness & 0xf) | ((blockBrightness & 0x1e0) >> 1) | ((blockBrightness & 0x3c00) >> 2) | ((blockBrightness & 0x78000) >> 3));

        if ((blockBrightness & 0x000f) < (lightValue & 0x000f)) {
            blockBrightness = blockBrightness & 0xfff0 | lightValue & 0x000f;
        }
        if ((blockBrightness & 0x00f0) < (lightValue & 0x00f0)) {
            blockBrightness = blockBrightness & 0xff0f | lightValue & 0x00f0;
        }
        if ((blockBrightness & 0x0f00) < (lightValue & 0x0f00)) {
            blockBrightness = blockBrightness & 0xf0ff | lightValue & 0x0f00;
        }
        if ((blockBrightness & 0xf000) < (lightValue & 0xf000)) {
            blockBrightness = blockBrightness & 0x0fff | lightValue & 0xf000;
        }

        return skyBrightness << 20 | blockBrightness << 4;
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     * 
     * Modified by CptSpaceToaster
     * 
     * Not present in 1.7.2... where it go?    - heaton84
    public float getLightBrightness(ChunkCache instance, int par1, int par2, int par3)
    {
        return instance.worldObj.provider.lightBrightnessTable[instance.getLightValue(par1, par2, par3)%15];
    }    
     */

}
