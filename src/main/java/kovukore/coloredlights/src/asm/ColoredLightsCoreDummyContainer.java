package kovukore.coloredlights.src.asm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import yamhaven.easycoloredlights.blocks.CLBlocksController;

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
		meta.name = "coloredlightscore";
		meta.version = "1.0.1";
		meta.credits = "";
		meta.authorList = Arrays.asList("AJWGeek", "Kohvough", "CptSpaceToaster");
		meta.description = "The coremod for Colored Lights";
	}
	
	@Subscribe
	public void registerThis(FMLPreInitializationEvent e)
	{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);

		CLBlocksController.init();
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
}