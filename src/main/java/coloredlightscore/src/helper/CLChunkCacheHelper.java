package coloredlightscore.src.helper;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;

public class CLChunkCacheHelper {

    public CLChunkCacheHelper() {
    }

    /**
     * Any Light rendered on a 1.8 Block goes through here
     * Light value returned is SSSS RRRR GGGG BBBB LLLL
     * 
     * Modified by CptSpaceToaster
     */
    public static int getLightBrightnessForSkyBlocks(ChunkCache instance, int x, int y, int z, int lightValue) {
        int skyBrightness = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int blockBrightness = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        lightValue = ((lightValue & 15) | ((lightValue & 480) >> 1) | ((lightValue & 15360) >> 2) | ((lightValue & 491520) >> 3));

        blockBrightness = ((blockBrightness & 15) | ((blockBrightness & 480) >> 1) | ((blockBrightness & 15360) >> 2) | ((blockBrightness & 491520) >> 3));

        if (blockBrightness < lightValue) {
            blockBrightness = lightValue;
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
