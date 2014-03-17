package kovukore.coloredlights.src.asm;

import java.util.Arrays;

import kovukore.coloredlights.fmlevents.ChunkDataEventHandler;
import kovukore.coloredlights.network.ChannelHandler;
import net.minecraftforge.common.MinecraftForge;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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