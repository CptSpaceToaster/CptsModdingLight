package kovukore.coloredlights.src.asm;

import java.util.Arrays;

import kovukore.coloredlights.src.api.CLApi;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
		meta.authorList = Arrays.asList("AJWGeek", "Kovu", "CptSpaceToaster");
		meta.description = "The coremod for Colored Lights";
	}
		
	
	@Subscribe
	public void registerThis(FMLPreInitializationEvent e)
	{
		
	}
	
	@Subscribe
	public void registerThis(FMLInitializationEvent e)
	{		
		MinecraftForge.EVENT_BUS.register(this);		
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void init(FMLInitializationEvent evt) {

		// Set up block color values
		
		CLApi.injectCLV(Blocks.lava, CLApi.l[15], CLApi.l[12], CLApi.l[10]);
		CLApi.injectCLV(Blocks.torch, CLApi.l[14], CLApi.l[14], CLApi.l[11]);
		CLApi.injectCLV(Blocks.fire, CLApi.l[15], CLApi.l[14], CLApi.l[11]);
		CLApi.injectCLV(Blocks.redstone_ore, CLApi.l[9], CLApi.l[8], CLApi.l[8]);
		CLApi.injectCLV(Blocks.redstone_torch, CLApi.l[7], CLApi.l[5], CLApi.l[5]);
		CLApi.injectCLV(Blocks.portal, CLApi.l[5], CLApi.l[1], CLApi.l[11]);

		// TODO: Moving lava?   lavaMoving.addColorLightValue(l[15], l[12], l[10]);
		// TODO: Redstone repeaters?  redstoneRepeaterActive.addColorLightValue(l[9], l[7], l[7]);
	}
	
}