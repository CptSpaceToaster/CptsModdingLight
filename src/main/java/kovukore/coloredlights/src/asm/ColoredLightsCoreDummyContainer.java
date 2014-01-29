package kovukore.coloredlights.src.asm;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import kovukore.coloredlights.src.api.test.BlockTest;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ColoredLightsCoreDummyContainer extends DummyModContainer
{
	public final static Block genericDirt = new BlockTest(Material.piston).func_149647_a(CreativeTabs.tabTools);

	public ColoredLightsCoreDummyContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "ColoredLightsCore";
		meta.name = "ColoredLightsCore";
		meta.version = "1.0.1";
		meta.credits = "";
		meta.authorList = Arrays.asList("AJWGeek", "Kohvough", "CptSpaceToaster");
		meta.description = "The coremod for Colored Lights";
	}

	@Subscribe
	public void registerThis(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Subscribe
	public void registerThis(FMLPreInitializationEvent e)
	{
		GameRegistry.registerBlock(genericDirt, "testLight");
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}
}