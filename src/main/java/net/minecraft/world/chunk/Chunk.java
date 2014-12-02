package net.minecraft.world.chunk;

import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Created by Murray on 11/30/2014.
 */
public class Chunk {
    public ExtendedBlockStorage[] storageArrays;
    public World worldObj;
    public int xPosition;
    public int zPosition;
    public static boolean isLit;

    public ExtendedBlockStorage[] getBlockStorageArray() {
        return storageArrays;
    }

    public ExtendedBlockStorage[] getStorageArrays() {
        return storageArrays;
    }

    public void setStorageArrays(ExtendedBlockStorage[] storageArrays) {
        this.storageArrays = storageArrays;
    }

    public int getBlockLightValue(int x, int y, int z, int skylightSubtracted) {
        return 0;
    }
}
