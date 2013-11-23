package net.minecraft.world;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

import com.google.common.collect.ImmutableSetMultimap;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeDummyContainer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.WorldSpecificSaveHandler;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraft.entity.EnumCreatureType;

public abstract class World implements IBlockAccess
{
    /**
     * Used in the getEntitiesWithinAABB functions to expand the search area for entities.
     * Modders should change this variable to a higher value if it is less then the radius
     * of one of there entities.
     */
    public static double MAX_ENTITY_RADIUS = 2.0D;

    public final MapStorage perWorldStorage;

    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    public boolean scheduledUpdatesAreImmediate;

    /** A list of all Entities in all currently-loaded chunks */
    public List loadedEntityList = new ArrayList();
    protected List unloadedEntityList = new ArrayList();

    /** A list of all TileEntities in all currently-loaded chunks */
    public List loadedTileEntityList = new ArrayList();
    private List addedTileEntityList = new ArrayList();

    /** Entities marked for removal. */
    private List entityRemoval = new ArrayList();

    /** Array list of players in the world. */
    public List playerEntities = new ArrayList();

    /** a list of all the lightning entities */
    public List weatherEffects = new ArrayList();
    private long cloudColour = 16777215L;

    /** How much light is subtracted from full daylight */
    public int skylightSubtracted;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int DIST_HASH_MAGIC = 1013904223;
    public float prevRainingStrength;
    public float rainingStrength;
    public float prevThunderingStrength;
    public float thunderingStrength;

    /**
     * Set to 2 whenever a lightning bolt is generated in SSP. Decrements if > 0 in updateWeather(). Value appears to be
     * unused.
     */
    public int lastLightningBolt;

    /** Option > Difficulty setting (0 - 3) */
    public int difficultySetting;

    /** RNG for World. */
    public Random rand = new Random();

    /** The WorldProvider instance that World uses. */
    public final WorldProvider provider;
    protected List worldAccesses = new ArrayList();

    /** Handles chunk operations and caching */
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    protected WorldInfo worldInfo;

    /** Boolean that is set to true when trying to find a spawn point */
    public boolean findingSpawnPoint;
    public MapStorage mapStorage;
    public VillageCollection villageCollectionObj;
    protected final VillageSiege villageSiegeObj = new VillageSiege(this);
    public final Profiler theProfiler;

    /** The world-local pool of vectors */
    private final Vec3Pool vecPool = new Vec3Pool(300, 2000);
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();

    /** The log agent for this world. */
    private final ILogAgent worldLogAgent;
    private ArrayList collidingBoundingBoxes = new ArrayList();
    private boolean scanningTileEntities;

    /** indicates if enemies are spawned or not */
    protected boolean spawnHostileMobs = true;

    /** A flag indicating whether we should spawn peaceful mobs. */
    protected boolean spawnPeacefulMobs = true;

    /** Positions to update */
    public Set activeChunkSet = new HashSet();

    /** number of ticks until the next random ambients play */
    private int ambientTickCountdown;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    long[] lightUpdateBlockList;

    /** This is set to true for client worlds, and false for server worlds. */
    public boolean isRemote;

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    public BiomeGenBase getBiomeGenForCoords(int par1, int par2)
    {
        return provider.getBiomeGenForCoords(par1, par2);
    }

    public BiomeGenBase getBiomeGenForCoordsBody(int par1, int par2)
    {
        if (this.blockExists(par1, 0, par2))
        {
            Chunk chunk = this.getChunkFromBlockCoords(par1, par2);

            if (chunk != null)
            {
                return chunk.getBiomeGenForWorldCoords(par1 & 15, par2 & 15, this.provider.worldChunkMgr);
            }
        }

        return this.provider.worldChunkMgr.getBiomeGenAt(par1, par2);
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.provider.worldChunkMgr;
    }

    @SideOnly(Side.CLIENT)
    public World(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent)
    {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new long[32768];
        this.saveHandler = par1ISaveHandler;
        this.theProfiler = par5Profiler;
        this.worldInfo = new WorldInfo(par4WorldSettings, par2Str);
        this.provider = par3WorldProvider;
        perWorldStorage = new MapStorage((ISaveHandler)null);
        this.worldLogAgent = par6ILogAgent;

    }

