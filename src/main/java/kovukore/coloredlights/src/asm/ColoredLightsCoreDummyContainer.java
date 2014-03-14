package kovukore.coloredlights.src.asm;

import java.util.Arrays;

import kovukore.coloredlights.fmlevents.ChunkDataEventHandler;
import kovukore.coloredlights.network.ChannelHandler;
import kovukore.coloredlights.src.api.CLStorage;
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
import cpw.mods.fml.common.network.NetworkRegistry;

public class ColoredLightsCoreDummyContainer extends DummyModContainer
{
	public ChunkDataEventHandler chunkDataEventHandler;
	
	public ColoredLightsCoreDummyContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "coloredlightscore";
		meta.name = "Colored Lights Core";
		meta.version = "1.0.2";
		meta.credits = "";
		meta.authorList = Arrays.asList("AJWGeek", "Kovu", "CptSpaceToaster", "heaton84");
		meta.description = "The coremod for Colored Lights";
		
		chunkDataEventHandler = new ChunkDataEventHandler();
	}
		
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);

        return true;
    }	
    
    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {

    	// Spin up network handler
    	ChannelHandler.INSTANCE = new ChannelHandler();
    	
    	// Hook into chunk events
    	MinecraftForge.EVENT_BUS.register(chunkDataEventHandler);
    }	
}