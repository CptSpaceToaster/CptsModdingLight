package kovukore.coloredlights.src.asm;

import java.util.Arrays;

import yamhaven.easycoloredlights.EasyColoredLights;
import yamhaven.easycoloredlights.blocks.CLBlocksController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ColoredLightsCoreDummyContainer extends DummyModContainer
{
	public ColoredLightsCoreDummyContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "coloredlightscore";
		meta.name = "Colored Lights Core";
		meta.version = "1.0.1";
		meta.credits = "";
		meta.authorList = Arrays.asList("AJWGeek", "Kovu", "CptSpaceToaster", "heaton84");
		meta.description = "The coremod for Colored Lights";
	}
		
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
    	MinecraftForge.EVENT_BUS.register(this); // This causes "Unable to determine registrant mod for coloredlightscore. This is a critical error and should be impossible"
    											 // But it's OK because it still works... ???
    	
        return true;
    }	
    
    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {
    	FMLLog.info("EVENT_BUS.preInit");
    }
    		
	@SubscribeEvent	
	public void LoadChunk(ChunkDataEvent event)
	{
		NBTTagCompound data = event.getData();
		Chunk chunk = event.getChunk();
		
		//TODO: Inject RGB nibble arrays
		//FMLLog.info("$$$ Load event (%s, %s)", chunk.xPosition, chunk.zPosition);
	}

	@SubscribeEvent	
	public void SaveChunk(ChunkDataEvent event)
	{
		NBTTagCompound data = event.getData();
		Chunk chunk = event.getChunk();

		//FMLLog.info("$$$ Save event (%s, %s)", chunk.xPosition, chunk.zPosition);	
	}	
}