    // Broken up so that the WorldClient gets the chance to set the mapstorage object before the dimension initializes
    @SideOnly(Side.CLIENT)
    protected void finishSetup()
    {
        VillageCollection villagecollection = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");

        if (villagecollection == null)
        {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData("villages", this.villageCollectionObj);
        }
        else
        {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.func_82566_a(this);
        }
        // Guarantee the dimension ID was not reset by the provider
        int providerDim = this.provider.dimensionId;
        this.provider.registerWorld(this);
        this.provider.dimensionId = providerDim;
        this.chunkProvider = this.createChunkProvider();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    public World(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider, Profiler par5Profiler, ILogAgent par6ILogAgent)
    {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.lightUpdateBlockList = new long[32768];
        this.saveHandler = par1ISaveHandler;
        this.theProfiler = par5Profiler;
        this.mapStorage = getMapStorage(par1ISaveHandler);
        this.worldLogAgent = par6ILogAgent;
        this.worldInfo = par1ISaveHandler.loadWorldInfo();

        if (par4WorldProvider != null)
        {
            this.provider = par4WorldProvider;
        }
        else if (this.worldInfo != null && this.worldInfo.getVanillaDimension() != 0)
        {
            this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getVanillaDimension());
        }
        else
        {
            this.provider = WorldProvider.getProviderForDimension(0);
        }

        if (this.worldInfo == null)
        {
            this.worldInfo = new WorldInfo(par3WorldSettings, par2Str);
        }
        else
        {
            this.worldInfo.setWorldName(par2Str);
        }

        this.provider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        if (this instanceof WorldServer)
        {
            this.perWorldStorage = new MapStorage(new WorldSpecificSaveHandler((WorldServer)this, par1ISaveHandler));
        }
        else
        {
            this.perWorldStorage = new MapStorage((ISaveHandler)null);
        }

        if (!this.worldInfo.isInitialized())
        {
            try
            {
                this.initialize(par3WorldSettings);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

                try
                {
                    this.addWorldInfoToCrashReport(crashreport);
                }
                catch (Throwable throwable1)
                {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldInfo.setServerInitialized(true);
        }

        VillageCollection villagecollection = (VillageCollection)perWorldStorage.loadData(VillageCollection.class, "villages");

        if (villagecollection == null)
        {
            this.villageCollectionObj = new VillageCollection(this);
            this.perWorldStorage.setData("villages", this.villageCollectionObj);
        }
        else
        {
            this.villageCollectionObj = villagecollection;
            this.villageCollectionObj.func_82566_a(this);
        }

        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    private static MapStorage s_mapStorage;
    private static ISaveHandler s_savehandler;
    //Provides a solution for different worlds getting different copies of the same data, potentially rewriting the data or causing race conditions/stale data
    //Buildcraft has suffered from the issue this fixes.  If you load the same data from two different worlds they can get two different copies of the same object, thus the last saved gets final say.
    private MapStorage getMapStorage(ISaveHandler savehandler)
    {
        if (s_savehandler != savehandler || s_mapStorage == null)
        {
            s_mapStorage = new MapStorage(savehandler);
            s_savehandler = savehandler;
        }
        return s_mapStorage;
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider createChunkProvider();

    protected void initialize(WorldSettings par1WorldSettings)
    {
        this.worldInfo.setServerInitialized(true);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation()
    {
        this.setSpawnLocation(8, 64, 8);
    }

    /**
     * Returns the block ID of the first block at this (x,z) location with air above it, searching from sea level
     * upwards.
     */
    public int getFirstUncoveredBlock(int par1, int par2)
    {
        int k;

        for (k = 63; !this.isAirBlock(par1, k + 1, par2); ++k)
        {
            ;
        }

        return this.getBlockId(par1, k, par2);
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getBlockId(int par1, int par2, int par3)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return 0;
            }
            else if (par2 >= 256)
            {
                return 0;
            }
            else
            {
                Chunk chunk = null;

                try
                {
                    chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                    return chunk.getBlockID(par1 & 15, par2, par3 & 15);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception getting block type in world");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Requested block coordinates");
                    crashreportcategory.addCrashSection("Found chunk", Boolean.valueOf(chunk == null));
                    crashreportcategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(par1, par2, par3));
                    throw new ReportedException(crashreport);
                }
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(int par1, int par2, int par3)
    {
        int id = getBlockId(par1, par2, par3);
        return id == 0 || Block.blocksList[id] == null || Block.blocksList[id].isAirBlock(this, par1, par2, par3);
    }

    /**
     * Checks if a block at a given position should have a tile entity.
     */
    public boolean blockHasTileEntity(int par1, int par2, int par3)
    {
        int l = this.getBlockId(par1, par2, par3);
        int meta = this.getBlockMetadata(par1, par2, par3);
        return Block.blocksList[l] != null && Block.blocksList[l].hasTileEntity(meta);
    }

    /**
     * Returns the render type of the block at the given coordinate.
     */
    public int blockGetRenderType(int par1, int par2, int par3)
    {
        int l = this.getBlockId(par1, par2, par3);
        return Block.blocksList[l] != null ? Block.blocksList[l].getRenderType() : -1;
    }

    /**
     * Returns whether a block exists at world coordinates x, y, z
     */
    public boolean blockExists(int par1, int par2, int par3)
    {
        return par2 >= 0 && par2 < 256 ? this.chunkExists(par1 >> 4, par3 >> 4) : false;
    }

    /**
     * Checks if any of the chunks within distance (argument 4) blocks of the given block exist
     */
    public boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4)
    {
        return this.checkChunksExist(par1 - par4, par2 - par4, par3 - par4, par1 + par4, par2 + par4, par3 + par4);
    }

    /**
     * Checks between a min and max all the chunks inbetween actually exist. Args: minX, minY, minZ, maxX, maxY, maxZ
     */
    public boolean checkChunksExist(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (par5 >= 0 && par2 < 256)
        {
            par1 >>= 4;
            par3 >>= 4;
            par4 >>= 4;
            par6 >>= 4;

            for (int k1 = par1; k1 <= par4; ++k1)
            {
                for (int l1 = par3; l1 <= par6; ++l1)
                {
                    if (!this.chunkExists(k1, l1))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether a chunk exists at chunk coordinates x, y
     */
    protected boolean chunkExists(int par1, int par2)
    {
        return this.chunkProvider.chunkExists(par1, par2);
    }

    /**
     * Returns a chunk looked up by block coordinates. Args: x, z
     */
    public Chunk getChunkFromBlockCoords(int par1, int par2)
    {
        return this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    public Chunk getChunkFromChunkCoords(int par1, int par2)
    {
        return this.chunkProvider.provideChunk(par1, par2);
    }

    /**
     * Sets the block ID and metadata at a given location. Args: X, Y, Z, new block ID, new metadata, flags. Flag 1 will
     * cause a block update. Flag 2 will send the change to clients (you almost always want this). Flag 4 prevents the
     * block from being re-rendered, if this is a client world. Flags can be added together.
     */
    public boolean setBlock(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int k1 = 0;

                if ((par6 & 1) != 0)
                {
                    k1 = chunk.getBlockID(par1 & 15, par2, par3 & 15);
                }

                boolean flag = chunk.setBlockIDWithMetadata(par1 & 15, par2, par3 & 15, par4, par5);
                this.theProfiler.startSection("checkLight");
                this.updateAllLightTypes(par1, par2, par3);
                this.theProfiler.endSection();

                if (flag)
                {
                    if ((par6 & 2) != 0 && (!this.isRemote || (par6 & 4) == 0))
                    {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par6 & 1) != 0)
                    {
                        this.notifyBlockChange(par1, par2, par3, k1);
                        Block block = Block.blocksList[par4];

                        if (block != null && block.hasComparatorInputOverride())
                        {
                            this.func_96440_m(par1, par2, par3, par4);
                        }
                    }
                }

                return flag;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getBlockMaterial(int par1, int par2, int par3)
    {
        int l = this.getBlockId(par1, par2, par3);
        return l == 0 ? Material.air : Block.blocksList[l].blockMaterial;
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return 0;
            }
            else if (par2 >= 256)
            {
                return 0;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return chunk.getBlockMetadata(par1, par2, par3);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Sets the blocks metadata and if set will then notify blocks that this block changed, depending on the flag. Args:
     * x, y, z, metadata, flag. See setBlock for flag description
     */
    public boolean setBlockMetadataWithNotify(int par1, int par2, int par3, int par4, int par5)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int j1 = par1 & 15;
                int k1 = par3 & 15;
                boolean flag = chunk.setBlockMetadata(j1, par2, k1, par4);

                if (flag)
                {
                    int l1 = chunk.getBlockID(j1, par2, k1);

                    if ((par5 & 2) != 0 && (!this.isRemote || (par5 & 4) == 0))
                    {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par5 & 1) != 0)
                    {
                        this.notifyBlockChange(par1, par2, par3, l1);
                        Block block = Block.blocksList[l1];

                        if (block != null && block.hasComparatorInputOverride())
                        {
                            this.func_96440_m(par1, par2, par3, l1);
                        }
                    }
                }

                return flag;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a block to 0 and notifies relevant systems with the block change  Args: x, y, z
     */
    public boolean setBlockToAir(int par1, int par2, int par3)
    {
        return this.setBlock(par1, par2, par3, 0, 0, 3);
    }

    /**
     * Destroys a block and optionally drops items. Args: X, Y, Z, dropItems
     */
    public boolean destroyBlock(int par1, int par2, int par3, boolean par4)
    {
        int l = this.getBlockId(par1, par2, par3);

        if (l > 0)
        {
            int i1 = this.getBlockMetadata(par1, par2, par3);
            this.playAuxSFX(2001, par1, par2, par3, l + (i1 << 12));

            if (par4)
            {
                Block.blocksList[l].dropBlockAsItem(this, par1, par2, par3, i1, 0);
            }

            return this.setBlock(par1, par2, par3, 0, 0, 3);
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a block and notifies relevant systems with the block change  Args: x, y, z, blockID
     */
    public boolean setBlock(int par1, int par2, int par3, int par4)
    {
        return this.setBlock(par1, par2, par3, par4, 0, 3);
    }

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it only
     * if the ID or MD changes), including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(int par1, int par2, int par3)
    {
        for (int l = 0; l < this.worldAccesses.size(); ++l)
        {
            ((IWorldAccess)this.worldAccesses.get(l)).markBlockForUpdate(par1, par2, par3);
        }
    }

    /**
     * The block type change and need to notify other systems  Args: x, y, z, blockID
     */
    public void notifyBlockChange(int par1, int par2, int par3, int par4)
    {
        this.notifyBlocksOfNeighborChange(par1, par2, par3, par4);
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void markBlocksDirtyVertical(int par1, int par2, int par3, int par4)
    {
        int i1;

        if (par3 > par4)
        {
            i1 = par4;
            par4 = par3;
            par3 = i1;
        }

        if (!this.provider.hasNoSky)
        {
            for (i1 = par3; i1 <= par4; ++i1)
            {
                this.updateLightByType(EnumSkyBlock.Sky, par1, i1, par2);
            }
        }

        this.markBlockRangeForRenderUpdate(par1, par3, par2, par1, par4, par2);
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        for (int k1 = 0; k1 < this.worldAccesses.size(); ++k1)
        {
            ((IWorldAccess)this.worldAccesses.get(k1)).markBlockRangeForRenderUpdate(par1, par2, par3, par4, par5, par6);
        }
    }

    /**
     * Notifies neighboring blocks that this specified block changed  Args: x, y, z, blockID
     */
    public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4)
    {
        this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
        this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
    }

    /**
     * Calls notifyBlockOfNeighborChange on adjacent blocks, except the one on the given side. Args: X, Y, Z,
     * changingBlockID, side
     */
    public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4, int par5)
    {
        if (par5 != 4)
        {
            this.notifyBlockOfNeighborChange(par1 - 1, par2, par3, par4);
        }

        if (par5 != 5)
        {
            this.notifyBlockOfNeighborChange(par1 + 1, par2, par3, par4);
        }

        if (par5 != 0)
        {
            this.notifyBlockOfNeighborChange(par1, par2 - 1, par3, par4);
        }

        if (par5 != 1)
        {
            this.notifyBlockOfNeighborChange(par1, par2 + 1, par3, par4);
        }

        if (par5 != 2)
        {
            this.notifyBlockOfNeighborChange(par1, par2, par3 - 1, par4);
        }

        if (par5 != 3)
        {
            this.notifyBlockOfNeighborChange(par1, par2, par3 + 1, par4);
        }
    }

    /**
     * Notifies a block that one of its neighbor change to the specified type Args: x, y, z, blockID
     */
    public void notifyBlockOfNeighborChange(int par1, int par2, int par3, int par4)
    {
        if (!this.isRemote)
        {
            int i1 = this.getBlockId(par1, par2, par3);
            Block block = Block.blocksList[i1];

            if (block != null)
            {
                try
                {
                    block.onNeighborBlockChange(this, par1, par2, par3, par4);
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                    int j1;

                    try
                    {
                        j1 = this.getBlockMetadata(par1, par2, par3);
                    }
                    catch (Throwable throwable1)
                    {
                        j1 = -1;
                    }

                    crashreportcategory.addCrashSectionCallable("Source block type", new CallableLvl1(this, par4));
                    CrashReportCategory.addBlockCrashInfo(crashreportcategory, par1, par2, par3, i1, j1);
                    throw new ReportedException(crashreport);
                }
            }
        }
    }

    /**
     * Returns true if the given block will receive a scheduled tick in this tick. Args: X, Y, Z, blockID
     */
    public boolean isBlockTickScheduledThisTick(int par1, int par2, int par3, int par4)
    {
        return false;
    }

    /**
     * Checks if the specified block is able to see the sky
     */
    public boolean canBlockSeeTheSky(int par1, int par2, int par3)
    {
        return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).canBlockSeeTheSky(par1 & 15, par2, par3 & 15);
    }

    /**
     * Does the same as getBlockLightValue_do but without checking if its not a normal block
     */
    public int getFullBlockLightValue(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else
        {
            if (par2 >= 256)
            {
                par2 = 255;
            }

            return this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4).getBlockLightValue(par1 & 15, par2, par3 & 15, 0);
        }
    }

    /**
     * Gets the light value of a block location
     */
    public int getBlockLightValue(int par1, int par2, int par3)
    {
        return this.getBlockLightValue_do(par1, par2, par3, true);
    }

    /**
     * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag
     * that indicates if its a half step block to get the maximum light value of a direct neighboring block (left,
     * right, forward, back, and up)
     * 
     * Modified to only look at the blocks expected light, and not colors
     * 
     * CptSpaceToaster
     */
    public int getBlockLightValue_do(int par1, int par2, int par3, boolean par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par4)
            {
                int l = this.getBlockId(par1, par2, par3);

                if (Block.useNeighborBrightness[l])
                {
                    int i1 = this.getBlockLightValue_do(par1, par2 + 1, par3, false);
                    int j1 = this.getBlockLightValue_do(par1 + 1, par2, par3, false);
                    int k1 = this.getBlockLightValue_do(par1 - 1, par2, par3, false);
                    int l1 = this.getBlockLightValue_do(par1, par2, par3 + 1, false);
                    int i2 = this.getBlockLightValue_do(par1, par2, par3 - 1, false);

                    if ((j1&15) > (i1&15))
                    {
                        i1 = j1;
                    }

                    if ((k1&15) > (i1&15))
                    {
                        i1 = k1;
                    }

                    if ((l1&15) > (i1&15))
                    {
                        i1 = l1;
                    }

                    if ((i2&15) > (i1&15))
                    {
                        i1 = i2;
                    }

                    return i1;
                }
            }

            if (par2 < 0)
            {
                return 0;
            }
            else
            {
                if (par2 >= 256)
                {
                    par2 = 255;
                }

                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return chunk.getBlockLightValue(par1, par2, par3, this.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHeightValue(int par1, int par2)
    {
        if (par1 >= -30000000 && par2 >= -30000000 && par1 < 30000000 && par2 < 30000000)
        {
            if (!this.chunkExists(par1 >> 4, par2 >> 4))
            {
                return 0;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
                return chunk.getHeightValue(par1 & 15, par2 & 15);
            }
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets the heightMapMinimum field of the given chunk, or 0 if the chunk is not loaded. Coords are in blocks. Args:
     * X, Z
     */
    public int getChunkHeightMapMinimum(int par1, int par2)
    {
        if (par1 >= -30000000 && par2 >= -30000000 && par1 < 30000000 && par2 < 30000000)
        {
            if (!this.chunkExists(par1 >> 4, par2 >> 4))
            {
                return 0;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par2 >> 4);
                return chunk.heightMapMinimum;
            }
        }
        else
        {
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Brightness for SkyBlock.Sky is clear white and (through color computing it is assumed) DEPENDENT ON DAYTIME.
     * Brightness for SkyBlock.Block is yellowish and independent.
     * 
     * 
     * 
     * Modified by CptSpaceToaster
     * 
     * kinda...
     */
    public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (this.provider.hasNoSky && par1EnumSkyBlock == EnumSkyBlock.Sky)
        {
            return 0;
        }
        else
        {
            if (par3 < 0)
            {
                par3 = 0;
            }

            if (par3 >= 256)
            {
                return par1EnumSkyBlock.defaultLightValue;
            }
            else if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000)
            {
                int l = par2 >> 4;
                int i1 = par4 >> 4;

                if (!this.chunkExists(l, i1))
                {
                    return par1EnumSkyBlock.defaultLightValue;
                }
                else if (Block.useNeighborBrightness[this.getBlockId(par2, par3, par4)])
                {
                    int j1 = this.getSavedLightValue(par1EnumSkyBlock, par2, par3 + 1, par4);
                    int k1 = this.getSavedLightValue(par1EnumSkyBlock, par2 + 1, par3, par4);
                    int l1 = this.getSavedLightValue(par1EnumSkyBlock, par2 - 1, par3, par4);
                    int i2 = this.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4 + 1);
                    int j2 = this.getSavedLightValue(par1EnumSkyBlock, par2, par3, par4 - 1);
                    
                    //TODO: Fix this, it doesn't mess with colors.
                    if (k1 > j1)
                    {
                        j1 = k1;
                    }

                    if (l1 > j1)
                    {
                        j1 = l1;
                    }

                    if (i2 > j1)
                    {
                        j1 = i2;
                    }

                    if (j2 > j1)
                    {
                        j1 = j2;
                    }
                    
                    return j1;
                }
                else
                {
                    Chunk chunk = this.getChunkFromChunkCoords(l, i1);
                    return chunk.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
                }
            }
            else
            {
                return par1EnumSkyBlock.defaultLightValue;
            }
        }
    }

    /**
     * Returns saved light value without taking into account the time of day.  Either looks in the sky light map or
     * block light map based on the enumSkyBlock arg.
     */
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000)
        {
            int l = par2 >> 4;
            int i1 = par4 >> 4;

            if (!this.chunkExists(l, i1))
            {
                return par1EnumSkyBlock.defaultLightValue;
            }
            else
            {
                Chunk chunk = this.getChunkFromChunkCoords(l, i1);
                return chunk.getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
            }
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    /**
     * Sets the light value either into the sky map or block map depending on if enumSkyBlock is set to sky or block.
     * Args: enumSkyBlock, x, y, z, lightValue
     */
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5)
    {
        if (par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 < 30000000)
        {
            if (par3 >= 0)
            {
                if (par3 < 256)
                {
                    if (this.chunkExists(par2 >> 4, par4 >> 4))
                    {
                        Chunk chunk = this.getChunkFromChunkCoords(par2 >> 4, par4 >> 4);
                        chunk.setLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15, par5);

                        for (int i1 = 0; i1 < this.worldAccesses.size(); ++i1)
                        {
                            ((IWorldAccess)this.worldAccesses.get(i1)).markBlockForRenderUpdate(par2, par3, par4);
                        }
                    }
                }
            }
        }
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(int par1, int par2, int par3)
    {
        for (int l = 0; l < this.worldAccesses.size(); ++l)
        {
            ((IWorldAccess)this.worldAccesses.get(l)).markBlockForRenderUpdate(par1, par2, par3);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    public int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

        par4 = ((par4 & 15)			|
          	   ((par4 & 480) >> 1) 	|
         	   ((par4 & 15360) >> 2)|
         	   ((par4 & 491520) >> 3) );
        
        j1 =   ((j1 & 15)			|
           	   ((j1 & 480) >> 1) 	|
          	   ((j1 & 15360) >> 2)	|
          	   ((j1 & 491520) >> 3) );
        
        if (j1 < par4)
        {
        	j1 = par4;
        }
        
//        if (j1 != 0)
//        	System.out.println("(i1<<20 | j1<<4): " + (i1<<20 | j1<<4));
        
        return i1 << 20 | j1 << 4;
    }

    @SideOnly(Side.CLIENT)
    public float getBrightness(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getBlockLightValue(par1, par2, par3);

        par4 = ((par4 & 15)			|
           	   ((par4 & 480) >> 1) 	|
          	   ((par4 & 15360) >> 2)|
          	   ((par4 & 491520) >> 3) );
         
         i1 =  ((i1 & 15)			|
        	   ((i1 & 480) >> 1) 	|
           	   ((i1 & 15360) >> 2)	|
           	   ((i1 & 491520) >> 3) );
        
        if (i1 < par4)
        {
            i1 = par4;
        }

        return this.provider.lightBrightnessTable[i1];
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     * 
     * Truncated to only use regular light, not colored
     * 
     * CptSpaceToaster
     */
    public float getLightBrightness(int par1, int par2, int par3)
    {
        return this.provider.lightBrightnessTable[this.getBlockLightValue(par1, par2, par3)&15];
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime()
    {
        return provider.isDaytime();
    }

    /**
     * Performs a raycast against all blocks in the world except liquids.
     */
    public MovingObjectPosition clip(Vec3 par1Vec3, Vec3 par2Vec3)
    {
        return this.rayTraceBlocks_do_do(par1Vec3, par2Vec3, false, false);
    }

    /**
     * Performs a raycast against all blocks in the world, and optionally liquids.
     */
    public MovingObjectPosition clip(Vec3 par1Vec3, Vec3 par2Vec3, boolean par3)
    {
        return this.rayTraceBlocks_do_do(par1Vec3, par2Vec3, par3, false);
    }

    public MovingObjectPosition rayTraceBlocks_do_do(Vec3 par1Vec3, Vec3 par2Vec3, boolean par3, boolean par4)
    {
        if (!Double.isNaN(par1Vec3.xCoord) && !Double.isNaN(par1Vec3.yCoord) && !Double.isNaN(par1Vec3.zCoord))
        {
            if (!Double.isNaN(par2Vec3.xCoord) && !Double.isNaN(par2Vec3.yCoord) && !Double.isNaN(par2Vec3.zCoord))
            {
                int i = MathHelper.floor_double(par2Vec3.xCoord);
                int j = MathHelper.floor_double(par2Vec3.yCoord);
                int k = MathHelper.floor_double(par2Vec3.zCoord);
                int l = MathHelper.floor_double(par1Vec3.xCoord);
                int i1 = MathHelper.floor_double(par1Vec3.yCoord);
                int j1 = MathHelper.floor_double(par1Vec3.zCoord);
                int k1 = this.getBlockId(l, i1, j1);
                int l1 = this.getBlockMetadata(l, i1, j1);
                Block block = Block.blocksList[k1];

                if (block != null && (!par4 || block == null || block.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null) && k1 > 0 && block.canCollideCheck(l1, par3))
                {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(this, l, i1, j1, par1Vec3, par2Vec3);

                    if (movingobjectposition != null)
                    {
                        return movingobjectposition;
                    }
                }

                k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(par1Vec3.xCoord) || Double.isNaN(par1Vec3.yCoord) || Double.isNaN(par1Vec3.zCoord))
                    {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return null;
                    }

                    boolean flag2 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag3 = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = par2Vec3.xCoord - par1Vec3.xCoord;
                    double d7 = par2Vec3.yCoord - par1Vec3.yCoord;
                    double d8 = par2Vec3.zCoord - par1Vec3.zCoord;

                    if (flag2)
                    {
                        d3 = (d0 - par1Vec3.xCoord) / d6;
                    }

                    if (flag3)
                    {
                        d4 = (d1 - par1Vec3.yCoord) / d7;
                    }

                    if (flag4)
                    {
                        d5 = (d2 - par1Vec3.zCoord) / d8;
                    }

                    boolean flag5 = false;
                    byte b0;

                    if (d3 < d4 && d3 < d5)
                    {
                        if (i > l)
                        {
                            b0 = 4;
                        }
                        else
                        {
                            b0 = 5;
                        }

                        par1Vec3.xCoord = d0;
                        par1Vec3.yCoord += d7 * d3;
                        par1Vec3.zCoord += d8 * d3;
                    }
                    else if (d4 < d5)
                    {
                        if (j > i1)
                        {
                            b0 = 0;
                        }
                        else
                        {
                            b0 = 1;
                        }

                        par1Vec3.xCoord += d6 * d4;
                        par1Vec3.yCoord = d1;
                        par1Vec3.zCoord += d8 * d4;
                    }
                    else
                    {
                        if (k > j1)
                        {
                            b0 = 2;
                        }
                        else
                        {
                            b0 = 3;
                        }

                        par1Vec3.xCoord += d6 * d5;
                        par1Vec3.yCoord += d7 * d5;
                        par1Vec3.zCoord = d2;
                    }

                    Vec3 vec32 = this.getWorldVec3Pool().getVecFromPool(par1Vec3.xCoord, par1Vec3.yCoord, par1Vec3.zCoord);
                    l = (int)(vec32.xCoord = (double)MathHelper.floor_double(par1Vec3.xCoord));

                    if (b0 == 5)
                    {
                        --l;
                        ++vec32.xCoord;
                    }

                    i1 = (int)(vec32.yCoord = (double)MathHelper.floor_double(par1Vec3.yCoord));

                    if (b0 == 1)
                    {
                        --i1;
                        ++vec32.yCoord;
                    }

                    j1 = (int)(vec32.zCoord = (double)MathHelper.floor_double(par1Vec3.zCoord));

                    if (b0 == 3)
                    {
                        --j1;
                        ++vec32.zCoord;
                    }

                    int i2 = this.getBlockId(l, i1, j1);
                    int j2 = this.getBlockMetadata(l, i1, j1);
                    Block block1 = Block.blocksList[i2];

                    if ((!par4 || block1 == null || block1.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null) && i2 > 0 && block1.canCollideCheck(j2, par3))
                    {
                        MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, l, i1, j1, par1Vec3, par2Vec3);

                        if (movingobjectposition1 != null)
                        {
                            return movingobjectposition1;
                        }
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(par1Entity, par2Str, par3, par4);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return;
        }
        par2Str = event.name;
        if (par1Entity != null && par2Str != null)
        {
            for (int i = 0; i < this.worldAccesses.size(); ++i)
            {
                ((IWorldAccess)this.worldAccesses.get(i)).playSound(par2Str, par1Entity.posX, par1Entity.posY - (double)par1Entity.yOffset, par1Entity.posZ, par3, par4);
            }
        }
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, float par3, float par4)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(par1EntityPlayer, par2Str, par3, par4);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return;
        }
        par2Str = event.name;
        if (par1EntityPlayer != null && par2Str != null)
        {
            for (int i = 0; i < this.worldAccesses.size(); ++i)
            {
                ((IWorldAccess)this.worldAccesses.get(i)).playSoundToNearExcept(par1EntityPlayer, par2Str, par1EntityPlayer.posX, par1EntityPlayer.posY - (double)par1EntityPlayer.yOffset, par1EntityPlayer.posZ, par3, par4);
            }
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void playSoundEffect(double par1, double par3, double par5, String par7Str, float par8, float par9)
    {
        if (par7Str != null)
        {
            for (int i = 0; i < this.worldAccesses.size(); ++i)
            {
                ((IWorldAccess)this.worldAccesses.get(i)).playSound(par7Str, par1, par3, par5, par8, par9);
            }
        }
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10) {}

    /**
     * Plays a record at the specified coordinates of the specified name. Args: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
        for (int l = 0; l < this.worldAccesses.size(); ++l)
        {
            ((IWorldAccess)this.worldAccesses.get(l)).playRecord(par1Str, par2, par3, par4);
        }
    }

    /**
     * Spawns a particle.  Args particleName, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        for (int i = 0; i < this.worldAccesses.size(); ++i)
        {
            ((IWorldAccess)this.worldAccesses.get(i)).spawnParticle(par1Str, par2, par4, par6, par8, par10, par12);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity par1Entity)
    {
        this.weatherEffects.add(par1Entity);
        return true;
    }

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(Entity par1Entity)
    {
        int i = MathHelper.floor_double(par1Entity.posX / 16.0D);
        int j = MathHelper.floor_double(par1Entity.posZ / 16.0D);
        boolean flag = par1Entity.forceSpawn;

        if (par1Entity instanceof EntityPlayer)
        {
            flag = true;
        }

        if (!flag && !this.chunkExists(i, j))
        {
            return false;
        }
        else
        {
            if (par1Entity instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)par1Entity;
                this.playerEntities.add(entityplayer);
                this.updateAllPlayersSleepingFlag();
            }

            if (MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(par1Entity, this)) && !flag)
            {
                return false;
            }

            this.getChunkFromChunkCoords(i, j).addEntity(par1Entity);
            this.loadedEntityList.add(par1Entity);
            this.onEntityAdded(par1Entity);
            return true;
        }
    }

    protected void onEntityAdded(Entity par1Entity)
    {
        for (int i = 0; i < this.worldAccesses.size(); ++i)
        {
            ((IWorldAccess)this.worldAccesses.get(i)).onEntityCreate(par1Entity);
        }
    }

    public void onEntityRemoved(Entity par1Entity)
    {
        for (int i = 0; i < this.worldAccesses.size(); ++i)
        {
            ((IWorldAccess)this.worldAccesses.get(i)).onEntityDestroy(par1Entity);
        }
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != null)
        {
            par1Entity.riddenByEntity.mountEntity((Entity)null);
        }

        if (par1Entity.ridingEntity != null)
        {
            par1Entity.mountEntity((Entity)null);
        }

        par1Entity.setDead();

        if (par1Entity instanceof EntityPlayer)
        {
            this.playerEntities.remove(par1Entity);
            this.updateAllPlayersSleepingFlag();
        }
    }

    /**
     * Do NOT use this method to remove normal entities- use normal removeEntity
     */
    public void removePlayerEntityDangerously(Entity par1Entity)
    {
        par1Entity.setDead();

        if (par1Entity instanceof EntityPlayer)
        {
            this.playerEntities.remove(par1Entity);
            this.updateAllPlayersSleepingFlag();
        }

        int i = par1Entity.chunkCoordX;
        int j = par1Entity.chunkCoordZ;

        if (par1Entity.addedToChunk && this.chunkExists(i, j))
        {
            this.getChunkFromChunkCoords(i, j).removeEntity(par1Entity);
        }

        this.loadedEntityList.remove(par1Entity);
        this.onEntityRemoved(par1Entity);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addWorldAccess(IWorldAccess par1IWorldAccess)
    {
        this.worldAccesses.add(par1IWorldAccess);
    }

    /**
     * Returns a list of bounding boxes that collide with aabb excluding the passed in entity's collision. Args: entity,
     * aabb
     */
    public List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB)
    {
        this.collidingBoundingBoxes.clear();
        int i = MathHelper.floor_double(par2AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par2AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par2AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (this.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block = Block.blocksList[this.getBlockId(k1, i2, l1)];

                        if (block != null)
                        {
                            block.addCollisionBoxesToList(this, k1, i2, l1, par2AxisAlignedBB, this.collidingBoundingBoxes, par1Entity);
                        }
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).getBoundingBox();

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
            {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }

            axisalignedbb1 = par1Entity.getCollisionBox((Entity)list.get(j2));

            if (axisalignedbb1 != null && axisalignedbb1.intersectsWith(par2AxisAlignedBB))
            {
                this.collidingBoundingBoxes.add(axisalignedbb1);
            }
        }

        return this.collidingBoundingBoxes;
    }

    /**
     * calculates and returns a list of colliding bounding boxes within a given AABB
     */
    public List getCollidingBlockBounds(AxisAlignedBB par1AxisAlignedBB)
    {
        this.collidingBoundingBoxes.clear();
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = i1; l1 < j1; ++l1)
            {
                if (this.blockExists(k1, 64, l1))
                {
                    for (int i2 = k - 1; i2 < l; ++i2)
                    {
                        Block block = Block.blocksList[this.getBlockId(k1, i2, l1)];

                        if (block != null)
                        {
                            block.addCollisionBoxesToList(this, k1, i2, l1, par1AxisAlignedBB, this.collidingBoundingBoxes, (Entity)null);
                        }
                    }
                }
            }
        }

        return this.collidingBoundingBoxes;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float)((double)f2 * (1.0D - (double)(this.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float)((double)f2 * (1.0D - (double)(this.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int)(f2 * 11.0F);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Removes a worldAccess from the worldAccesses object
     */
    public void removeWorldAccess(IWorldAccess par1IWorldAccess)
    {
        this.worldAccesses.remove(par1IWorldAccess);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the sun brightness - checks time of day, rain and thunder
     */
    public float getSunBrightness(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.2F);

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float)((double)f2 * (1.0D - (double)(this.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float)((double)f2 * (1.0D - (double)(this.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        return f2 * 0.8F + 0.2F;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Calculates the color for the skybox
     */
    public Vec3 getSkyColor(Entity par1Entity, float par2)
    {
        return provider.getSkyColor(par1Entity, par2);
    }

    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColorBody(Entity par1Entity, float par2)
    {
        float f1 = this.getCelestialAngle(par2);
        float f2 = MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }
        
        int i = MathHelper.floor_double(par1Entity.posX);
        int j = MathHelper.floor_double(par1Entity.posZ);
        
        int multiplier = ForgeHooksClient.getSkyBlendColour(this, i, j);

        float f4 = (float)(multiplier >> 16 & 255) / 255.0F;
        float f5 = (float)(multiplier >> 8 & 255) / 255.0F;
        float f6 = (float)(multiplier & 255) / 255.0F;
        f4 *= f2;
        f5 *= f2;
        f6 *= f2;
        float f7 = this.getRainStrength(par2);
        float f8;
        float f9;

        if (f7 > 0.0F)
        {
            f8 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.6F;
            f9 = 1.0F - f7 * 0.75F;
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
            f6 = f6 * f9 + f8 * (1.0F - f9);
        }

        f8 = this.getWeightedThunderStrength(par2);

        if (f8 > 0.0F)
        {
            f9 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.2F;
            float f10 = 1.0F - f8 * 0.75F;
            f4 = f4 * f10 + f9 * (1.0F - f10);
            f5 = f5 * f10 + f9 * (1.0F - f10);
            f6 = f6 * f10 + f9 * (1.0F - f10);
        }

        if (this.lastLightningBolt > 0)
        {
            f9 = (float)this.lastLightningBolt - par2;

            if (f9 > 1.0F)
            {
                f9 = 1.0F;
            }

            f9 *= 0.45F;
            f4 = f4 * (1.0F - f9) + 0.8F * f9;
            f5 = f5 * (1.0F - f9) + 0.8F * f9;
            f6 = f6 * (1.0F - f9) + 1.0F * f9;
        }

        return this.getWorldVec3Pool().getVecFromPool((double)f4, (double)f5, (double)f6);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(float par1)
    {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), par1);
    }

    @SideOnly(Side.CLIENT)
    public int getMoonPhase()
    {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor()
    {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    /**
     * Return getCelestialAngle()*2*PI
     */
    public float getCelestialAngleRadians(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        return f1 * (float)Math.PI * 2.0F;
    }

    @SideOnly(Side.CLIENT)
    public Vec3 getCloudColour(float par1)
    {
        return provider.drawClouds(par1);
    }

    @SideOnly(Side.CLIENT)
    public Vec3 drawCloudsBody(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        float f2 = MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        float f3 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float f4 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float f5 = (float)(this.cloudColour & 255L) / 255.0F;
        float f6 = this.getRainStrength(par1);
        float f7;
        float f8;

        if (f6 > 0.0F)
        {
            f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            f8 = 1.0F - f6 * 0.95F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        f3 *= f2 * 0.9F + 0.1F;
        f4 *= f2 * 0.9F + 0.1F;
        f5 *= f2 * 0.85F + 0.15F;
        f7 = this.getWeightedThunderStrength(par1);

        if (f7 > 0.0F)
        {
            f8 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f9 = 1.0F - f7 * 0.95F;
            f3 = f3 * f9 + f8 * (1.0F - f9);
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
        }

        return this.getWorldVec3Pool().getVecFromPool((double)f3, (double)f4, (double)f5);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns vector(ish) with R/G/B for fog
     */
    public Vec3 getFogColor(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        return this.provider.getFogColor(f1, par1);
    }

    /**
     * Gets the height to which rain/snow will fall. Calculates it if not already stored.
     */
    public int getPrecipitationHeight(int par1, int par2)
    {
        return this.getChunkFromBlockCoords(par1, par2).getPrecipitationHeight(par1 & 15, par2 & 15);
    }

    /**
     * Finds the highest block on the x, z coordinate that is solid and returns its y coord. Args x, z
     */
    public int getTopSolidOrLiquidBlock(int par1, int par2)
    {
        Chunk chunk = this.getChunkFromBlockCoords(par1, par2);
        int x = par1;
        int z = par2;
        int k = chunk.getTopFilledSegment() + 15;
        par1 &= 15;

        for (par2 &= 15; k > 0; --k)
        {
            int l = chunk.getBlockID(par1, k, par2);

            if (l != 0 && Block.blocksList[l].blockMaterial.blocksMovement() && Block.blocksList[l].blockMaterial != Material.leaves && !Block.blocksList[l].isBlockFoliage(this, x, k, z))
            {
                return k + 1;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * How bright are stars in the sky
     */
    public float getStarBrightness(float par1)
    {
        return provider.getStarBrightness(par1);
    }

    @SideOnly(Side.CLIENT)
    public float getStarBrightnessBody(float par1)
    {
        float f1 = this.getCelestialAngle(par1);
        float f2 = 1.0F - (MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.25F);

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        return f2 * f2 * 0.5F;
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    public void scheduleBlockUpdate(int par1, int par2, int par3, int par4, int par5) {}

    public void scheduleBlockUpdateWithPriority(int par1, int par2, int par3, int par4, int par5, int par6) {}

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void scheduleBlockUpdateFromLoad(int par1, int par2, int par3, int par4, int par5, int par6) {}

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities()
    {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");
        int i;
        Entity entity;
        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        for (i = 0; i < this.weatherEffects.size(); ++i)
        {
            entity = (Entity)this.weatherEffects.get(i);

            try
            {
                ++entity.ticksExisted;
                entity.onUpdate();
            }
            catch (Throwable throwable)
            {
                crashreport = CrashReport.makeCrashReport(throwable, "Ticking entity");
                crashreportcategory = crashreport.makeCategory("Entity being ticked");

                if (entity == null)
                {
                    crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                }
                else
                {
                    entity.addEntityCrashInfo(crashreportcategory);
                }

                if (ForgeDummyContainer.removeErroringEntities)
                {
                    FMLLog.severe(crashreport.getCompleteReport());
                    removeEntity(entity);
                }
                else
                {
                    throw new ReportedException(crashreport);
                }
            }

            if (entity.isDead)
            {
                this.weatherEffects.remove(i--);
            }
        }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int j;
        int k;

        for (i = 0; i < this.unloadedEntityList.size(); ++i)
        {
            entity = (Entity)this.unloadedEntityList.get(i);
            j = entity.chunkCoordX;
            k = entity.chunkCoordZ;

            if (entity.addedToChunk && this.chunkExists(j, k))
            {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity);
            }
        }

        for (i = 0; i < this.unloadedEntityList.size(); ++i)
        {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(i));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (i = 0; i < this.loadedEntityList.size(); ++i)
        {
            entity = (Entity)this.loadedEntityList.get(i);

            if (entity.ridingEntity != null)
            {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity)
                {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");

            if (!entity.isDead)
            {
                try
                {
                    this.updateEntity(entity);
                }
                catch (Throwable throwable1)
                {
                    crashreport = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                    crashreportcategory = crashreport.makeCategory("Entity being ticked");
                    entity.addEntityCrashInfo(crashreportcategory);

                    if (ForgeDummyContainer.removeErroringEntities)
                    {
                        FMLLog.severe(crashreport.getCompleteReport());
                        removeEntity(entity);
                    }
                    else
                    {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");

            if (entity.isDead)
            {
                j = entity.chunkCoordX;
                k = entity.chunkCoordZ;

                if (entity.addedToChunk && this.chunkExists(j, k))
                {
                    this.getChunkFromChunkCoords(j, k).removeEntity(entity);
                }

                this.loadedEntityList.remove(i--);
                this.onEntityRemoved(entity);
            }

            this.theProfiler.endSection();
        }

        this.theProfiler.endStartSection("tileEntities");
        this.scanningTileEntities = true;
        Iterator iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext())
        {
            TileEntity tileentity = (TileEntity)iterator.next();

            if (!tileentity.isInvalid() && tileentity.hasWorldObj() && this.blockExists(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord))
            {
                try
                {
                    tileentity.updateEntity();
                }
                catch (Throwable throwable2)
                {
                    crashreport = CrashReport.makeCrashReport(throwable2, "Ticking tile entity");
                    crashreportcategory = crashreport.makeCategory("Tile entity being ticked");
                    tileentity.func_85027_a(crashreportcategory);
                    if (ForgeDummyContainer.removeErroringTileEntities)
                    {
                        FMLLog.severe(crashreport.getCompleteReport());
                        tileentity.invalidate();
                        setBlockToAir(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                    }
                    else
                    {
                        throw new ReportedException(crashreport);
                    }
                }
            }

            if (tileentity.isInvalid())
            {
                iterator.remove();

                if (this.chunkExists(tileentity.xCoord >> 4, tileentity.zCoord >> 4))
                {
                    Chunk chunk = this.getChunkFromChunkCoords(tileentity.xCoord >> 4, tileentity.zCoord >> 4);

                    if (chunk != null)
                    {
                        chunk.cleanChunkBlockTileEntity(tileentity.xCoord & 15, tileentity.yCoord, tileentity.zCoord & 15);
                    }
                }
            }
        }


        if (!this.entityRemoval.isEmpty())
        {
            for (Object tile : entityRemoval)
            {
               ((TileEntity)tile).onChunkUnload();
            }
            this.loadedTileEntityList.removeAll(this.entityRemoval);
            this.entityRemoval.clear();
        }

        this.scanningTileEntities = false;

        this.theProfiler.endStartSection("pendingTileEntities");

        if (!this.addedTileEntityList.isEmpty())
        {
            for (int l = 0; l < this.addedTileEntityList.size(); ++l)
            {
                TileEntity tileentity1 = (TileEntity)this.addedTileEntityList.get(l);

                if (!tileentity1.isInvalid())
                {
                    if (!this.loadedTileEntityList.contains(tileentity1))
                    {
                        this.loadedTileEntityList.add(tileentity1);
                    }
                }
                else
                {
                    if (this.chunkExists(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4))
                    {
                        Chunk chunk1 = this.getChunkFromChunkCoords(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4);

                        if (chunk1 != null)
                        {
                            chunk1.cleanChunkBlockTileEntity(tileentity1.xCoord & 15, tileentity1.yCoord, tileentity1.zCoord & 15);
                        }
                    }
                }
            }

            this.addedTileEntityList.clear();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public void addTileEntity(Collection par1Collection)
    {
        List dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList;
        for(Object entity : par1Collection)
        {
            if(((TileEntity)entity).canUpdate())
            {
                dest.add(entity);
            }
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    public void updateEntity(Entity par1Entity)
    {
        this.updateEntityWithOptionalForce(par1Entity, true);
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2)
    {
        int i = MathHelper.floor_double(par1Entity.posX);
        int j = MathHelper.floor_double(par1Entity.posZ);

        boolean isForced = getPersistentChunks().containsKey(new ChunkCoordIntPair(i >> 4, j >> 4));
        byte b0 = isForced ? (byte)0 : 32;
        boolean canUpdate = !par2 || this.checkChunksExist(i - b0, 0, j - b0, i + b0, 0, j + b0);
        if (!canUpdate)
        {
            EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(par1Entity);
            MinecraftForge.EVENT_BUS.post(event);
            canUpdate = event.canUpdate;
        }
        if (canUpdate)
        {
            par1Entity.lastTickPosX = par1Entity.posX;
            par1Entity.lastTickPosY = par1Entity.posY;
            par1Entity.lastTickPosZ = par1Entity.posZ;
            par1Entity.prevRotationYaw = par1Entity.rotationYaw;
            par1Entity.prevRotationPitch = par1Entity.rotationPitch;

            if (par2 && par1Entity.addedToChunk)
            {
                ++par1Entity.ticksExisted;

                if (par1Entity.ridingEntity != null)
                {
                    par1Entity.updateRidden();
                }
                else
                {
                    par1Entity.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");

            if (Double.isNaN(par1Entity.posX) || Double.isInfinite(par1Entity.posX))
            {
                par1Entity.posX = par1Entity.lastTickPosX;
            }

            if (Double.isNaN(par1Entity.posY) || Double.isInfinite(par1Entity.posY))
            {
                par1Entity.posY = par1Entity.lastTickPosY;
            }

            if (Double.isNaN(par1Entity.posZ) || Double.isInfinite(par1Entity.posZ))
            {
                par1Entity.posZ = par1Entity.lastTickPosZ;
            }

            if (Double.isNaN((double)par1Entity.rotationPitch) || Double.isInfinite((double)par1Entity.rotationPitch))
            {
                par1Entity.rotationPitch = par1Entity.prevRotationPitch;
            }

            if (Double.isNaN((double)par1Entity.rotationYaw) || Double.isInfinite((double)par1Entity.rotationYaw))
            {
                par1Entity.rotationYaw = par1Entity.prevRotationYaw;
            }

            int k = MathHelper.floor_double(par1Entity.posX / 16.0D);
            int l = MathHelper.floor_double(par1Entity.posY / 16.0D);
            int i1 = MathHelper.floor_double(par1Entity.posZ / 16.0D);

            if (!par1Entity.addedToChunk || par1Entity.chunkCoordX != k || par1Entity.chunkCoordY != l || par1Entity.chunkCoordZ != i1)
            {
                if (par1Entity.addedToChunk && this.chunkExists(par1Entity.chunkCoordX, par1Entity.chunkCoordZ))
                {
                    this.getChunkFromChunkCoords(par1Entity.chunkCoordX, par1Entity.chunkCoordZ).removeEntityAtIndex(par1Entity, par1Entity.chunkCoordY);
                }

                if (this.chunkExists(k, i1))
                {
                    par1Entity.addedToChunk = true;
                    this.getChunkFromChunkCoords(k, i1).addEntity(par1Entity);
                }
                else
                {
                    par1Entity.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();

            if (par2 && par1Entity.addedToChunk && par1Entity.riddenByEntity != null)
            {
                if (!par1Entity.riddenByEntity.isDead && par1Entity.riddenByEntity.ridingEntity == par1Entity)
                {
                    this.updateEntity(par1Entity.riddenByEntity);
                }
                else
                {
                    par1Entity.riddenByEntity.ridingEntity = null;
                    par1Entity.riddenByEntity = null;
                }
            }
        }
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB
     */
    public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB)
    {
        return this.checkNoEntityCollision(par1AxisAlignedBB, (Entity)null);
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    public boolean checkNoEntityCollision(AxisAlignedBB par1AxisAlignedBB, Entity par2Entity)
    {
        List list = this.getEntitiesWithinAABBExcludingEntity((Entity)null, par1AxisAlignedBB);

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = (Entity)list.get(i);

            if (!entity1.isDead && entity1.preventEntitySpawning && entity1 != par2Entity)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean checkBlockCollision(AxisAlignedBB par1AxisAlignedBB)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D)
        {
            --i;
        }

        if (par1AxisAlignedBB.minY < 0.0D)
        {
            --k;
        }

        if (par1AxisAlignedBB.minZ < 0.0D)
        {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = k; l1 < l; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean isAnyLiquid(AxisAlignedBB par1AxisAlignedBB)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (par1AxisAlignedBB.minX < 0.0D)
        {
            --i;
        }

        if (par1AxisAlignedBB.minY < 0.0D)
        {
            --k;
        }

        if (par1AxisAlignedBB.minZ < 0.0D)
        {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = k; l1 < l; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial.isLiquid())
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns whether or not the given bounding box is on fire or not
     */
    public boolean isBoundingBoxBurning(AxisAlignedBB par1AxisAlignedBB)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (this.checkChunksExist(i, k, i1, j, l, j1))
        {
            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        int j2 = this.getBlockId(k1, l1, i2);

                        if (j2 == Block.fire.blockID || j2 == Block.lavaMoving.blockID || j2 == Block.lavaStill.blockID)
                        {
                            return true;
                        }
                        else
                        {
                            Block block = Block.blocksList[j2];
                            if (block != null && block.isBlockBurning(this, k1, l1, i2))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean handleMaterialAcceleration(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        if (!this.checkChunksExist(i, k, i1, j, l, j1))
        {
            return false;
        }
        else
        {
            boolean flag = false;
            Vec3 vec3 = this.getWorldVec3Pool().getVecFromPool(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                        if (block != null && block.blockMaterial == par2Material)
                        {
                            double d0 = (double)((float)(l1 + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(k1, l1, i2)));

                            if ((double)l >= d0)
                            {
                                flag = true;
                                block.velocityToAddToEntity(this, k1, l1, i2, par3Entity, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D && par3Entity.isPushedByWater())
            {
                vec3 = vec3.normalize();
                double d1 = 0.014D;
                par3Entity.motionX += vec3.xCoord * d1;
                par3Entity.motionY += vec3.yCoord * d1;
                par3Entity.motionZ += vec3.zCoord * d1;
            }

            return flag;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(AxisAlignedBB par1AxisAlignedBB, Material par2Material)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = k; l1 < l; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial == par2Material)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean isAABBInMaterial(AxisAlignedBB par1AxisAlignedBB, Material par2Material)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = k; l1 < l; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockMaterial == par2Material)
                    {
                        int j2 = this.getBlockMetadata(k1, l1, i2);
                        double d0 = (double)(l1 + 1);

                        if (j2 < 8)
                        {
                            d0 = (double)(l1 + 1) - (double)j2 / 8.0D;
                        }

                        if (d0 >= par1AxisAlignedBB.minY)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates an explosion. Args: entity, x, y, z, strength
     */
    public Explosion createExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9)
    {
        return this.newExplosion(par1Entity, par2, par4, par6, par8, false, par9);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10)
    {
        Explosion explosion = new Explosion(this, par1Entity, par2, par4, par6, par8);
        explosion.isFlaming = par9;
        explosion.isSmoking = par10;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(Vec3 par1Vec3, AxisAlignedBB par2AxisAlignedBB)
    {
        double d0 = 1.0D / ((par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * 2.0D + 1.0D);
        int i = 0;
        int j = 0;

        for (float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0))
        {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1))
            {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2))
                {
                    double d3 = par2AxisAlignedBB.minX + (par2AxisAlignedBB.maxX - par2AxisAlignedBB.minX) * (double)f;
                    double d4 = par2AxisAlignedBB.minY + (par2AxisAlignedBB.maxY - par2AxisAlignedBB.minY) * (double)f1;
                    double d5 = par2AxisAlignedBB.minZ + (par2AxisAlignedBB.maxZ - par2AxisAlignedBB.minZ) * (double)f2;

                    if (this.clip(this.getWorldVec3Pool().getVecFromPool(d3, d4, d5), par1Vec3) == null)
                    {
                        ++i;
                    }

                    ++j;
                }
            }
        }

        return (float)i / (float)j;
    }

    /**
     * If the block in the given direction of the given coordinate is fire, extinguish it. Args: Player, X,Y,Z,
     * blockDirection
     */
    public boolean extinguishFire(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5)
    {
        if (par5 == 0)
        {
            --par3;
        }

        if (par5 == 1)
        {
            ++par3;
        }

        if (par5 == 2)
        {
            --par4;
        }

        if (par5 == 3)
        {
            ++par4;
        }

        if (par5 == 4)
        {
            --par2;
        }

        if (par5 == 5)
        {
            ++par2;
        }

        if (this.getBlockId(par2, par3, par4) == Block.fire.blockID)
        {
            this.playAuxSFXAtEntity(par1EntityPlayer, 1004, par2, par3, par4, 0);
            this.setBlockToAir(par2, par3, par4);
            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * This string is 'All: (number of loaded entities)' Viewable by press ing F3
     */
    public String getDebugLoadedEntities()
    {
        return "All: " + this.loadedEntityList.size();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
     */
    public String getProviderName()
    {
        return this.chunkProvider.makeString();
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(int par1, int par2, int par3)
    {
        if (par2 >= 0 && par2 < 256)
        {
            TileEntity tileentity = null;
            int l;
            TileEntity tileentity1;

            if (this.scanningTileEntities)
            {
                for (l = 0; l < this.addedTileEntityList.size(); ++l)
                {
                    tileentity1 = (TileEntity)this.addedTileEntityList.get(l);

                    if (!tileentity1.isInvalid() && tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3)
                    {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null)
            {
                Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);

                if (chunk != null)
                {
                    tileentity = chunk.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
                }
            }

            if (tileentity == null)
            {
                for (l = 0; l < this.addedTileEntityList.size(); ++l)
                {
                    tileentity1 = (TileEntity)this.addedTileEntityList.get(l);

                    if (!tileentity1.isInvalid() && tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3)
                    {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            return tileentity;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the TileEntity for a given block in X, Y, Z coordinates
     */
    public void setBlockTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        if (par4TileEntity == null || par4TileEntity.isInvalid())
        {
            return;
        }

        if (par4TileEntity.canUpdate())
        {
            if (scanningTileEntities)
            {
                Iterator iterator = addedTileEntityList.iterator();
                while (iterator.hasNext())
                {
                    TileEntity tileentity1 = (TileEntity)iterator.next();

                    if (tileentity1.xCoord == par1 && tileentity1.yCoord == par2 && tileentity1.zCoord == par3)
                    {
                        tileentity1.invalidate();
                        iterator.remove();
                    }
                }
                addedTileEntityList.add(par4TileEntity);
            }
            else
            {
                loadedTileEntityList.add(par4TileEntity);
            }
        }

        Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        if (chunk != null)
        {
            chunk.setChunkBlockTileEntity(par1 & 15, par2, par3 & 15, par4TileEntity);
        }
        //notify tile changes
        func_96440_m(par1, par2, par3, 0);
    }

    /**
     * Removes the TileEntity for a given block in X,Y,Z coordinates
     */
    public void removeBlockTileEntity(int par1, int par2, int par3)
    {
        Chunk chunk = getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        if (chunk != null)
        {
            chunk.removeChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
        }
        //notify tile changes
        func_96440_m(par1, par2, par3, 0);
    }

    /**
     * adds tile entity to despawn list (renamed from markEntityForDespawn)
     */
    public void markTileEntityForDespawn(TileEntity par1TileEntity)
    {
        this.entityRemoval.add(par1TileEntity);
    }

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean isBlockOpaqueCube(int par1, int par2, int par3)
    {
        Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return block == null ? false : block.isOpaqueCube();
    }

    /**
     * Indicate if a material is a normal solid opaque cube.
     */
    public boolean isBlockNormalCube(int par1, int par2, int par3)
    {
        Block block = Block.blocksList[getBlockId(par1, par2, par3)];
        return block != null && block.isBlockNormalCube(this, par1, par2, par3);
    }

    public boolean isBlockFullCube(int par1, int par2, int par3)
    {
        int l = this.getBlockId(par1, par2, par3);

        if (l != 0 && Block.blocksList[l] != null)
        {
            AxisAlignedBB axisalignedbb = Block.blocksList[l].getCollisionBoundingBoxFromPool(this, par1, par2, par3);
            return axisalignedbb != null && axisalignedbb.getAverageEdgeLength() >= 1.0D;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    public boolean doesBlockHaveSolidTopSurface(int par1, int par2, int par3)
    {
        return isBlockSolidOnSide(par1, par2, par3, ForgeDirection.UP);
    }

    /**
     * Performs check to see if the block is a normal, solid block, or if the metadata of the block indicates that its
     * facing puts its solid side upwards. (inverted stairs, for example)
     */
    @Deprecated //DO NOT USE THIS!!! USE doesBlockHaveSolidTopSurface
    public boolean isBlockTopFacingSurfaceSolid(Block par1Block, int par2)
    {
        // -.-  Mojang PLEASE make this location sensitive, you have no reason not to.
        return par1Block == null ? false : (par1Block.blockMaterial.isOpaque() && par1Block.renderAsNormalBlock() ? true : (par1Block instanceof BlockStairs ? (par2 & 4) == 4 : (par1Block instanceof BlockHalfSlab ? (par2 & 8) == 8 : (par1Block instanceof BlockHopper ? true : (par1Block instanceof BlockSnow ? (par2 & 7) == 7 : false)))));
    }

    /**
     * Checks if the block is a solid, normal cube. If the chunk does not exist, or is not loaded, it returns the
     * boolean parameter.
     */
    public boolean isBlockNormalCubeDefault(int par1, int par2, int par3, boolean par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            Chunk chunk = this.chunkProvider.provideChunk(par1 >> 4, par3 >> 4);

            if (chunk != null && !chunk.isEmpty())
            {
                Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
                return block == null ? false : isBlockNormalCube(par1, par2, par3);
            }
            else
            {
                return par4;
            }
        }
        else
        {
            return par4;
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight()
    {
        int i = this.calculateSkylightSubtracted(1.0F);

        if (i != this.skylightSubtracted)
        {
            this.skylightSubtracted = i;
        }
    }

    /**
     * Set which types of mobs are allowed to spawn (peaceful vs hostile).
     */
    public void setAllowedSpawnTypes(boolean par1, boolean par2)
    {
        provider.setAllowedSpawnTypes(par1, par2);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        this.updateWeather();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    private void calculateInitialWeather()
    {
        provider.calculateInitialWeather();
    }

    public void calculateInitialWeatherBody()
    {
        if (this.worldInfo.isRaining())
        {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        provider.updateWeather();
    }

    public void updateWeatherBody()
    {
        if (!this.provider.hasNoSky)
        {
            int i = this.worldInfo.getThunderTime();

            if (i <= 0)
            {
                if (this.worldInfo.isThundering())
                {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                }
                else
                {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            }
            else
            {
                --i;
                this.worldInfo.setThunderTime(i);

                if (i <= 0)
                {
                    this.worldInfo.setThundering(!this.worldInfo.isThundering());
                }
            }

            int j = this.worldInfo.getRainTime();

            if (j <= 0)
            {
                if (this.worldInfo.isRaining())
                {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                }
                else
                {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            }
            else
            {
                --j;
                this.worldInfo.setRainTime(j);

                if (j <= 0)
                {
                    this.worldInfo.setRaining(!this.worldInfo.isRaining());
                }
            }

            this.prevRainingStrength = this.rainingStrength;

            if (this.worldInfo.isRaining())
            {
                this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            }
            else
            {
                this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            if (this.rainingStrength < 0.0F)
            {
                this.rainingStrength = 0.0F;
            }

            if (this.rainingStrength > 1.0F)
            {
                this.rainingStrength = 1.0F;
            }

            this.prevThunderingStrength = this.thunderingStrength;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            }
            else
            {
                this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            if (this.thunderingStrength < 0.0F)
            {
                this.thunderingStrength = 0.0F;
            }

            if (this.thunderingStrength > 1.0F)
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    public void toggleRain()
    {
        provider.toggleRain();
    }

    protected void setActivePlayerChunksAndCheckLight()
    {
        this.activeChunkSet.clear();
        this.activeChunkSet.addAll(getPersistentChunks().keySet());

        this.theProfiler.startSection("buildList");
        int i;
        EntityPlayer entityplayer;
        int j;
        int k;

        for (i = 0; i < this.playerEntities.size(); ++i)
        {
            entityplayer = (EntityPlayer)this.playerEntities.get(i);
            j = MathHelper.floor_double(entityplayer.posX / 16.0D);
            k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
            byte b0 = 7;

            for (int l = -b0; l <= b0; ++l)
            {
                for (int i1 = -b0; i1 <= b0; ++i1)
                {
                    this.activeChunkSet.add(new ChunkCoordIntPair(l + j, i1 + k));
                }
            }
        }

        this.theProfiler.endSection();

        if (this.ambientTickCountdown > 0)
        {
            --this.ambientTickCountdown;
        }

        this.theProfiler.startSection("playerCheckLight");

        if (!this.playerEntities.isEmpty())
        {
            i = this.rand.nextInt(this.playerEntities.size());
            entityplayer = (EntityPlayer)this.playerEntities.get(i);
            j = MathHelper.floor_double(entityplayer.posX) + this.rand.nextInt(11) - 5;
            k = MathHelper.floor_double(entityplayer.posY) + this.rand.nextInt(11) - 5;
            int j1 = MathHelper.floor_double(entityplayer.posZ) + this.rand.nextInt(11) - 5;
            this.updateAllLightTypes(j, k, j1);
        }

        this.theProfiler.endSection();
    }

    protected void moodSoundAndLightCheck(int par1, int par2, Chunk par3Chunk)
    {
        this.theProfiler.endStartSection("moodSound");

        if (this.ambientTickCountdown == 0 && !this.isRemote)
        {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int k = this.updateLCG >> 2;
            int l = k & 15;
            int i1 = k >> 8 & 15;
            int j1 = k >> 16 & 127;
            int k1 = par3Chunk.getBlockID(l, j1, i1);
            l += par1;
            i1 += par2;

            if (k1 == 0 && this.getFullBlockLightValue(l, j1, i1) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, l, j1, i1) <= 0)
            {
                EntityPlayer entityplayer = this.getClosestPlayer((double)l + 0.5D, (double)j1 + 0.5D, (double)i1 + 0.5D, 8.0D);

                if (entityplayer != null && entityplayer.getDistanceSq((double)l + 0.5D, (double)j1 + 0.5D, (double)i1 + 0.5D) > 4.0D)
                {
                    this.playSoundEffect((double)l + 0.5D, (double)j1 + 0.5D, (double)i1 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        par3Chunk.enqueueRelightChecks();
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void tickBlocksAndAmbiance()
    {
        this.setActivePlayerChunksAndCheckLight();
    }

    /**
     * checks to see if a given block is both water and is cold enough to freeze
     */
    public boolean isBlockFreezable(int par1, int par2, int par3)
    {
        return this.canBlockFreeze(par1, par2, par3, false);
    }

    /**
     * checks to see if a given block is both water and has at least one immediately adjacent non-water block
     */
    public boolean isBlockFreezableNaturally(int par1, int par2, int par3)
    {
        return this.canBlockFreeze(par1, par2, par3, true);
    }

    /**
     * checks to see if a given block is both water, and cold enough to freeze - if the par4 boolean is set, this will
     * only return true if there is a non-water block immediately adjacent to the specified block
     */
    public boolean canBlockFreeze(int par1, int par2, int par3, boolean par4)
    {
        return provider.canBlockFreeze(par1, par2, par3, par4);
    }

    public boolean canBlockFreezeBody(int par1, int par2, int par3, boolean par4)
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
        float f = biomegenbase.getFloatTemperature();

        if (f > 0.15F)
        {
            return false;
        }
        else
        {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10)
            {
                int l = this.getBlockId(par1, par2, par3);

                if ((l == Block.waterStill.blockID || l == Block.waterMoving.blockID) && this.getBlockMetadata(par1, par2, par3) == 0)
                {
                    if (!par4)
                    {
                        return true;
                    }

                    boolean flag1 = true;

                    if (flag1 && this.getBlockMaterial(par1 - 1, par2, par3) != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1 + 1, par2, par3) != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1, par2, par3 - 1) != Material.water)
                    {
                        flag1 = false;
                    }

                    if (flag1 && this.getBlockMaterial(par1, par2, par3 + 1) != Material.water)
                    {
                        flag1 = false;
                    }

                    if (!flag1)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Tests whether or not snow can be placed at a given location
     */
    public boolean canSnowAt(int par1, int par2, int par3)
    {
        return provider.canSnowAt(par1, par2, par3);
    }

    public boolean canSnowAtBody(int par1, int par2, int par3)
    {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
        float f = biomegenbase.getFloatTemperature();

        if (f > 0.15F)
        {
            return false;
        }
        else
        {
            if (par2 >= 0 && par2 < 256 && this.getSavedLightValue(EnumSkyBlock.Block, par1, par2, par3) < 10)
            {
                int l = this.getBlockId(par1, par2 - 1, par3);
                int i1 = this.getBlockId(par1, par2, par3);

                if (i1 == 0 && Block.snow.canPlaceBlockAt(this, par1, par2, par3) && l != 0 && l != Block.ice.blockID && Block.blocksList[l].blockMaterial.blocksMovement())
                {
                    return true;
                }
            }

            return false;
        }
    }

    public void updateAllLightTypes(int par1, int par2, int par3)
    {
        if (!this.provider.hasNoSky)
        {
            this.updateLightByType(EnumSkyBlock.Sky, par1, par2, par3);
        }

        this.updateLightByType(EnumSkyBlock.Block, par1, par2, par3);
    }

    /**
     * Modded to work with colored light
     * 
     * CptSpaceToaster
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z coordinate
     * @param par4EnumSkyBlock Whether or not the block is skyblock, or simple a regular block
     * @return
     */
    private int computeLightValue(int x, int y, int z, EnumSkyBlock par4EnumSkyBlock)
    {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky && this.canBlockSeeTheSky(x, y, z))
        {
            return 15;
        }
        else
        {
            int l = this.getBlockId(x, y, z);
            Block block = Block.blocksList[l];
            int blockLight = (block == null ? 0 : block.getLightValue(this, x, y, z));
            int currentLight = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
            int opacity = (block == null ? 0 : block.getLightOpacity(this, x, y, z));

            if (opacity >= 15 && blockLight > 0)
            {
                opacity = 1;
            }

            if (opacity < 1)
            {
                opacity = 1;
            }

            if (opacity >= 15)
            {
                return 0;
            }
//            else if ((currentLight&15) >= 14) {
//            	return currentLight;
//            }
            else
            {
                for (int k1 = 0; k1 < 6; ++k1)
                {
                    int l1 = x + Facing.offsetsXForSide[k1];
                    int i2 = y + Facing.offsetsYForSide[k1];
                    int j2 = z + Facing.offsetsZForSide[k1];
                    
                    int neighboorLight = this.getSavedLightValue(par4EnumSkyBlock, l1, i2, j2);
                    int ll = neighboorLight&15;
                    int rl = neighboorLight&480;
                    int gl = neighboorLight&15360;
                    int bl = neighboorLight&491520;
                    
                    if((neighboorLight&507375) >= 0) {
                    	ll-=opacity;
                    	
                    	if (ll < 0)
                        	ll = 0;
                    	
                    	rl-=32*opacity;
                        if (rl < 0)
                        	rl = 0;
                        
                        gl-=1024*opacity;
                        if (gl < 0)
                        	gl = 0;
                        
                        bl-=32768*opacity;
                        if (bl < 0)
                        	bl = 0;
                    }
                    
                    if (ll > (currentLight&15)) {
                    	currentLight = (currentLight&507360) | ll;
                    } 
                    if (rl > (currentLight&480)) {
                        currentLight = (currentLight&506895) | rl;
                    }
                    if (gl > (currentLight&15360)) {
                    	currentLight = (currentLight&492015) | gl;
	                }
                    if (bl > (currentLight&491520)) {
                        currentLight = (currentLight&15855) | bl;
                    }
                }

                return currentLight;
            }
        }
    }

    /**
     * This was gutted and refitted to handle blocks with colors
     * 
     * CptSpaceToaster
     * @param par1Enu
     * @param x
     * @param y
     * @param z
     */
    public void updateLightByType(EnumSkyBlock par1Enu, int x, int y, int z)
    {
//    	// EnumSkyBlock is whether is a skyblock or normal block
//        if (this.doChunksNearChunkExist(x, y, z, 17))
//        {
//            int l = 0; // intermediate variables
//            int i1 = 0; // intermediate variables
//            this.theProfiler.startSection("getBrightness");
//            int initSavedLight = this.getSavedLightValue(par1Enu, x, y, z);  
//            int initCompLight = this.computeLightValue(x, y, z, par1Enu); 	
//            long l1; // Blocklist 1
//            int i2; // X2
//            int j2; // Y2
//            int k2; // Z2
//            int l2; // blocklist 2
//            int i3; // X3
//            int j3; // Y3
//            int k3; // Z3
//            long l3; // blocklist 3
//
//            // Checks to see if the Computed Light is larger than the Saved Light 
//            if ((((1048576|initSavedLight)-initCompLight)&541200) > 0)
//            {
//                this.lightUpdateBlockList[i1++] = 133152;
//            }
//            // Checks to see if the Saved Light is larger than the Computed Light 
//            else if ((((1048576|initCompLight)-initSavedLight)&541200) > 0)
//            {
//                this.lightUpdateBlockList[i1++] = 133152 | initSavedLight << 18; // Takes saved light and throws into left
//                System.out.println("Loop 1: il = " + i1 + " ,l = " + l);
//                while (l < i1)
//                {
//                	
//                    l1 = this.lightUpdateBlockList[l++];
//                    i2 = ((int)(l1 & 63) - 32 + x); 								// X2 Location
//                    j2 = ((int)(l1 >> 6 & 63) - 32 + y); 							// Y2 Location
//                    k2 = ((int)(l1 >> 12 & 63) - 32 + z); 							// Z2 location
//                    l2 = (int)(l1 >>> 18)&507375; 									// Initially saved light popped back
//                    i3 = this.getSavedLightValue(par1Enu, i2, j2, k2);		//Second Saved Light
//
//                    // l2 and i3 are both light values
//                    if ((i3&15) == (l2&15) 		 ||
//                    	(i3&480) == (l2&480) 	 ||
//                    	(i3&15360) == (l2&15360) ||
//                    	(i3&491520) == (l2&491520) )
//                    {
//                    	
//                    	// ~ bit flip operator (NOT)
//                    	// & AND operator
//                    	// l2&~15 = turning off bits 1234 and 
//                    	// TODO: FIX THIS
//                    	
//                    	
//                    	int m1 = l2;
//                    	
//                    	System.out.println("m1 Before: " + Integer.toBinaryString(m1));
//                    	
//                    	if ((i3&15) == (l2&15))
//                    		m1 &= 507360;
//                    	if ((i3&480) == (l2&480))
//                    		m1 &= 506895;
//                    	if ((i3&15360) == (l2&15360))
//                    		m1 &= 492015;
//                    	if ((i3&491520) == (l2&491520))
//                    		m1 &= 15855;
//                    	
//                    	System.out.println("m1 After: " + Integer.toBinaryString(m1));
//                    	
//                    	
//                    	this.setLightValue(par1Enu, i2, j2, k2, m1);
//                    	
//                    	/*
//                        if (((i3&15) == (l2&15)		||
//                        	(i3&480) == (l2&480)	||
//                        	(i3&15360) == (l2&15360)||
//                        	(i3&491520) == (l2&491520) )&&
//                        	(l2&507375) > 0) // Checks to see if there is any light FORCES NUMBER TO BE POSITIVE
//                        {
//                        	
//                        	j3 = MathHelper.abs_int(i2 - x);
//                            l3 = MathHelper.abs_int(j2 - y);
//                            k3 = MathHelper.abs_int(k2 - z);
//                        	
//                        	//System.out.println("l2: " + Integer.toBinaryString(l2));
//                            //Computes light again
//                            if (j3 + l3 + k3 < 17) //Manhatten distance
//                            {
//                                for (int i4 = 0; i4 < 6; ++i4)
//                                {
//                                    int j4 = i2 + Facing.offsetsXForSide[i4];
//                                    int k4 = j2 + Facing.offsetsYForSide[i4];
//                                    int l4 = k2 + Facing.offsetsZForSide[i4];
//                                    
//                                    Block block = Block.blocksList[getBlockId(j4, k4, l4)];
//                                    int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, j4, k4, l4));
//                                    int opacity = Math.max(1, blockOpacity);
//                                    
//                                    int neighboorLight = this.getSavedLightValue(par1Enu, j4, k4, l4);
//                                    int ll = neighboorLight&15;
//                                    int rl = neighboorLight&480;
//                                    int gl = neighboorLight&15360;
//                                    int bl = neighboorLight&491520;
//                                    
//                                    if((neighboorLight&507375) >= 0) {
//            	                    	ll-=opacity;
//            	                    	
//            	                    	if (ll < 0)
//                                        	ll = 0;
//            	                    	
//            	                    	rl-=32*opacity;
//                                        if (rl < 0)
//                                        	rl = 0;
//                                        
//                                        gl-=1024*opacity;
//                                        if (gl < 0)
//                                        	gl = 0;
//                                        
//                                        bl-=32768*opacity;
//                                        if (bl < 0)
//                                        	bl = 0;
//                                    }
//                                    
//                                    
//                                    
//                                    i3 = this.getSavedLightValue(par1Enu, i2, j2, k2);
//                                    //i3 = this.getSavedLightValue(par1Enu, j4, k4, l4);
//                                    //int tryThis = this.computeLightValue(j4, k4, l4, par1Enu); 
//                                    
//                                    if ( ( (i3&15) == ll		||
//	                                	   (i3&480) == rl		||
//	                                	   (i3&15360) == gl		||
//	                                	   (i3&491520) == bl )	&&
//	                                	   i1 < this.lightUpdateBlockList.length - 6)//-6 again?  There are six faces on a cube...
//                                    {
//                                        this.lightUpdateBlockList[i1++] = j4 - x + 32 | (k4 - y + 32 << 6) | (l4 - z + 32 << 12) | ((bl<<15) | (gl<<10) | (rl<<5) | ll) << 18;
//                                    }
//                                }
//                            }
//                        }
//                        
//                        */
//                    }
//                }
//
//                l = 0;
//            }
//
//            this.theProfiler.endSection();
//            this.theProfiler.startSection("checkedPosition < toCheckCount");
//            //System.out.println("Loop 2: il = " + i1 + " ,l = " + l);
//            while (l < i1) // Lights greater than saved lights
//            {
//            	
//                l1 = this.lightUpdateBlockList[l++];
//                i2 = ((int)(l1 & 63) - 32 + x);
//                j2 = ((int)(l1 >> 6 & 63) - 32 + y);
//                k2 = ((int)(l1 >> 12 & 63) - 32 + z);
//                l2 = (this.getSavedLightValue(par1Enu, i2, j2, k2));
//                i3 = (this.computeLightValue(i2, j2, k2, par1Enu));
//
//                if (i3 != l2)
//                {
//                	if((i3&15) > (l2&15))
//                		this.setLightValue(par1Enu, i2, j2, k2, l2&507360 | i3&15);
//                	if((i3&480) > (l2&480))
//                		this.setLightValue(par1Enu, i2, j2, k2, l2&506895 | i3&480);
//                	if((i3&15360) > (l2&15360))
//                		this.setLightValue(par1Enu, i2, j2, k2, l2&492015 | i3&15360);
//                	if((i3&491520) > (l2&491520))
//                		this.setLightValue(par1Enu, i2, j2, k2, l2&15855 | i3&491520);
//                	
//                	// Checks to see if i3 is larger than the l2
//                    if ((((1048576|l2)-i3)&541200) > 0)
//                    {
//                        j3 = Math.abs(i2 - x);
//                        l3 = Math.abs(j2 - y);
//                        k3 = Math.abs(k2 - z);
//                        boolean flag = i1 < this.lightUpdateBlockList.length - 6; //what's with the minus 6?
//
//                        if (j3 + l3 + k3 < 17 && flag) // Manhatan distance
//                        {
//                        	// WHERE LAG HAPPENS WHEN PLACED
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, i2 - 1, j2, k2))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 - 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
//                            }
//
//                            if ((((1048576|this.getSavedLightValue(par1Enu, i2 + 1, j2, k2))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 + 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
//                            }
//
//                            if ((((1048576|this.getSavedLightValue(par1Enu, i2, j2 - 1, k2))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 - x + 32 + (j2 - 1 - y + 32 << 6) + (k2 - z + 32 << 12);
//                            }
//
//                            if ((((1048576|this.getSavedLightValue(par1Enu, i2, j2 + 1, k2))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 - x + 32 + (j2 + 1 - y + 32 << 6) + (k2 - z + 32 << 12);
//                            }
//
//                            if ((((1048576|this.getSavedLightValue(par1Enu, i2, j2, k2 - 1))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 - 1 - z + 32 << 12);
//                            }
//
//                            if ((((1048576|this.getSavedLightValue(par1Enu, i2, j2, k2 + 1))-i3)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 + 1 - z + 32 << 12);
//                            }
//                        }
//                    }
//                }
//            }
//
//            this.theProfiler.endSection();
//        }

//*************************************************************************************************************************************    	
//*************************************************************************************************************************************
//*************************************************************************************************************************************

//    	if (this.doChunksNearChunkExist(x, y, z, 17))
//        {
//            int l = 0;
//            int i1 = 0;
//            this.theProfiler.startSection("getBrightness");
//            int savedLightValue = this.getSavedLightValue(par1Enu, x, y, z);
//            int compLightValue = this.computeLightValue(x, y, z, par1Enu);
//            long l1;
//            int x1;
//            int y1;
//            int z1;
//            int lightEntry;
//            int expectedEntryLight;
//            int x2;
//            int z2;
//            int y2;
//
//            if (compLightValue > savedLightValue)
//            {
//                this.lightUpdateBlockList[i1++] = 133152;		//Save Entry at pos 0 (move i1)
//            }
//            else if (compLightValue < savedLightValue)
//            {
//                this.lightUpdateBlockList[i1++] = 133152 | savedLightValue << 18;	//Save Entry at pos 0 with its Light Value (move i1)	
//
//                while (l < i1)
//                {	  
//                    l1 = this.lightUpdateBlockList[l++];		//Get Entry at l, which starts at 0
//                    x1 = ((int)(l1 & 63) - 32 + x);				//Get Entry X coord
//                    y1 = ((int)(l1 >> 6 & 63) - 32 + y);		//Get Entry Y coord
//                    z1 = ((int)(l1 >> 12 & 63) - 32 + z);		//Get Entry Z coord
//                    lightEntry = (int)(l1 >>> 18)&507375;		//Get Entry's saved Light
//                    expectedEntryLight = this.getSavedLightValue(par1Enu, x1, y1, z1);	//Get the saved Light Level at the entry's location
//
//                    if (expectedEntryLight == lightEntry)	//The entered light, and the saved light equal
//                    										//Not sure why this condition is super important...
//                    {
//                        this.setLightValue(par1Enu, x1, y1, z1, 0);
//
//                        if (lightEntry > 0)
//                        {
//                            x2 = MathHelper.abs_int(x1 - x);
//                            y2 = MathHelper.abs_int(y1 - y);
//                            z2 = MathHelper.abs_int(z1 - z);
//
//                            if (x2 + y2 + z2 < 17)
//                            {
//                            	//For Each time savedLightLevelFromEntry == lightEntry, we'll check all 6 faces around the block, and we'll
//                            	//and if a face == lightEntry, then we'll mark it for additional updates
//                                for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
//                                {
//                                    int xFace = x1 + Facing.offsetsXForSide[faceIndex];
//                                    int yFace = y1 + Facing.offsetsYForSide[faceIndex];
//                                    int zFace = z1 + Facing.offsetsZForSide[faceIndex];
//                                    Block block = Block.blocksList[getBlockId(xFace, yFace, zFace)];
//                                    int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, xFace, yFace, zFace));
//                                    int i5 = Math.max(1, blockOpacity);
//                                    //Get Saved light value from face
//                                    expectedEntryLight = this.getSavedLightValue(par1Enu, xFace, yFace, zFace);
//
//                                    if (expectedEntryLight == lightEntry - i5 && i1 < this.lightUpdateBlockList.length)
//                                    {
//                                    	//Create entry at the face's location, with the diminished lightEntry
//                                        this.lightUpdateBlockList[i1++] = xFace - x + 32 | yFace - y + 32 << 6 | zFace - z + 32 << 12 | lightEntry - i5 << 18;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                //reset l, so we can loop through all of the updates again!
//                l = 0;
//            }
//
//            this.theProfiler.endSection();
//            this.theProfiler.startSection("checkedPosition < toCheckCount");
//
//            while (l < i1)
//            {
//                l1 = this.lightUpdateBlockList[l++];		//Get Entry and it's light value (if there is one)
//                x1 = ((int)(l1 & 63) - 32 + x);				//Get Entry X coord
//                y1 = ((int)(l1 >> 6 & 63) - 32 + y);		//Get Entry Y coord
//                z1 = ((int)(l1 >> 12 & 63) - 32 + z);		//Get Entry Z coord
//                
//                //Get the Saved Light at the Entry's Position
//                lightEntry = this.getSavedLightValue(par1Enu, x1, y1, z1);	
//                
//                
//                //Compute the light level at the entry's location.  If the light's have been set to zero before this occurs,
//                //then the computation will change dynamically
//                expectedEntryLight = this.computeLightValue(x1, y1, z1, par1Enu);
//                
//
//                if (expectedEntryLight != lightEntry)
//                {
//                    //this.setLightValue(par1Enu, x1, y1, z1, expectedEntryLight);
//                    if((expectedEntryLight&15) > (lightEntry&15))
//                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&507360 | expectedEntryLight&15);
//                	if((expectedEntryLight&480) > (lightEntry&480))
//                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&506895 | expectedEntryLight&480);
//                	if((expectedEntryLight&15360) > (lightEntry&15360))
//                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&492015 | expectedEntryLight&15360);
//                	if((expectedEntryLight&491520) > (lightEntry&491520))
//                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&15855 | expectedEntryLight&491520);
//                	
//                	
//                    if (expectedEntryLight > lightEntry)
//                    {
//                        x2 = Math.abs(x1 - x);
//                        y2 = Math.abs(y1 - y);
//                        z2 = Math.abs(z1 - z);
//                        boolean flag = i1 < this.lightUpdateBlockList.length - 6;
//
//                        if (x2 + y2 + z2 < 17 && flag)
//                        {
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1 - 1, y2, z2))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
//                            }
//
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1 + 1, y1, z1))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
//                            }
//
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2 - 1, z2))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
//                            }
//
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2 + 1, z2))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
//                            }
//
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2, z2 - 1))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
//                            }
//
//                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2, z2 + 1))-expectedEntryLight)&541200) > 0)
//                            {
//                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
//                            }
//                        }
//                    }
//                }
//            }
//
//            this.theProfiler.endSection();
    	
//*************************************************************************************************************************************    	
//*************************************************************************************************************************************
//*************************************************************************************************************************************
    	
    	if (this.doChunksNearChunkExist(x, y, z, 17))
        {
            int l = 0;
            int i1 = 0;
            this.theProfiler.startSection("getBrightness");
            int savedLightValue = this.getSavedLightValue(par1Enu, x, y, z);
            int compLightValue = this.computeLightValue(x, y, z, par1Enu);
            long l1;
            int x1;
            int y1;
            int z1;
            int lightEntry;
            int expectedEntryLight;
            int x2;
            int z2;
            int y2;

            if (compLightValue > savedLightValue)
            {
                this.lightUpdateBlockList[i1++] = 133152;		//Save Entry at pos 0 (move i1)
            }
            else if (compLightValue < savedLightValue)
            {
                this.lightUpdateBlockList[i1++] = 133152 | savedLightValue << 18;	//Save Entry at pos 0 with its Light Value (move i1)	

                while (l < i1)
                {	  
                    l1 = this.lightUpdateBlockList[l++];		//Get Entry at l, which starts at 0
                    x1 = ((int)(l1 & 63) - 32 + x);				//Get Entry X coord
                    y1 = ((int)(l1 >> 6 & 63) - 32 + y);		//Get Entry Y coord
                    z1 = ((int)(l1 >> 12 & 63) - 32 + z);		//Get Entry Z coord
                    lightEntry = (int)(l1 >>> 18)&507375;		//Get Entry's saved Light
                    expectedEntryLight = this.getSavedLightValue(par1Enu, x1, y1, z1);	//Get the saved Light Level at the entry's location

                    if (expectedEntryLight == lightEntry)	//The entered light, and the saved light equal
                    										//Not sure why this condition is super important...
                    {
                        this.setLightValue(par1Enu, x1, y1, z1, 0);

                        if (lightEntry > 0)
                        {
                            x2 = MathHelper.abs_int(x1 - x);
                            y2 = MathHelper.abs_int(y1 - y);
                            z2 = MathHelper.abs_int(z1 - z);

                            if (x2 + y2 + z2 < 17)
                            {
                            	//For Each time savedLightLevelFromEntry == lightEntry, we'll check all 6 faces around the block, and we'll
                            	//and if a face == lightEntry, then we'll mark it for additional updates
//                                for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
//                                {
//                                    int xFace = x1 + Facing.offsetsXForSide[faceIndex];
//                                    int yFace = y1 + Facing.offsetsYForSide[faceIndex];
//                                    int zFace = z1 + Facing.offsetsZForSide[faceIndex];
//                                    Block block = Block.blocksList[getBlockId(xFace, yFace, zFace)];
//                                    int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, xFace, yFace, zFace));
//                                    int opacity = Math.max(1, blockOpacity);
//                                    //Get Saved light value from face
//                                    expectedEntryLight = this.getSavedLightValue(par1Enu, xFace, yFace, zFace);
//                                    if (expectedEntryLight == lightEntry - opacity && i1 < this.lightUpdateBlockList.length)
//                                    {
//                                    	//Create entry at the face's location, with the diminished lightEntry
//                                        this.lightUpdateBlockList[i1++] = xFace - x + 32 | yFace - y + 32 << 6 | zFace - z + 32 << 12 | lightEntry - i5 << 18;
//                                    }
//                                }
                            	
                            	for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
                            	{
                            		int xFace = x1 + Facing.offsetsXForSide[faceIndex];
                            		int yFace = y1 + Facing.offsetsYForSide[faceIndex];
                            		int zFace = z1 + Facing.offsetsZForSide[faceIndex];

                            		Block block = Block.blocksList[getBlockId(xFace, yFace, zFace)];
                            		int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, xFace, yFace, zFace));
                            		int opacity = Math.max(1, blockOpacity);
                            		//Get Saved light value from face
                            		expectedEntryLight = this.getSavedLightValue(par1Enu, xFace, yFace, zFace);
                            		
                            		int neighboorLight = lightEntry;
                            		int ll = neighboorLight&15;
                            		int rl = neighboorLight&480;
                            		int gl = neighboorLight&15360;
                            		int bl = neighboorLight&491520;

                            		if((neighboorLight&507375) >= 0) {
                            			ll-=opacity;
                            			if (ll < 0)
                            				ll = 0;

                            			rl-=32*opacity;
                            			if (rl < 0)
                            				rl = 0;

                            			gl-=1024*opacity;
                            			if (gl < 0)
                            				gl = 0;

                            			bl-=32768*opacity;
                            			if (bl < 0)
                            				bl = 0;
                            		}

                            		
                            		//i3 = this.getSavedLightValue(par1Enu, j4, k4, l4);
                            		//int tryThis = this.computeLightValue(j4, k4, l4, par1Enu); 

                            		if ( ((expectedEntryLight&15) == ll		||
                            			  (expectedEntryLight&480) == rl	||
                            			  (expectedEntryLight&15360) == gl	||
                            			  (expectedEntryLight&491520) == bl )	&&
                            			  i1 < this.lightUpdateBlockList.length )		//Why did I have a -6 here?
                            		{
                            			this.lightUpdateBlockList[i1++] = xFace - x + 32 | (yFace - y + 32 << 6) | (zFace - z + 32 << 12) | ((bl<<15) | (gl<<10) | (rl<<5) | ll) << 18;
                            		}
                            	}
                            }
                        }
                    }
                }
                //reset l, so we can loop through all of the updates again!
                l = 0;
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("checkedPosition < toCheckCount");

            while (l < i1)
            {
                l1 = this.lightUpdateBlockList[l++];		//Get Entry and it's light value (if there is one)
                x1 = ((int)(l1 & 63) - 32 + x);				//Get Entry X coord
                y1 = ((int)(l1 >> 6 & 63) - 32 + y);		//Get Entry Y coord
                z1 = ((int)(l1 >> 12 & 63) - 32 + z);		//Get Entry Z coord
                
                //Get the Saved Light at the Entry's Position
                lightEntry = this.getSavedLightValue(par1Enu, x1, y1, z1);	
                
                
                //Compute the light level at the entry's location.  If the light's have been set to zero before this occurs,
                //then the computation will change dynamically
                expectedEntryLight = this.computeLightValue(x1, y1, z1, par1Enu);
                

                if (expectedEntryLight != lightEntry)
                {
                    //this.setLightValue(par1Enu, x1, y1, z1, expectedEntryLight);
                    if((expectedEntryLight&15) > (lightEntry&15))
                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&507360 | expectedEntryLight&15);
                	if((expectedEntryLight&480) > (lightEntry&480))
                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&506895 | expectedEntryLight&480);
                	if((expectedEntryLight&15360) > (lightEntry&15360))
                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&492015 | expectedEntryLight&15360);
                	if((expectedEntryLight&491520) > (lightEntry&491520))
                		this.setLightValue(par1Enu, x1, y1, z1, lightEntry&15855 | expectedEntryLight&491520);
                	
                	
                    if (expectedEntryLight > lightEntry)
                    {
                        x2 = Math.abs(x1 - x);
                        y2 = Math.abs(y1 - y);
                        z2 = Math.abs(z1 - z);
                        boolean flag = i1 < this.lightUpdateBlockList.length - 6;

                        if (x2 + y2 + z2 < 17 && flag)
                        {
                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1 - 1, y2, z2))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1 + 1, y1, z1))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2 - 1, z2))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2 + 1, z2))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2, z2 - 1))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
                            }

                        	if ((((1048576|this.getSavedLightValue(par1Enu, x1, y2, z2 + 1))-expectedEntryLight)&541200) > 0)
                            {
                                this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean par1)
    {
        return false;
    }

    public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2)
    {
        return null;
    }

    /**
     * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
     */
    public List getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB)
    {
        return this.getEntitiesWithinAABBExcludingEntity(par1Entity, par2AxisAlignedBB, (IEntitySelector)null);
    }

    public List getEntitiesWithinAABBExcludingEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3IEntitySelector)
    {
        ArrayList arraylist = new ArrayList();
        int i = MathHelper.floor_double((par2AxisAlignedBB.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((par2AxisAlignedBB.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int k = MathHelper.floor_double((par2AxisAlignedBB.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int l = MathHelper.floor_double((par2AxisAlignedBB.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1)
        {
            for (int j1 = k; j1 <= l; ++j1)
            {
                if (this.chunkExists(i1, j1))
                {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(par1Entity, par2AxisAlignedBB, arraylist, par3IEntitySelector);
                }
            }
        }

        return arraylist;
    }

    /**
     * Returns all entities of the specified class type which intersect with the AABB. Args: entityClass, aabb
     */
    public List getEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB)
    {
        return this.selectEntitiesWithinAABB(par1Class, par2AxisAlignedBB, (IEntitySelector)null);
    }

    public List selectEntitiesWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, IEntitySelector par3IEntitySelector)
    {
        int i = MathHelper.floor_double((par2AxisAlignedBB.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((par2AxisAlignedBB.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int k = MathHelper.floor_double((par2AxisAlignedBB.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int l = MathHelper.floor_double((par2AxisAlignedBB.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
        ArrayList arraylist = new ArrayList();

        for (int i1 = i; i1 <= j; ++i1)
        {
            for (int j1 = k; j1 <= l; ++j1)
            {
                if (this.chunkExists(i1, j1))
                {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(par1Class, par2AxisAlignedBB, arraylist, par3IEntitySelector);
                }
            }
        }

        return arraylist;
    }

    public Entity findNearestEntityWithinAABB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, Entity par3Entity)
    {
        List list = this.getEntitiesWithinAABB(par1Class, par2AxisAlignedBB);
        Entity entity1 = null;
        double d0 = Double.MAX_VALUE;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity2 = (Entity)list.get(i);

            if (entity2 != par3Entity)
            {
                double d1 = par3Entity.getDistanceSqToEntity(entity2);

                if (d1 <= d0)
                {
                    entity1 = entity2;
                    d0 = d1;
                }
            }
        }

        return entity1;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public abstract Entity getEntityByID(int i);

    @SideOnly(Side.CLIENT)

    /**
     * Accessor for world Loaded Entity List
     */
    public List getLoadedEntityList()
    {
        return this.loadedEntityList;
    }

    /**
     * Args: X, Y, Z, tile entity Marks the chunk the tile entity is in as modified. This is essential as chunks that
     * are not marked as modified may be rolled back when exiting the game.
     */
    public void markTileEntityChunkModified(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        if (this.blockExists(par1, par2, par3))
        {
            this.getChunkFromBlockCoords(par1, par3).setChunkModified();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int countEntities(Class par1Class)
    {
        int i = 0;

        for (int j = 0; j < this.loadedEntityList.size(); ++j)
        {
            Entity entity = (Entity)this.loadedEntityList.get(j);

            if ((!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) && par1Class.isAssignableFrom(entity.getClass()))
            {
                ++i;
            }
        }

        return i;
    }

    /**
     * adds entities to the loaded entities list, and loads thier skins.
     */
    public void addLoadedEntities(List par1List)
    {
        for (int i = 0; i < par1List.size(); ++i)
        {
            Entity entity = (Entity)par1List.get(i);
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, this)))
            {
                loadedEntityList.add(entity);
                this.onEntityAdded(entity);
            }
        }
    }

    /**
     * Adds a list of entities to be unloaded on the next pass of World.updateEntities()
     */
    public void unloadEntities(List par1List)
    {
        this.unloadedEntityList.addAll(par1List);
    }

    /**
     * Returns true if the given Entity can be placed on the given side of the given block position.
     */
    public boolean canPlaceEntityOnSide(int par1, int par2, int par3, int par4, boolean par5, int par6, Entity par7Entity, ItemStack par8ItemStack)
    {
        int j1 = this.getBlockId(par2, par3, par4);
        Block block = Block.blocksList[j1];
        Block block1 = Block.blocksList[par1];
        AxisAlignedBB axisalignedbb = block1.getCollisionBoundingBoxFromPool(this, par2, par3, par4);

        if (par5)
        {
            axisalignedbb = null;
        }

        if (axisalignedbb != null && !this.checkNoEntityCollision(axisalignedbb, par7Entity))
        {
            return false;
        }
        else
        {
            if (block != null && (block == Block.waterMoving || block == Block.waterStill || block == Block.lavaMoving || block == Block.lavaStill || block == Block.fire || block.blockMaterial.isReplaceable()))
            {
                block = null;
            }

            if (block != null && block.isBlockReplaceable(this, par2, par3, par4))
            {
                block = null;
            }

            return block != null && block.blockMaterial == Material.circuits && block1 == Block.anvil ? true : par1 > 0 && block == null && block1.canPlaceBlockOnSide(this, par2, par3, par4, par6, par8ItemStack);
        }
    }

    public PathEntity getPathEntityToEntity(Entity par1Entity, Entity par2Entity, float par3, boolean par4, boolean par5, boolean par6, boolean par7)
    {
        this.theProfiler.startSection("pathfind");
        int i = MathHelper.floor_double(par1Entity.posX);
        int j = MathHelper.floor_double(par1Entity.posY + 1.0D);
        int k = MathHelper.floor_double(par1Entity.posZ);
        int l = (int)(par3 + 16.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2, 0);
        PathEntity pathentity = (new PathFinder(chunkcache, par4, par5, par6, par7)).createEntityPathTo(par1Entity, par2Entity, par3);
        this.theProfiler.endSection();
        return pathentity;
    }

    public PathEntity getEntityPathToXYZ(Entity par1Entity, int par2, int par3, int par4, float par5, boolean par6, boolean par7, boolean par8, boolean par9)
    {
        this.theProfiler.startSection("pathfind");
        int l = MathHelper.floor_double(par1Entity.posX);
        int i1 = MathHelper.floor_double(par1Entity.posY);
        int j1 = MathHelper.floor_double(par1Entity.posZ);
        int k1 = (int)(par5 + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3, 0);
        PathEntity pathentity = (new PathFinder(chunkcache, par6, par7, par8, par9)).createEntityPathTo(par1Entity, par2, par3, par4, par5);
        this.theProfiler.endSection();
        return pathentity;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getBlockId(par1, par2, par3);
        return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingStrongPower(this, par1, par2, par3, par4);
    }

    /**
     * Returns the highest redstone signal strength powering the given block. Args: X, Y, Z.
     */
    public int getBlockPowerInput(int par1, int par2, int par3)
    {
        byte b0 = 0;
        int l = Math.max(b0, this.isBlockProvidingPowerTo(par1, par2 - 1, par3, 0));

        if (l >= 15)
        {
            return l;
        }
        else
        {
            l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2 + 1, par3, 1));

            if (l >= 15)
            {
                return l;
            }
            else
            {
                l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2, par3 - 1, 2));

                if (l >= 15)
                {
                    return l;
                }
                else
                {
                    l = Math.max(l, this.isBlockProvidingPowerTo(par1, par2, par3 + 1, 3));

                    if (l >= 15)
                    {
                        return l;
                    }
                    else
                    {
                        l = Math.max(l, this.isBlockProvidingPowerTo(par1 - 1, par2, par3, 4));

                        if (l >= 15)
                        {
                            return l;
                        }
                        else
                        {
                            l = Math.max(l, this.isBlockProvidingPowerTo(par1 + 1, par2, par3, 5));
                            return l >= 15 ? l : l;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the indirect signal strength being outputted by the given block in the *opposite* of the given direction.
     * Args: X, Y, Z, direction
     */
    public boolean getIndirectPowerOutput(int par1, int par2, int par3, int par4)
    {
        return this.getIndirectPowerLevelTo(par1, par2, par3, par4) > 0;
    }

    /**
     * Gets the power level from a certain block face.  Args: x, y, z, direction
     */
    public int getIndirectPowerLevelTo(int par1, int par2, int par3, int par4)
    {
        if (this.isBlockNormalCube(par1, par2, par3))
        {
            return this.getBlockPowerInput(par1, par2, par3);
        }
        else
        {
            int i1 = this.getBlockId(par1, par2, par3);
            return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingWeakPower(this, par1, par2, par3, par4);
        }
    }

    /**
     * Used to see if one of the blocks next to you or your block is getting power from a neighboring block. Used by
     * items like TNT or Doors so they don't have redstone going straight into them.  Args: x, y, z
     */
    public boolean isBlockIndirectlyGettingPowered(int par1, int par2, int par3)
    {
        return this.getIndirectPowerLevelTo(par1, par2 - 1, par3, 0) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2 + 1, par3, 1) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 - 1, 2) > 0 ? true : (this.getIndirectPowerLevelTo(par1, par2, par3 + 1, 3) > 0 ? true : (this.getIndirectPowerLevelTo(par1 - 1, par2, par3, 4) > 0 ? true : this.getIndirectPowerLevelTo(par1 + 1, par2, par3, 5) > 0))));
    }

    public int getStrongestIndirectPower(int par1, int par2, int par3)
    {
        int l = 0;

        for (int i1 = 0; i1 < 6; ++i1)
        {
            int j1 = this.getIndirectPowerLevelTo(par1 + Facing.offsetsXForSide[i1], par2 + Facing.offsetsYForSide[i1], par3 + Facing.offsetsZForSide[i1], i1);

            if (j1 >= 15)
            {
                return 15;
            }

            if (j1 > l)
            {
                l = j1;
            }
        }

        return l;
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityPlayer getClosestPlayerToEntity(Entity par1Entity, double par2)
    {
        return this.getClosestPlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7)
    {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            EntityPlayer entityplayer1 = (EntityPlayer)this.playerEntities.get(i);
            double d5 = entityplayer1.getDistanceSq(par1, par3, par5);

            if ((par7 < 0.0D || d5 < par7 * par7) && (d4 == -1.0D || d5 < d4))
            {
                d4 = d5;
                entityplayer = entityplayer1;
            }
        }

        return entityplayer;
    }

    /**
     * Returns the closest vulnerable player to this entity within the given radius, or null if none is found
     */
    public EntityPlayer getClosestVulnerablePlayerToEntity(Entity par1Entity, double par2)
    {
        return this.getClosestVulnerablePlayer(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par2);
    }

    /**
     * Returns the closest vulnerable player within the given radius, or null if none is found.
     */
    public EntityPlayer getClosestVulnerablePlayer(double par1, double par3, double par5, double par7)
    {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            EntityPlayer entityplayer1 = (EntityPlayer)this.playerEntities.get(i);

            if (!entityplayer1.capabilities.disableDamage && entityplayer1.isEntityAlive())
            {
                double d5 = entityplayer1.getDistanceSq(par1, par3, par5);
                double d6 = par7;

                if (entityplayer1.isSneaking())
                {
                    d6 = par7 * 0.800000011920929D;
                }

                if (entityplayer1.isInvisible())
                {
                    float f = entityplayer1.getArmorVisibility();

                    if (f < 0.1F)
                    {
                        f = 0.1F;
                    }

                    d6 *= (double)(0.7F * f);
                }

                if ((par7 < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4))
                {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityPlayer getPlayerEntityByName(String par1Str)
    {
        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            if (par1Str.equals(((EntityPlayer)this.playerEntities.get(i)).getCommandSenderName()))
            {
                return (EntityPlayer)this.playerEntities.get(i);
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket() {}

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock() throws MinecraftException
    {
        this.saveHandler.checkSessionLock();
    }

    @SideOnly(Side.CLIENT)
    public void func_82738_a(long par1)
    {
        this.worldInfo.incrementTotalWorldTime(par1);
    }

    /**
     * Retrieve the world seed from level.dat
     */
    public long getSeed()
    {
        return provider.getSeed();
    }

    public long getTotalWorldTime()
    {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime()
    {
        return provider.getWorldTime();
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long par1)
    {
        provider.setWorldTime(par1);
    }

    /**
     * Returns the coordinates of the spawn point
     */
    public ChunkCoordinates getSpawnPoint()
    {
        return provider.getSpawnPoint();
    }

    @SideOnly(Side.CLIENT)
    public void setSpawnLocation(int par1, int par2, int par3)
    {
        provider.setSpawnPoint(par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * spwans an entity and loads surrounding chunks
     */
    public void joinEntityInSurroundings(Entity par1Entity)
    {
        int i = MathHelper.floor_double(par1Entity.posX / 16.0D);
        int j = MathHelper.floor_double(par1Entity.posZ / 16.0D);
        byte b0 = 2;

        for (int k = i - b0; k <= i + b0; ++k)
        {
            for (int l = j - b0; l <= j + b0; ++l)
            {
                this.getChunkFromChunkCoords(k, l);
            }
        }

        if (!this.loadedEntityList.contains(par1Entity))
        {
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(par1Entity, this)))
            {
                loadedEntityList.add(par1Entity);
            }
        }
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        return provider.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    public boolean canMineBlockBody(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity par1Entity, byte par2) {}

    /**
     * gets the IChunkProvider this world uses.
     */
    public IChunkProvider getChunkProvider()
    {
        return this.chunkProvider;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void addBlockEvent(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        if (par4 > 0)
        {
            Block.blocksList[par4].onBlockEventReceived(this, par1, par2, par3, par5, par6);
        }
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler()
    {
        return this.saveHandler;
    }

    /**
     * Gets the World's WorldInfo instance
     */
    public WorldInfo getWorldInfo()
    {
        return this.worldInfo;
    }

    /**
     * Gets the GameRules instance.
     */
    public GameRules getGameRules()
    {
        return this.worldInfo.getGameRulesInstance();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag() {}

    public float getWeightedThunderStrength(float par1)
    {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * par1) * this.getRainStrength(par1);
    }

    /**
     * Not sure about this actually. Reverting this one myself.
     */
    public float getRainStrength(float par1)
    {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * par1;
    }

    @SideOnly(Side.CLIENT)
    public void setRainStrength(float par1)
    {
        this.prevRainingStrength = par1;
        this.rainingStrength = par1;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering()
    {
        return (double)this.getWeightedThunderStrength(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining()
    {
        return (double)this.getRainStrength(1.0F) > 0.2D;
    }

    public boolean canLightningStrikeAt(int par1, int par2, int par3)
    {
        if (!this.isRaining())
        {
            return false;
        }
        else if (!this.canBlockSeeTheSky(par1, par2, par3))
        {
            return false;
        }
        else if (this.getPrecipitationHeight(par1, par3) > par2)
        {
            return false;
        }
        else
        {
            BiomeGenBase biomegenbase = this.getBiomeGenForCoords(par1, par3);
            return biomegenbase.getEnableSnow() ? false : biomegenbase.canSpawnLightningBolt();
        }
    }

    /**
     * Checks to see if the biome rainfall values for a given x,y,z coordinate set are extremely high
     */
    public boolean isBlockHighHumidity(int par1, int par2, int par3)
    {
        return provider.isBlockHighHumidity(par1, par2, par3);
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void setItemData(String par1Str, WorldSavedData par2WorldSavedData)
    {
        this.mapStorage.setData(par1Str, par2WorldSavedData);
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk using the MapStorage, instantiating
     * the given Class, or returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadItemData(Class par1Class, String par2Str)
    {
        return this.mapStorage.loadData(par1Class, par2Str);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int getUniqueDataId(String par1Str)
    {
        return this.mapStorage.getUniqueDataId(par1Str);
    }

    public void func_82739_e(int par1, int par2, int par3, int par4, int par5)
    {
        for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1)
        {
            ((IWorldAccess)this.worldAccesses.get(j1)).broadcastSound(par1, par2, par3, par4, par5);
        }
    }

    /**
     * See description for playAuxSFX.
     */
    public void playAuxSFX(int par1, int par2, int par3, int par4, int par5)
    {
        this.playAuxSFXAtEntity((EntityPlayer)null, par1, par2, par3, par4, par5);
    }

    /**
     * See description for playAuxSFX.
     */
    public void playAuxSFXAtEntity(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        try
        {
            for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1)
            {
                ((IWorldAccess)this.worldAccesses.get(j1)).playAuxSFX(par1EntityPlayer, par2, par3, par4, par5, par6);
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getLocationInfo(par3, par4, par5));
            crashreportcategory.addCrashSection("Event source", par1EntityPlayer);
            crashreportcategory.addCrashSection("Event type", Integer.valueOf(par2));
            crashreportcategory.addCrashSection("Event data", Integer.valueOf(par6));
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Returns current world height.
     */
    public int getHeight()
    {
        return provider.getHeight();
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight()
    {
        return provider.getActualHeight();
    }

    public IUpdatePlayerListBox getMinecartSoundUpdater(EntityMinecart par1EntityMinecart)
    {
        return null;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random setRandomSeed(int par1, int par2, int par3)
    {
        long l = (long)par1 * 341873128712L + (long)par2 * 132897987541L + this.getWorldInfo().getSeed() + (long)par3;
        this.rand.setSeed(l);
        return this.rand;
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    public ChunkPosition findClosestStructure(String par1Str, int par2, int par3, int par4)
    {
        return this.getChunkProvider().findClosestStructure(this, par1Str, par2, par3, par4);
    }

    @SideOnly(Side.CLIENT)

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns horizon height for use in rendering the sky.
     */
    public double getHorizon()
    {
        return provider.getHorizon();
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport par1CrashReport)
    {
        CrashReportCategory crashreportcategory = par1CrashReport.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory.addCrashSectionCallable("All players", new CallableLvl2(this));
        crashreportcategory.addCrashSectionCallable("Chunk stats", new CallableLvl3(this));

        try
        {
            this.worldInfo.addToCrashReport(crashreportcategory);
        }
        catch (Throwable throwable)
        {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }

        return crashreportcategory;
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockInWorldPartially(int par1, int par2, int par3, int par4, int par5)
    {
        for (int j1 = 0; j1 < this.worldAccesses.size(); ++j1)
        {
            IWorldAccess iworldaccess = (IWorldAccess)this.worldAccesses.get(j1);
            iworldaccess.destroyBlockPartially(par1, par2, par3, par4, par5);
        }
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3Pool getWorldVec3Pool()
    {
        return this.vecPool;
    }

    /**
     * returns a calendar object containing the current date
     */
    public Calendar getCurrentDate()
    {
        if ((this.getTotalWorldTime() & 600L) == 0L)
        {
            this.theCalendar.setTimeInMillis(MinecraftServer.getSystemTimeMillis());
        }

        return this.theCalendar;
    }

    @SideOnly(Side.CLIENT)
    public void func_92088_a(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13NBTTagCompound) {}

    public Scoreboard getScoreboard()
    {
        return this.worldScoreboard;
    }

    public void func_96440_m(int par1, int par2, int par3, int par4)
    {
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            int j1 = par1 + dir.offsetX;
            int y = par2 + dir.offsetY;
            int k1 = par3 + dir.offsetZ;
            int l1 = getBlockId(j1, y, k1);
            Block block = Block.blocksList[l1];

            if(block != null)
            {
                block.onNeighborTileChange(this, j1, y, k1, par1, par2, par3);

                if(Block.isNormalCube(l1))
                {
                    j1 += dir.offsetX;
                    y += dir.offsetY;
                    k1 += dir.offsetZ;
                    l1 = getBlockId(j1, y, k1);
                    block = Block.blocksList[l1];
                    if(block != null && block.weakTileChanges())
                    {
                        block.onNeighborTileChange(this, j1, y, k1, par1, par2, par3);
                    }
                }
            }
        }
    }

    public ILogAgent getWorldLogAgent()
    {
        return this.worldLogAgent;
    }

    /**
     * returns a float value that can be used to determine how likely something is to go awry in the area. It increases
     * based on how long the player is within the vicinity, the lunar phase, and game difficulty. The value can be up to
     * 1.5 on the highest difficulty, 1.0 otherwise.
     */
    public float getLocationTensionFactor(double par1, double par3, double par5)
    {
        return this.getTensionFactorForBlock(MathHelper.floor_double(par1), MathHelper.floor_double(par3), MathHelper.floor_double(par5));
    }

    /**
     * returns a float value that can be used to determine how likely something is to go awry in the area. It increases
     * based on how long the player is within the vicinity, the lunar phase, and game difficulty. The value can be up to
     * 1.5 on the highest difficulty, 1.0 otherwise.
     */
    public float getTensionFactorForBlock(int par1, int par2, int par3)
    {
        float f = 0.0F;
        boolean flag = this.difficultySetting == 3;

        if (this.blockExists(par1, par2, par3))
        {
            float f1 = this.getCurrentMoonPhaseFactor();
            f += MathHelper.clamp_float((float)this.getChunkFromBlockCoords(par1, par3).inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
            f += f1 * 0.25F;
        }

        if (this.difficultySetting < 2)
        {
            f *= (float)this.difficultySetting / 2.0F;
        }

        return MathHelper.clamp_float(f, 0.0F, flag ? 1.5F : 1.0F);
    }

    /**
     * Adds a single TileEntity to the world.
     * @param entity The TileEntity to be added.
     */
    public void addTileEntity(TileEntity entity)
    {
        List dest = scanningTileEntities ? addedTileEntityList : loadedTileEntityList;
        if(entity.canUpdate())
        {
            dest.add(entity);
        }
    }

    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param x Block X Position
     * @param y Block Y Position
     * @param z Block Z Position
     * @param side The Side in question
     * @return True if the side is solid
     */
    public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side)
    {
        return isBlockSolidOnSide(x, y, z, side, false);
    }

    /**
     * Determine if the given block is considered solid on the
     * specified side.  Used by placement logic.
     *
     * @param x Block X Position
     * @param y Block Y Position
     * @param z Block Z Position
     * @param side The Side in question
     * @param _default The defult to return if the block doesn't exist.
     * @return True if the side is solid
     */
    @Override
    public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
        {
            return _default;
        }

        Chunk chunk = this.chunkProvider.provideChunk(x >> 4, z >> 4);
        if (chunk == null || chunk.isEmpty())
        {
            return _default;
        }

        Block block = Block.blocksList[getBlockId(x, y, z)];
        if(block == null)
        {
            return false;
        }

        return block.isBlockSolidOnSide(this, x, y, z, side);
    }

    /**
     * Get the persistent chunks for this world
     *
     * @return
     */
    public ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks()
    {
        return ForgeChunkManager.getPersistentChunksFor(this);
    }

    /**
     * Readded as it was removed, very useful helper function
     *
     * @param x X position
     * @param y Y Position
     * @param z Z Position
     * @return The blocks light opacity
     */
    public int getBlockLightOpacity(int x, int y, int z)
    {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
        {
            return 0;
        }

        if (y < 0 || y >= 256)
        {
            return 0;
        }

        return getChunkFromChunkCoords(x >> 4, z >> 4).getBlockLightOpacity(x & 15, y, z & 15);
    }

    /**
     * Returns a count of entities that classify themselves as the specified creature type.
     */
    public int countEntities(EnumCreatureType type, boolean forSpawnCount)
    {
        int count = 0;
        for (int x = 0; x < loadedEntityList.size(); x++)
        {
            if (((Entity)loadedEntityList.get(x)).isCreatureType(type, forSpawnCount))
            {
                count++;
            }
        }
        return count;
    }
}
