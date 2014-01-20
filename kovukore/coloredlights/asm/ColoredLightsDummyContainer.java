package kovukore.coloredlights.asm;

import java.util.Arrays;
import java.util.logging.Level;

import org.objectweb.asm.tree.MethodNode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockComparator;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowing;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockStationary;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ColoredLightsDummyContainer extends DummyModContainer
{
	public ColoredLightsDummyContainer()
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