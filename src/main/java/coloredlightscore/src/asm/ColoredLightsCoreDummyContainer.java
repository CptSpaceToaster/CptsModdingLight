package coloredlightscore.src.asm;

import java.util.Arrays;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import coloredlightscore.fmlevents.ChunkDataEventHandler;
import coloredlightscore.network.ChannelHandler;
import coloredlightscore.src.api.CLApi;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ColoredLightsCoreDummyContainer extends DummyModContainer
{
	public ChunkDataEventHandler chunkDataEventHandler;
	
	public static org.apache.logging.log4j.Logger CLLog = FMLLog.getLogger();
	
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

    	CLLog = evt.getModLog();

    	CLLog.info("Starting up ColoredLightsCore");
    	
    	// Spin up network handler
    	ChannelHandler.INSTANCE = new ChannelHandler();
    	
    	// Hook into chunk events
    	MinecraftForge.EVENT_BUS.register(chunkDataEventHandler);
    	
    	/*FMLLog.info("##### Testing: %s", MCP_ENVIRONMENT);
    	FMLLog.info("##### Testing: %s", ASMUtils.getClassName("net.minecraft.block.Block"));
    	FMLLog.info("##### Testing: %s", ASMUtils.getMethod("net.minecraft.block.Block", "setLightLevel", "(F)Lnet/minecraft/block/Block;"));
    	FMLLog.info("##### Testing: %s", ASMUtils.getMethod("ahu", "a", "(F)Lahu;"));*/
    }	
    
    @Subscribe
    public void postInit(FMLPostInitializationEvent evt)
    {
		// Inject RGB values into vanilla blocks		
		CLApi.injectCLV(Blocks.lava, CLApi.l[15], CLApi.l[12], CLApi.l[10]);
		CLApi.injectCLV(Blocks.flowing_lava, CLApi.l[15], CLApi.l[12], CLApi.l[10]);
		CLApi.injectCLV(Blocks.torch, CLApi.l[14], CLApi.l[13], CLApi.l[12]);
		CLApi.injectCLV(Blocks.fire, CLApi.l[15], CLApi.l[14], CLApi.l[11]);
		CLApi.injectCLV(Blocks.redstone_ore, CLApi.l[9], CLApi.l[6], CLApi.l[6]);
		CLApi.injectCLV(Blocks.redstone_torch, CLApi.l[7], CLApi.l[4], CLApi.l[4]);
		CLApi.injectCLV(Blocks.portal, CLApi.l[6], CLApi.l[3], CLApi.l[12]);
		CLApi.injectCLV(Blocks.powered_repeater, CLApi.l[9], CLApi.l[7], CLApi.l[7]);

    }
}