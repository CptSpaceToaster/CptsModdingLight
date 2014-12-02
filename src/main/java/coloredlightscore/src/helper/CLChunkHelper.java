package coloredlightscore.src.helper;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Created by Murray on 11/30/2014.
 */
public class CLChunkHelper {
    /**
     * Gets the amount of light on a block taking into account sunlight
     */
    public static int getBlockLightValue(Chunk instance, int p_76629_1_, int p_76629_2_, int p_76629_3_, int p_76629_4_)
    {
        ExtendedBlockStorage extendedblockstorage = instance.storageArrays[p_76629_2_ >> 4];

        if (extendedblockstorage == null)
        {
            return !instance.worldObj.provider.hasNoSky && p_76629_4_ < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - p_76629_4_ : 0;
        }
        else
        {
            int skyLight = instance.worldObj.provider.hasNoSky ? 0 : extendedblockstorage.getExtSkylightValue(p_76629_1_, p_76629_2_ & 15, p_76629_3_) & 0xf;

            if (skyLight > 0)
            {
                Chunk.isLit = true;
            }

            skyLight -= p_76629_4_;
            int blockLight = extendedblockstorage.getExtBlocklightValue(p_76629_1_, p_76629_2_ & 15, p_76629_3_) & 0xf;
            if (skyLight > blockLight)
            {
                blockLight = skyLight;
            }

            return blockLight;
        }
    }
}
