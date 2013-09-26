package net.minecraft.world.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class Chunk
{
    /**
     * Determines if the chunk is lit or not at a light value greater than 0.
     */
    public static boolean isLit;

    /**
     * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
     * logical segment of 16x16x16 blocks, stacked vertically.
     */
    private ExtendedBlockStorage[] storageArrays;

    /**
     * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs.
     */
    private byte[] blockBiomeArray;

    /**
     * A map, similar to heightMap, that tracks how far down precipitation can fall.
     */
    public int[] precipitationHeightMap;

    /** Which columns need their skylightMaps updated. */
    public boolean[] updateSkylightColumns;

    /** Whether or not this Chunk is currently loaded into the World */
    public boolean isChunkLoaded;

    /** Reference to the World object. */
    public World worldObj;
    public int[] heightMap;

    /** The x coordinate of the chunk. */
    public final int xPosition;

    /** The z coordinate of the chunk. */
    public final int zPosition;
    private boolean isGapLightingUpdated;

    /** A Map of ChunkPositions to TileEntities in this chunk */
    public Map chunkTileEntityMap;

    /**
     * Array of Lists containing the entities in this Chunk. Each List represents a 16 block subchunk.
     */
    public List[] entityLists;

    /** Boolean value indicating if the terrain is populated. */
    public boolean isTerrainPopulated;

    /**
     * Set to true if the chunk has been modified and needs to be updated internally.
     */
    public boolean isModified;

    /**
     * Whether this Chunk has any Entities and thus requires saving on every tick
     */
    public boolean hasEntities;

    /** The time according to World.worldTime when this chunk was last saved */
    public long lastSaveTime;

    /**
     * Updates to this chunk will not be sent to clients if this is false. This field is set to true the first time the
     * chunk is sent to a client, and never set to false.
     */
    public boolean sendUpdates;

    /** Lowest value in the heightmap. */
    public int heightMapMinimum;
    public long field_111204_q;

    /**
     * Contains the current round-robin relight check index, and is implied as the relight check location as well.
     */
    private int queuedLightChecks;

    public Chunk(World par1World, int par2, int par3)
    {
        this.storageArrays = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.precipitationHeightMap = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.chunkTileEntityMap = new HashMap();
        this.queuedLightChecks = 4096;
        this.entityLists = new List[16];
        this.worldObj = par1World;
        this.xPosition = par2;
        this.zPosition = par3;
        this.heightMap = new int[256];

        for (int k = 0; k < this.entityLists.length; ++k)
        {
            this.entityLists[k] = new ArrayList();
        }

        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.blockBiomeArray, (byte) - 1);
    }

    public Chunk(World par1World, byte[] par2ArrayOfByte, int par3, int par4)
    {
        this(par1World, par3, par4);
        int k = par2ArrayOfByte.length / 256;

        for (int l = 0; l < 16; ++l)
        {
            for (int i1 = 0; i1 < 16; ++i1)
            {
                for (int j1 = 0; j1 < k; ++j1)
                {
                    /* FORGE: The following change, a cast from unsigned byte to int,
                     * fixes a vanilla bug when generating new chunks that contain a block ID > 127 */
                    int b0 = par2ArrayOfByte[l << 11 | i1 << 7 | j1] & 0xFF;

                    if (b0 != 0)
                    {
                        int k1 = j1 >> 4;

                        if (this.storageArrays[k1] == null)
                        {
                            this.storageArrays[k1] = new ExtendedBlockStorage(k1 << 4, !par1World.provider.hasNoSky);
                        }

                        this.storageArrays[k1].setExtBlockID(l, j1 & 15, i1, b0);
                    }
                }
            }
        }
    }

    /**
     * Metadata sensitive Chunk constructor for use in new ChunkProviders that
     * use metadata sensitive blocks during generation.
     *
     * @param world The world this chunk belongs to
     * @param ids A ByteArray containing all the BlockID's to set this chunk to
     * @param metadata A ByteArray containing all the metadata to set this chunk to
     * @param chunkX The chunk's X position
     * @param chunkZ The Chunk's Z position
     */
    public Chunk(World world, byte[] ids, byte[] metadata, int chunkX, int chunkZ)
    {
        this(world, chunkX, chunkZ);
        int k = ids.length / 256;

        for (int x = 0; x < 16; ++x)
        {
            for (int z = 0; z < 16; ++z)
            {
                for (int y = 0; y < k; ++y)
                {
                    int idx = x << 11 | z << 7 | y;
                   int id = ids[idx] & 0xFF;
                    int meta = metadata[idx];

                    if (id != 0)
                    {
                        int l = y >> 4;

                        if (this.storageArrays[l] == null)
                        {
                            this.storageArrays[l] = new ExtendedBlockStorage(l << 4, !world.provider.hasNoSky);
                        }

                        this.storageArrays[l].setExtBlockID(x, y & 15, z, id);
                        this.storageArrays[l].setExtBlockMetadata(x, y & 15, z, meta);
                    }
                }
            }
        }
    }

    /**
     * A Chunk Constructor which handles shorts to allow block ids > 256 (full 4096 range)
     * Meta data sensitive
     * NOTE: The x,y,z order of the array is different from the native Chunk constructor to allow for generation > y127
     * NOTE: This is possibly more efficient than the standard constructor due to less memory skipping
     *
     * @param world The world this chunk belongs to
     * @param ids A ShortArray containing all the BlockID's to set this chunk to (x is low order, z is mid, y is high)
     * @param metadata A ByteArray containing all the metadata to set this chunk to
     * @param chunkX The chunk's X position
     * @param chunkZ The Chunk's Z position
     */
    public Chunk(World world, short[] ids, byte[] metadata, int chunkX, int chunkZ)
    {
        this(world, chunkX, chunkZ);
        int max = ids.length / 256;

        for (int y = 0; y < max; ++y)
        {
            for (int z = 0; z < 16; ++z)
            {
                for (int x = 0; x < 16; ++x)
                {
                    int idx = y << 8 | z << 4 | x;
                    int id = ids[idx] & 0xFFFFFF;
                    int meta = metadata[idx];

                    if (id != 0) {
                        int storageBlock = y >> 4;

                        if (this.storageArrays[storageBlock] == null) {
                                this.storageArrays[storageBlock] = new ExtendedBlockStorage(storageBlock << 4, !world.provider.hasNoSky);
                        }
        
                        this.storageArrays[storageBlock].setExtBlockID(x, y & 15, z, id);
                        this.storageArrays[storageBlock].setExtBlockMetadata(x, y & 15, z, meta);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the chunk is at the X/Z location specified
     */
    public boolean isAtLocation(int par1, int par2)
    {
        return par1 == this.xPosition && par2 == this.zPosition;
    }

    /**
     * Returns the value in the height map at this x, z coordinate in the chunk
     */
    public int getHeightValue(int par1, int par2)
    {
        return this.heightMap[par2 << 4 | par1];
    }

    /**
     * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
     */
    public int getTopFilledSegment()
    {
        for (int i = this.storageArrays.length - 1; i >= 0; --i)
        {
            if (this.storageArrays[i] != null)
            {
                return this.storageArrays[i].getYLocation();
            }
        }

        return 0;
    }

    /**
     * Returns the ExtendedBlockStorage array for this Chunk.
     */
    public ExtendedBlockStorage[] getBlockStorageArray()
    {
        return this.storageArrays;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Generates the height map for a chunk from scratch
     */
    public void generateHeightMap()
    {
        int i = this.getTopFilledSegment();

        for (int j = 0; j < 16; ++j)
        {
            int k = 0;

            while (k < 16)
            {
                this.precipitationHeightMap[j + (k << 4)] = -999;
                int l = i + 16 - 1;

                while (true)
                {
                    if (l > 0)
                    {
                        int i1 = this.getBlockID(j, l - 1, k);

                        if (getBlockLightOpacity(j, l - 1, k) == 0)
                        {
                            --l;
                            continue;
                        }

                        this.heightMap[k << 4 | j] = l;
                    }

                    ++k;
                    break;
                }
            }
        }

        this.isModified = true;
    }

    /**
     * Generates the initial skylight map for the chunk upon generation or load.
     */
    public void generateSkylightMap()
    {
        int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;
        int j;
        int k;

        for (j = 0; j < 16; ++j)
        {
            k = 0;

            while (k < 16)
            {
                this.precipitationHeightMap[j + (k << 4)] = -999;
                int l = i + 16 - 1;

                while (true)
                {
                    if (l > 0)
                    {
                        if (this.getBlockLightOpacity(j, l - 1, k) == 0)
                        {
                            --l;
                            continue;
                        }

                        this.heightMap[k << 4 | j] = l;

                        if (l < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = l;
                        }
                    }

                    if (!this.worldObj.provider.hasNoSky)
                    {
                        l = 15;
                        int i1 = i + 16 - 1;

                        do
                        {
                            l -= this.getBlockLightOpacity(j, i1, k);

                            if (l > 0)
                            {
                                ExtendedBlockStorage extendedblockstorage = this.storageArrays[i1 >> 4];

                                if (extendedblockstorage != null)
                                {
                                    extendedblockstorage.setExtSkylightValue(j, i1 & 15, k, l);
                                    this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + j, i1, (this.zPosition << 4) + k);
                                }
                            }

                            --i1;
                        }
                        while (i1 > 0 && l > 0);
                    }

                    ++k;
                    break;
                }
            }
        }

        this.isModified = true;

        for (j = 0; j < 16; ++j)
        {
            for (k = 0; k < 16; ++k)
            {
                this.propagateSkylightOcclusion(j, k);
            }
        }
    }

    /**
     * Propagates a given sky-visible block's light value downward and upward to neighboring blocks as necessary.
     */
    private void propagateSkylightOcclusion(int par1, int par2)
    {
        this.updateSkylightColumns[par1 + par2 * 16] = true;
        this.isGapLightingUpdated = true;
    }

    /**
     * Runs delayed skylight updates.
     */
    private void updateSkylight_do()
    {
        this.worldObj.theProfiler.startSection("recheckGaps");

        if (this.worldObj.doChunksNearChunkExist(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8, 16))
        {
            for (int i = 0; i < 16; ++i)
            {
                for (int j = 0; j < 16; ++j)
                {
                    if (this.updateSkylightColumns[i + j * 16])
                    {
                        this.updateSkylightColumns[i + j * 16] = false;
                        int k = this.getHeightValue(i, j);
                        int l = this.xPosition * 16 + i;
                        int i1 = this.zPosition * 16 + j;
                        int j1 = this.worldObj.getChunkHeightMapMinimum(l - 1, i1);
                        int k1 = this.worldObj.getChunkHeightMapMinimum(l + 1, i1);
                        int l1 = this.worldObj.getChunkHeightMapMinimum(l, i1 - 1);
                        int i2 = this.worldObj.getChunkHeightMapMinimum(l, i1 + 1);

                        if (k1 < j1)
                        {
                            j1 = k1;
                        }

                        if (l1 < j1)
                        {
                            j1 = l1;
                        }

                        if (i2 < j1)
                        {
                            j1 = i2;
                        }

                        this.checkSkylightNeighborHeight(l, i1, j1);
                        this.checkSkylightNeighborHeight(l - 1, i1, k);
                        this.checkSkylightNeighborHeight(l + 1, i1, k);
                        this.checkSkylightNeighborHeight(l, i1 - 1, k);
                        this.checkSkylightNeighborHeight(l, i1 + 1, k);
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Checks the height of a block next to a sky-visible block and schedules a lighting update as necessary.
     */
    private void checkSkylightNeighborHeight(int par1, int par2, int par3)
    {
        int l = this.worldObj.getHeightValue(par1, par2);

        if (l > par3)
        {
            this.updateSkylightNeighborHeight(par1, par2, par3, l + 1);
        }
        else if (l < par3)
        {
            this.updateSkylightNeighborHeight(par1, par2, l, par3 + 1);
        }
    }

    private void updateSkylightNeighborHeight(int par1, int par2, int par3, int par4)
    {
        if (par4 > par3 && this.worldObj.doChunksNearChunkExist(par1, 0, par2, 16))
        {
            for (int i1 = par3; i1 < par4; ++i1)
            {
                this.worldObj.updateLightByType(EnumSkyBlock.Sky, par1, i1, par2);
            }

            this.isModified = true;
        }
    }

    /**
     * Initiates the recalculation of both the block-light and sky-light for a given block inside a chunk.
     */
    private void relightBlock(int par1, int par2, int par3)
    {
        int l = this.heightMap[par3 << 4 | par1] & 255;
        int i1 = l;

        if (par2 > l)
        {
            i1 = par2;
        }

        while (i1 > 0 && this.getBlockLightOpacity(par1, i1 - 1, par3) == 0)
        {
            --i1;
        }

        if (i1 != l)
        {
            this.worldObj.markBlocksDirtyVertical(par1 + this.xPosition * 16, par3 + this.zPosition * 16, i1, l);
            this.heightMap[par3 << 4 | par1] = i1;
            int j1 = this.xPosition * 16 + par1;
            int k1 = this.zPosition * 16 + par3;
            int l1;
            int i2;

            if (!this.worldObj.provider.hasNoSky)
            {
                ExtendedBlockStorage extendedblockstorage;

                if (i1 < l)
                {
                    for (l1 = i1; l1 < l; ++l1)
                    {
                        extendedblockstorage = this.storageArrays[l1 >> 4];

                        if (extendedblockstorage != null)
                        {
                            extendedblockstorage.setExtSkylightValue(par1, l1 & 15, par3, 15);
                            this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + par1, l1, (this.zPosition << 4) + par3);
                        }
                    }
                }
                else
                {
                    for (l1 = l; l1 < i1; ++l1)
                    {
                        extendedblockstorage = this.storageArrays[l1 >> 4];

                        if (extendedblockstorage != null)
                        {
                            extendedblockstorage.setExtSkylightValue(par1, l1 & 15, par3, 0);
                            this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + par1, l1, (this.zPosition << 4) + par3);
                        }
                    }
                }

                l1 = 15;

                while (i1 > 0 && l1 > 0)
                {
                    --i1;
                    i2 = this.getBlockLightOpacity(par1, i1, par3);

                    if (i2 == 0)
                    {
                        i2 = 1;
                    }

                    l1 -= i2;

                    if (l1 < 0)
                    {
                        l1 = 0;
                    }

                    ExtendedBlockStorage extendedblockstorage1 = this.storageArrays[i1 >> 4];

                    if (extendedblockstorage1 != null)
                    {
                        extendedblockstorage1.setExtSkylightValue(par1, i1 & 15, par3, l1);
                    }
                }
            }

            l1 = this.heightMap[par3 << 4 | par1];
            i2 = l;
            int j2 = l1;

            if (l1 < l)
            {
                i2 = l1;
                j2 = l;
            }

            if (l1 < this.heightMapMinimum)
            {
                this.heightMapMinimum = l1;
            }

            if (!this.worldObj.provider.hasNoSky)
            {
                this.updateSkylightNeighborHeight(j1 - 1, k1, i2, j2);
                this.updateSkylightNeighborHeight(j1 + 1, k1, i2, j2);
                this.updateSkylightNeighborHeight(j1, k1 - 1, i2, j2);
                this.updateSkylightNeighborHeight(j1, k1 + 1, i2, j2);
                this.updateSkylightNeighborHeight(j1, k1, i2, j2);
            }

            this.isModified = true;
        }
    }

    public int getBlockLightOpacity(int par1, int par2, int par3)
    {
        int x = (xPosition << 4) + par1;
        int z = (zPosition << 4) + par3;
        Block block = Block.blocksList[getBlockID(par1, par2, par3)];
        return (block == null ? 0 : block.getLightOpacity(worldObj, x, par2, z));
    }

    /**
     * Return the ID of a block in the chunk.
     */
    public int getBlockID(int par1, int par2, int par3)
    {
        if (par2 >> 4 >= this.storageArrays.length)
        {
            return 0;
        }
        else
        {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[par2 >> 4];
            return extendedblockstorage != null ? extendedblockstorage.getExtBlockID(par1, par2 & 15, par3) : 0;
        }
    }

    /**
     * Return the metadata corresponding to the given coordinates inside a chunk.
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 >> 4 >= this.storageArrays.length)
        {
            return 0;
        }
        else
        {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[par2 >> 4];
            return extendedblockstorage != null ? extendedblockstorage.getExtBlockMetadata(par1, par2 & 15, par3) : 0;
        }
    }

    /**
     * Sets a blockID of a position within a chunk with metadata. Args: x, y, z, blockID, metadata
     */
    public boolean setBlockIDWithMetadata(int par1, int par2, int par3, int par4, int par5)
    {
        int j1 = par3 << 4 | par1;

        if (par2 >= this.precipitationHeightMap[j1] - 1)
        {
            this.precipitationHeightMap[j1] = -999;
        }

        int k1 = this.heightMap[j1];
        int l1 = this.getBlockID(par1, par2, par3);
        int i2 = this.getBlockMetadata(par1, par2, par3);

        if (l1 == par4 && i2 == par5)
        {
            return false;
        }
        else
        {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[par2 >> 4];
            boolean flag = false;

            if (extendedblockstorage == null)
            {
                if (par4 == 0)
                {
                    return false;
                }

                extendedblockstorage = this.storageArrays[par2 >> 4] = new ExtendedBlockStorage(par2 >> 4 << 4, !this.worldObj.provider.hasNoSky);
                flag = par2 >= k1;
            }

            int j2 = this.xPosition * 16 + par1;
            int k2 = this.zPosition * 16 + par3;

            if (l1 != 0 && !this.worldObj.isRemote)
            {
                Block.blocksList[l1].onSetBlockIDWithMetaData(this.worldObj, j2, par2, k2, i2);
            }

            extendedblockstorage.setExtBlockID(par1, par2 & 15, par3, par4);

            if (l1 != 0)
            {
                if (!this.worldObj.isRemote)
                {
                    Block.blocksList[l1].breakBlock(this.worldObj, j2, par2, k2, l1, i2);
                }
                else if (Block.blocksList[l1] != null && Block.blocksList[l1].hasTileEntity(i2))
                {
                    TileEntity te = worldObj.getBlockTileEntity(j2, par2, k2);
                    if (te != null && te.shouldRefresh(l1, par4, i2, par5, worldObj, j2, par2, k2))
                    {
                        this.worldObj.removeBlockTileEntity(j2, par2, k2);
                    }
                }
            }

            if (extendedblockstorage.getExtBlockID(par1, par2 & 15, par3) != par4)
            {
                return false;
            }
            else
            {
                extendedblockstorage.setExtBlockMetadata(par1, par2 & 15, par3, par5);

                if (flag)
                {
                    this.generateSkylightMap();
                }
                else
                {
                    if (getBlockLightOpacity(par1, par2, par3) > 0)
                    {
                        if (par2 >= k1)
                        {
                            this.relightBlock(par1, par2 + 1, par3);
                        }
                    }
                    else if (par2 == k1 - 1)
                    {
                        this.relightBlock(par1, par2, par3);
                    }

                    this.propagateSkylightOcclusion(par1, par3);
                }

                TileEntity tileentity;

                if (par4 != 0)
                {
                    if (!this.worldObj.isRemote)
                    {
                        Block.blocksList[par4].onBlockAdded(this.worldObj, j2, par2, k2);
                    }

                    if (Block.blocksList[par4] != null && Block.blocksList[par4].hasTileEntity(par5))
                    {
                        tileentity = this.getChunkBlockTileEntity(par1, par2, par3);

                        if (tileentity == null)
                        {
                            tileentity = Block.blocksList[par4].createTileEntity(this.worldObj, par5);
                            this.worldObj.setBlockTileEntity(j2, par2, k2, tileentity);
                        }

                        if (tileentity != null)
                        {
                            tileentity.updateContainingBlockInfo();
                            tileentity.blockMetadata = par5;
                        }
                    }
                }

                this.isModified = true;
                return true;
            }
        }
    }

    /**
     * Set the metadata of a block in the chunk
     */
    public boolean setBlockMetadata(int par1, int par2, int par3, int par4)
    {
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[par2 >> 4];

        if (extendedblockstorage == null)
        {
            return false;
        }
        else
        {
            int i1 = extendedblockstorage.getExtBlockMetadata(par1, par2 & 15, par3);

            if (i1 == par4)
            {
                return false;
            }
            else
            {
                this.isModified = true;
                extendedblockstorage.setExtBlockMetadata(par1, par2 & 15, par3, par4);
                int j1 = extendedblockstorage.getExtBlockID(par1, par2 & 15, par3);

                if (j1 > 0 && Block.blocksList[j1] != null && Block.blocksList[j1].hasTileEntity(par4))
                {
                    TileEntity tileentity = this.getChunkBlockTileEntity(par1, par2, par3);

                    if (tileentity != null)
                    {
                        tileentity.updateContainingBlockInfo();
                        tileentity.blockMetadata = par4;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Gets the amount of light saved in this block (doesn't adjust for daylight)
     */
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[par3 >> 4];
        return extendedblockstorage == null ? (this.canBlockSeeTheSky(par2, par3, par4) ? par1EnumSkyBlock.defaultLightValue : 0) : (par1EnumSkyBlock == EnumSkyBlock.Sky ? (this.worldObj.provider.hasNoSky ? 0 : extendedblockstorage.getExtSkylightValue(par2, par3 & 15, par4)) : (par1EnumSkyBlock == EnumSkyBlock.Block ? extendedblockstorage.getExtBlocklightValue(par2, par3 & 15, par4) : par1EnumSkyBlock.defaultLightValue));
    }

    /**
     * Sets the light value at the coordinate. If enumskyblock is set to sky it sets it in the skylightmap and if its a
     * block then into the blocklightmap. Args enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5)
    {
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[par3 >> 4];

        if (extendedblockstorage == null)
        {
            extendedblockstorage = this.storageArrays[par3 >> 4] = new ExtendedBlockStorage(par3 >> 4 << 4, !this.worldObj.provider.hasNoSky);
            this.generateSkylightMap();
        }

        this.isModified = true;

        if (par1EnumSkyBlock == EnumSkyBlock.Sky)
        {
            if (!this.worldObj.provider.hasNoSky)
            {
                extendedblockstorage.setExtSkylightValue(par2, par3 & 15, par4, par5);
            }
        }
        else if (par1EnumSkyBlock == EnumSkyBlock.Block)
        {
            extendedblockstorage.setExtBlocklightValue(par2, par3 & 15, par4, par5);
        }
    }

    /**
     * Gets the amount of light on a block taking into account sunlight
     */
    public int getBlockLightValue(int par1, int par2, int par3, int par4)
    {
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[par2 >> 4];

        if (extendedblockstorage == null)
        {
            return !this.worldObj.provider.hasNoSky && par4 < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - par4 : 0;
        }
        else
        {
            int i1 = this.worldObj.provider.hasNoSky ? 0 : extendedblockstorage.getExtSkylightValue(par1, par2 & 15, par3);

            if (i1 > 0)
            {
                isLit = true;
            }

            i1 -= par4;
            int j1 = extendedblockstorage.getExtBlocklightValue(par1, par2 & 15, par3);
            

            if (j1 > i1)
            {
                i1 = j1;
            }

            return i1;
        }
    }

    /**
     * Adds an entity to the chunk. Args: entity
     */
    public void addEntity(Entity par1Entity)
    {
        this.hasEntities = true;
        int i = MathHelper.floor_double(par1Entity.posX / 16.0D);
        int j = MathHelper.floor_double(par1Entity.posZ / 16.0D);

        if (i != this.xPosition || j != this.zPosition)
        {
            this.worldObj.getWorldLogAgent().logSevere("Wrong location! " + par1Entity);
            Thread.dumpStack();
        }

        int k = MathHelper.floor_double(par1Entity.posY / 16.0D);

        if (k < 0)
        {
            k = 0;
        }

        if (k >= this.entityLists.length)
        {
            k = this.entityLists.length - 1;
        }

        MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringChunk(par1Entity, this.xPosition, this.zPosition, par1Entity.chunkCoordX, par1Entity.chunkCoordZ));
        par1Entity.addedToChunk = true;
        par1Entity.chunkCoordX = this.xPosition;
        par1Entity.chunkCoordY = k;
        par1Entity.chunkCoordZ = this.zPosition;
        this.entityLists[k].add(par1Entity);
    }

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(Entity par1Entity)
    {
        this.removeEntityAtIndex(par1Entity, par1Entity.chunkCoordY);
    }

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(Entity par1Entity, int par2)
    {
        if (par2 < 0)
        {
            par2 = 0;
        }

        if (par2 >= this.entityLists.length)
        {
            par2 = this.entityLists.length - 1;
        }

        this.entityLists[par2].remove(par1Entity);
    }

    /**
     * Returns whether is not a block above this one blocking sight to the sky (done via checking against the heightmap)
     */
    public boolean canBlockSeeTheSky(int par1, int par2, int par3)
    {
        return par2 >= this.heightMap[par3 << 4 | par1];
    }

    /**
     * Gets the TileEntity for a given block in this chunk
     */
    public TileEntity getChunkBlockTileEntity(int par1, int par2, int par3)
    {
        ChunkPosition chunkposition = new ChunkPosition(par1, par2, par3);
        TileEntity tileentity = (TileEntity)this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid())
        {
            chunkTileEntityMap.remove(chunkposition);
            tileentity = null;
        }

        if (tileentity == null)
        {
            int l = this.getBlockID(par1, par2, par3);
            int meta = this.getBlockMetadata(par1, par2, par3);

            if (l <= 0 || !Block.blocksList[l].hasTileEntity(meta))
            {
                return null;
            }

            if (tileentity == null)
            {
                tileentity = Block.blocksList[l].createTileEntity(this.worldObj, meta);
                this.worldObj.setBlockTileEntity(this.xPosition * 16 + par1, par2, this.zPosition * 16 + par3, tileentity);
            }

            tileentity = (TileEntity)this.chunkTileEntityMap.get(chunkposition);
        }

        return tileentity;
    }

    /**
     * Adds a TileEntity to a chunk
     */
    public void addTileEntity(TileEntity par1TileEntity)
    {
        int i = par1TileEntity.xCoord - this.xPosition * 16;
        int j = par1TileEntity.yCoord;
        int k = par1TileEntity.zCoord - this.zPosition * 16;
        this.setChunkBlockTileEntity(i, j, k, par1TileEntity);

        if (this.isChunkLoaded)
        {
            this.worldObj.addTileEntity(par1TileEntity);
        }
    }

    /**
     * Sets the TileEntity for a given block in this chunk
     */
    public void setChunkBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        ChunkPosition chunkposition = new ChunkPosition(par1, par2, par3);
        par4TileEntity.setWorldObj(this.worldObj);
        par4TileEntity.xCoord = this.xPosition * 16 + par1;
        par4TileEntity.yCoord = par2;
        par4TileEntity.zCoord = this.zPosition * 16 + par3;

        Block block = Block.blocksList[getBlockID(par1, par2, par3)];
        if (block != null && block.hasTileEntity(getBlockMetadata(par1, par2, par3)))
        {
            if (this.chunkTileEntityMap.containsKey(chunkposition))
            {
                ((TileEntity)this.chunkTileEntityMap.get(chunkposition)).invalidate();
            }

            par4TileEntity.validate();
            this.chunkTileEntityMap.put(chunkposition, par4TileEntity);
        }
    }

    /**
     * Removes the TileEntity for a given block in this chunk
     */
    public void removeChunkBlockTileEntity(int par1, int par2, int par3)
    {
        ChunkPosition chunkposition = new ChunkPosition(par1, par2, par3);

        if (this.isChunkLoaded)
        {
            TileEntity tileentity = (TileEntity)this.chunkTileEntityMap.remove(chunkposition);

            if (tileentity != null)
            {
                tileentity.invalidate();
            }
        }
    }

    /**
     * Called when this Chunk is loaded by the ChunkProvider
     */
    public void onChunkLoad()
    {
        this.isChunkLoaded = true;
        this.worldObj.addTileEntity(this.chunkTileEntityMap.values());

        for (int i = 0; i < this.entityLists.length; ++i)
        {
            Iterator iterator = this.entityLists[i].iterator();

            while (iterator.hasNext())
            {
                Entity entity = (Entity)iterator.next();
                entity.func_110123_P();
            }

            this.worldObj.addLoadedEntities(this.entityLists[i]);
        }
        MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(this));
    }

    /**
     * Called when this Chunk is unloaded by the ChunkProvider
     */
    public void onChunkUnload()
    {
        this.isChunkLoaded = false;
        Iterator iterator = this.chunkTileEntityMap.values().iterator();

        while (iterator.hasNext())
        {
            TileEntity tileentity = (TileEntity)iterator.next();
            this.worldObj.markTileEntityForDespawn(tileentity);
        }

        for (int i = 0; i < this.entityLists.length; ++i)
        {
            this.worldObj.unloadEntities(this.entityLists[i]);
        }
        MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(this));
    }

    /**
     * Sets the isModified flag for this Chunk
     */
    public void setChunkModified()
    {
        this.isModified = true;
    }

    /**
     * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity
     * Args: entity, aabb, listToFill
     */
    public void getEntitiesWithinAABBForEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
    {
        int i = MathHelper.floor_double((par2AxisAlignedBB.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((par2AxisAlignedBB.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);

        if (i < 0)
        {
            i = 0;
            j = Math.max(i, j);
        }

        if (j >= this.entityLists.length)
        {
            j = this.entityLists.length - 1;
            i = Math.min(i, j);
        }

        for (int k = i; k <= j; ++k)
        {
            List list1 = this.entityLists[k];

            for (int l = 0; l < list1.size(); ++l)
            {
                Entity entity1 = (Entity)list1.get(l);

                if (entity1 != par1Entity && entity1.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(entity1)))
                {
                    par3List.add(entity1);
                    Entity[] aentity = entity1.getParts();

                    if (aentity != null)
                    {
                        for (int i1 = 0; i1 < aentity.length; ++i1)
                        {
                            entity1 = aentity[i1];

                            if (entity1 != par1Entity && entity1.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(entity1)))
                            {
                                par3List.add(entity1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all entities that can be assigned to the specified class. Args: entityClass, aabb, listToFill
     */
    public void getEntitiesOfTypeWithinAAAB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector)
    {
        int i = MathHelper.floor_double((par2AxisAlignedBB.minY - World.MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((par2AxisAlignedBB.maxY + World.MAX_ENTITY_RADIUS) / 16.0D);

        if (i < 0)
        {
            i = 0;
        }
        else if (i >= this.entityLists.length)
        {
            i = this.entityLists.length - 1;
        }

        if (j >= this.entityLists.length)
        {
            j = this.entityLists.length - 1;
        }
        else if (j < 0)
        {
            j = 0;
        }

        for (int k = i; k <= j; ++k)
        {
            List list1 = this.entityLists[k];

            for (int l = 0; l < list1.size(); ++l)
            {
                Entity entity = (Entity)list1.get(l);

                if (par1Class.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(par2AxisAlignedBB) && (par4IEntitySelector == null || par4IEntitySelector.isEntityApplicable(entity)))
                {
                    par3List.add(entity);
                }
            }
        }
    }

    /**
     * Returns true if this Chunk needs to be saved
     */
    public boolean needsSaving(boolean par1)
    {
        if (par1)
        {
            if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified)
            {
                return true;
            }
        }
        else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L)
        {
            return true;
        }

        return this.isModified;
    }

    public Random getRandomWithSeed(long par1)
    {
        return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ par1);
    }

    public boolean isEmpty()
    {
        return false;
    }

    public void populateChunk(IChunkProvider par1IChunkProvider, IChunkProvider par2IChunkProvider, int par3, int par4)
    {
        if (!this.isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3, par4);
        }

        if (par1IChunkProvider.chunkExists(par3 - 1, par4) && !par1IChunkProvider.provideChunk(par3 - 1, par4).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1) && par1IChunkProvider.chunkExists(par3, par4 + 1) && par1IChunkProvider.chunkExists(par3 - 1, par4 + 1))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4);
        }

        if (par1IChunkProvider.chunkExists(par3, par4 - 1) && !par1IChunkProvider.provideChunk(par3, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4 - 1) && par1IChunkProvider.chunkExists(par3 + 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3, par4 - 1);
        }

        if (par1IChunkProvider.chunkExists(par3 - 1, par4 - 1) && !par1IChunkProvider.provideChunk(par3 - 1, par4 - 1).isTerrainPopulated && par1IChunkProvider.chunkExists(par3, par4 - 1) && par1IChunkProvider.chunkExists(par3 - 1, par4))
        {
            par1IChunkProvider.populate(par2IChunkProvider, par3 - 1, par4 - 1);
        }
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not already stored.
     */
    public int getPrecipitationHeight(int par1, int par2)
    {
        int k = par1 | par2 << 4;
        int l = this.precipitationHeightMap[k];

        if (l == -999)
        {
            int i1 = this.getTopFilledSegment() + 15;
            l = -1;

            while (i1 > 0 && l == -1)
            {
                int j1 = this.getBlockID(par1, i1, par2);
                Material material = j1 == 0 ? Material.air : Block.blocksList[j1].blockMaterial;

                if (!material.blocksMovement() && !material.isLiquid())
                {
                    --i1;
                }
                else
                {
                    l = i1 + 1;
                }
            }

            this.precipitationHeightMap[k] = l;
        }

        return l;
    }

    /**
     * Checks whether skylight needs updated; if it does, calls updateSkylight_do
     */
    public void updateSkylight()
    {
        if (this.isGapLightingUpdated && !this.worldObj.provider.hasNoSky)
        {
            this.updateSkylight_do();
        }
    }

    /**
     * Gets a ChunkCoordIntPair representing the Chunk's position.
     */
    public ChunkCoordIntPair getChunkCoordIntPair()
    {
        return new ChunkCoordIntPair(this.xPosition, this.zPosition);
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
     * (true) or not (false).
     */
    public boolean getAreLevelsEmpty(int par1, int par2)
    {
        if (par1 < 0)
        {
            par1 = 0;
        }

        if (par2 >= 256)
        {
            par2 = 255;
        }

        for (int k = par1; k <= par2; k += 16)
        {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[k >> 4];

            if (extendedblockstorage != null && !extendedblockstorage.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public void setStorageArrays(ExtendedBlockStorage[] par1ArrayOfExtendedBlockStorage)
    {
        this.storageArrays = par1ArrayOfExtendedBlockStorage;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Initialise this chunk with new binary data
     */
    public void fillChunk(byte[] par1ArrayOfByte, int par2, int par3, boolean par4)
    {
        Iterator iterator = chunkTileEntityMap.values().iterator();
        while(iterator.hasNext())
        {
            TileEntity tileEntity = (TileEntity)iterator.next();
            tileEntity.updateContainingBlockInfo();
            tileEntity.getBlockMetadata();
            tileEntity.getBlockType();
        }

        int k = 0;
        boolean flag1 = !this.worldObj.provider.hasNoSky;
        int l;

        for (l = 0; l < this.storageArrays.length; ++l)
        {
            if ((par2 & 1 << l) != 0)
            {
                if (this.storageArrays[l] == null)
                {
                    this.storageArrays[l] = new ExtendedBlockStorage(l << 4, flag1);
                }

                byte[] abyte1 = this.storageArrays[l].getBlockLSBArray();
                System.arraycopy(par1ArrayOfByte, k, abyte1, 0, abyte1.length);
                k += abyte1.length;
            }
            else if (par4 && this.storageArrays[l] != null)
            {
                this.storageArrays[l] = null;
            }
        }

        NibbleArray nibblearray;

        for (l = 0; l < this.storageArrays.length; ++l)
        {
            if ((par2 & 1 << l) != 0 && this.storageArrays[l] != null)
            {
                nibblearray = this.storageArrays[l].getMetadataArray();
                System.arraycopy(par1ArrayOfByte, k, nibblearray.data, 0, nibblearray.data.length);
                k += nibblearray.data.length;
            }
        }

        for (l = 0; l < this.storageArrays.length; ++l)
        {
            if ((par2 & 1 << l) != 0 && this.storageArrays[l] != null)
            {
                nibblearray = this.storageArrays[l].getBlocklightArray();
                System.arraycopy(par1ArrayOfByte, k, nibblearray.data, 0, nibblearray.data.length);
                k += nibblearray.data.length;
            }
        }

        if (flag1)
        {
            for (l = 0; l < this.storageArrays.length; ++l)
            {
                if ((par2 & 1 << l) != 0 && this.storageArrays[l] != null)
                {
                    nibblearray = this.storageArrays[l].getSkylightArray();
                    System.arraycopy(par1ArrayOfByte, k, nibblearray.data, 0, nibblearray.data.length);
                    k += nibblearray.data.length;
                }
            }
        }

        for (l = 0; l < this.storageArrays.length; ++l)
        {
            if ((par3 & 1 << l) != 0)
            {
                if (this.storageArrays[l] == null)
                {
                    k += 2048;
                }
                else
                {
                    nibblearray = this.storageArrays[l].getBlockMSBArray();

                    if (nibblearray == null)
                    {
                        nibblearray = this.storageArrays[l].createBlockMSBArray();
                    }

                    System.arraycopy(par1ArrayOfByte, k, nibblearray.data, 0, nibblearray.data.length);
                    k += nibblearray.data.length;
                }
            }
            else if (par4 && this.storageArrays[l] != null && this.storageArrays[l].getBlockMSBArray() != null)
            {
                this.storageArrays[l].clearMSBArray();
            }
        }

        if (par4)
        {
            System.arraycopy(par1ArrayOfByte, k, this.blockBiomeArray, 0, this.blockBiomeArray.length);
            int i1 = k + this.blockBiomeArray.length;
        }

        for (l = 0; l < this.storageArrays.length; ++l)
        {
            if (this.storageArrays[l] != null && (par2 & 1 << l) != 0)
            {
                this.storageArrays[l].removeInvalidBlocks();
            }
        }

        this.generateHeightMap();

        List<TileEntity> invalidList = new ArrayList<TileEntity>();
        iterator = chunkTileEntityMap.values().iterator();
        while (iterator.hasNext())
        {
            TileEntity tileEntity = (TileEntity)iterator.next();
            int x = tileEntity.xCoord & 15;
            int y = tileEntity.yCoord;
            int z = tileEntity.zCoord & 15;
            Block block = tileEntity.getBlockType();
            if (block == null || block.blockID != getBlockID(x, y, z) || tileEntity.getBlockMetadata() != getBlockMetadata(x, y, z))
            {
                invalidList.add(tileEntity);
            }
            tileEntity.updateContainingBlockInfo();
        }

        for (TileEntity tileEntity : invalidList)
        {
            tileEntity.invalidate();
        }
    }

    /**
     * This method retrieves the biome at a set of coordinates
     */
    public BiomeGenBase getBiomeGenForWorldCoords(int par1, int par2, WorldChunkManager par3WorldChunkManager)
    {
        int k = this.blockBiomeArray[par2 << 4 | par1] & 255;

        if (k == 255)
        {
            BiomeGenBase biomegenbase = par3WorldChunkManager.getBiomeGenAt((this.xPosition << 4) + par1, (this.zPosition << 4) + par2);
            k = biomegenbase.biomeID;
            this.blockBiomeArray[par2 << 4 | par1] = (byte)(k & 255);
        }

        return BiomeGenBase.biomeList[k] == null ? BiomeGenBase.plains : BiomeGenBase.biomeList[k];
    }

    /**
     * Returns an array containing a 16x16 mapping on the X/Z of block positions in this Chunk to biome IDs.
     */
    public byte[] getBiomeArray()
    {
        return this.blockBiomeArray;
    }

    /**
     * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane of block positions in this Chunk to
     * biome IDs.
     */
    public void setBiomeArray(byte[] par1ArrayOfByte)
    {
        this.blockBiomeArray = par1ArrayOfByte;
    }

    /**
     * Resets the relight check index to 0 for this Chunk.
     */
    public void resetRelightChecks()
    {
        this.queuedLightChecks = 0;
    }

    /**
     * Called once-per-chunk-per-tick, and advances the round-robin relight check index per-storage-block by up to 8
     * blocks at a time. In a worst-case scenario, can potentially take up to 1.6 seconds, calculated via
     * (4096/(8*16))/20, to re-check all blocks in a chunk, which could explain both lagging light updates in certain
     * cases as well as Nether relight
     */
    public void enqueueRelightChecks()
    {
        for (int i = 0; i < 8; ++i)
        {
            if (this.queuedLightChecks >= 4096)
            {
                return;
            }

            int j = this.queuedLightChecks % 16;
            int k = this.queuedLightChecks / 16 % 16;
            int l = this.queuedLightChecks / 256;
            ++this.queuedLightChecks;
            int i1 = (this.xPosition << 4) + k;
            int j1 = (this.zPosition << 4) + l;

            for (int k1 = 0; k1 < 16; ++k1)
            {
                int l1 = (j << 4) + k1;

                if (this.storageArrays[j] == null && (k1 == 0 || k1 == 15 || k == 0 || k == 15 || l == 0 || l == 15) || this.storageArrays[j] != null && this.storageArrays[j].getExtBlockID(k, k1, l) == 0)
                {
                    if (Block.lightValue[this.worldObj.getBlockId(i1, l1 - 1, j1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1, l1 - 1, j1);
                    }

                    if (Block.lightValue[this.worldObj.getBlockId(i1, l1 + 1, j1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1, l1 + 1, j1);
                    }

                    if (Block.lightValue[this.worldObj.getBlockId(i1 - 1, l1, j1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1 - 1, l1, j1);
                    }

                    if (Block.lightValue[this.worldObj.getBlockId(i1 + 1, l1, j1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1 + 1, l1, j1);
                    }

                    if (Block.lightValue[this.worldObj.getBlockId(i1, l1, j1 - 1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1, l1, j1 - 1);
                    }

                    if (Block.lightValue[this.worldObj.getBlockId(i1, l1, j1 + 1)] > 0)
                    {
                        this.worldObj.updateAllLightTypes(i1, l1, j1 + 1);
                    }

                    this.worldObj.updateAllLightTypes(i1, l1, j1);
                }
            }
        }
    }

    /** FORGE: Used to remove only invalid TileEntities */
    public void cleanChunkBlockTileEntity(int x, int y, int z)
    {
        ChunkPosition position = new ChunkPosition(x, y, z);
        if (isChunkLoaded)
        {
            TileEntity entity = (TileEntity)chunkTileEntityMap.get(position);
            if (entity != null && entity.isInvalid())
            {
                chunkTileEntityMap.remove(position);
            }
        }
    }
}
