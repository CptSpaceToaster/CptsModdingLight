package kovukore.asm.overriddenclasses;

import java.util.Random;

import net.minecraft.crash.CrashReport;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ReportedException;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.WorldSpecificSaveHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kovukore.asm.transformer.ASMAddField;

public class Lights_World extends World {
	private int ambientTickCountdown;
//	private Random rand;
//	private ISaveHandler saveHandler;
//	private Profiler theProfiler;
//	private WorldInfo worldInfo;
//	private WorldProvider provider;
//	private MapStorage perWorldStorage;
//	private ILogAgent worldLogAgent;
//	private MapStorage mapStorage;
//	private IChunkProvider chunkProvider;
//	
//	private MapStorage getMapStorage(ISaveHandler par1iSaveHandler) {
//		return null;
//	}
//	private IChunkProvider createChunkProvider() {
//		return null;
//	}
//	private void initialize(WorldSettings par3WorldSettings) {	
//	}
//	private void addWorldInfoToCrashReport(CrashReport crashreport) {
//	}
	
	
	@ASMAddField
	private long[] lightUpdateBlockList;
	
	
	//Replace Constructor again
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
	
	


	


	
}
