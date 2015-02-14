package net.minecraft.world;

import coloredlightscore.src.api.CLWorldPipe;
import net.minecraft.block.Block;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Murray on 11/30/2014.
 */
public class World implements IBlockAccess {
    // Added by TransformWorld
    public float[] clSunColor;
    public CLWorldPipe pipe;

    public WorldProvider provider;
    public int skylightSubtracted;
    public Profiler theProfiler;
    public int lastLightningBolt;
    public boolean isRemote;

    public int getBlockLightValue(int par1, int par2, int par3) {
        System.out.println("1");
        pipe = new CLWorldPipe(this);
        System.out.println("2");
        return 0;
    }

    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return false;
    }

    public Block getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public TileEntity getTileEntity(int p_147438_1_, int p_147438_2_, int p_147438_3_) {
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
        return 0;
    }

    @Override
    public int getBlockMetadata(int p_72805_1_, int p_72805_2_, int p_72805_3_) {
        return 0;
    }

    @Override
    public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) {
        return 0;
    }

    @Override
    public boolean isAirBlock(int p_147437_1_, int p_147437_2_, int p_147437_3_) {
        return false;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int p_72807_1_, int p_72807_2_) {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return false;
    }

    public int getSavedLightValue(EnumSkyBlock par4EnumSkyBlock, int l1, int i2, int j2) {
        return 0;
    }

    public int getSkyBlockTypeBrightness(EnumSkyBlock sky, int x, int y, int z) {
        return 0;
    }

    public Chunk getChunkFromChunkCoords(int i, int i1) {
        return null;
    }

    public boolean doChunksNearChunkExist(int x, int y, int z, int i) {
        return false;
    }

    public void setLightValue(EnumSkyBlock par1Enu, int x1, int y1, int z1, int i) {

    }

    public float getSunBrightness(float par1) {
        return 0;
    }

    public boolean isBlockIndirectlyGettingPowered(int x, int y, int z) {
        return false;
    }

    public void scheduleBlockUpdate(int x, int y, int z, Block block, int i) {
    }

    public boolean setBlock(int x, int y, int z, Block block, int i, int i1) {
        return false;
    }

    public boolean setBlockMetadataWithNotify(int x, int y, int z, int temp, int i) {
        return false;
    }
}
