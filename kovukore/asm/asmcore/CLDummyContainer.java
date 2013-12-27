package kovukore.asm.asmcore;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CLDummyContainer extends DummyModContainer
{
	public CLDummyContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "ColoredLightsCore";
		meta.name = "ColoredLightsCore";
		meta.version = "1.0.0";
		meta.credits = "";
		meta.authorList = Arrays.asList("Kovu (Lion-O), CptSpaceToaster");
		meta.description = "The coremod for Colored Lights";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) 
	{
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void modConstruction(FMLConstructionEvent evt)
	{
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent evt)
	{
	}

	@Subscribe
	public void init(FMLInitializationEvent evt) 
	{
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent evt)
	{
	}
}