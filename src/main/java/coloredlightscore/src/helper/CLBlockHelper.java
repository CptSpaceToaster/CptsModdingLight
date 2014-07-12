package coloredlightscore.src.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import coloredlightscore.src.api.CLApi;

public class CLBlockHelper {
    public static Block setLightLevel(Block interceptedReturnValue, Block instance, float par1) {
        return CLApi.setBlockColorRGB(instance, par1, par1, par1);
    }

    public static int getMixedBrightnessForBlockWithColor(IBlockAccess blockAccess, int x, int y, int z) {
        int l;
        Block block = blockAccess.getBlock(x, y, z);
        if (blockAccess instanceof World)
            l = CLWorldHelper.getLightBrightnessForSkyBlocks((World) blockAccess, x, y, z, block.getLightValue(blockAccess, x, y, z));
        else
            l = CLChunkCacheHelper.getLightBrightnessForSkyBlocks((ChunkCache) blockAccess, x, y, z, block.getLightValue(blockAccess, x, y, z));

        if (l == 0 && block instanceof BlockSlab) {
            --y;
            block = blockAccess.getBlock(x, y, z);
            if (blockAccess instanceof World)
                return CLWorldHelper.getLightBrightnessForSkyBlocks((World) blockAccess, x, y, z, block.getLightValue(blockAccess, x, y, z));
            else
                return CLChunkCacheHelper.getLightBrightnessForSkyBlocks((ChunkCache) blockAccess, x, y, z, block.getLightValue(blockAccess, x, y, z));
        } else {
            return l;
        }
    }
}