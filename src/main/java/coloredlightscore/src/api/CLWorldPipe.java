package coloredlightscore.src.api;

import net.minecraft.block.Block;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/* A collection of the methods used in CLWorldHelper.  By routing them through the pipe, they can easily be accessed
 * and redirected to more efficient handlers if another avid modder so desired!
 *
 * Thank you Player! */
public class CLWorldPipe {
    public World world;

    public CLWorldPipe(World par_world) {
        world = par_world;
    }

    public Block getBlock(int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    public Chunk getChunkFromChunkCoords(int x, int z) {
        return world.getChunkFromChunkCoords(x, z);
    }

    public int getSkyBlockTypeBrightness(EnumSkyBlock enu, int x, int y, int z) {
        return world.getSkyBlockTypeBrightness(enu, x, y, z);
    }

    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return world.canBlockSeeTheSky(x, y, z);
    }

    public int getSavedLightValue(EnumSkyBlock enu, int x, int y, int z) {
        return world.getSavedLightValue(enu, x, y, z);
    }

    public boolean doChunksNearChunkExist(int x, int y, int z, int r) {
        return world.doChunksNearChunkExist(x, y, z, r);
    }

    public void setLightValue(EnumSkyBlock enu, int x, int y, int z, int lightValue) {
        world.setLightValue(enu, x, y, z, lightValue);
    }


}
