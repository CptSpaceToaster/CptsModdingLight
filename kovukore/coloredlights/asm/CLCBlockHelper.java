package kovukore.coloredlights.asm;

import java.util.logging.Level;

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
import cpw.mods.fml.common.FMLLog;

public class CLCBlockHelper
{
	public static void initializeBlocks()
	{
		FMLLog.log(Level.INFO, "ColoredLightsCore is PATCHING vanilla blocks");
		try
		{
			//TODO: This won't run in an obfuscated environment, and it receives the following error
			//Crash Report: http://i.imgur.com/ti5kYJV.png
			//Consol Barf:  http://i.imgur.com/jAjnV0a.png
			
			
			//Null the existing entries
			Block.blocksList[Block.lavaMoving.blockID] = null;
			Block.blocksList[Block.lavaStill.blockID] = null;
			Block.blocksList[Block.torchWood.blockID] = null;
			Block.blocksList[Block.fire.blockID] = null;
			Block.blocksList[Block.oreRedstoneGlowing.blockID] = null;
			Block.blocksList[Block.torchRedstoneActive.blockID] = null;
			Block.blocksList[Block.portal.blockID] = null;
			Block.blocksList[Block.redstoneRepeaterActive.blockID] = null;
			Block.blocksList[Block.redstoneComparatorActive.blockID] = null;
			//Add our own version
			Block.blocksList[Block.lavaMoving.blockID] = (BlockFluid) (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_flow").addColorLightValue(1.0F, 0.8F, 0.667F);
			Block.blocksList[Block.lavaStill.blockID] = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_still").addColorLightValue(1.0F, 0.8F, 0.667F);
			Block.blocksList[Block.torchWood.blockID] = (new BlockTorch(50)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("torch").setTextureName("torch_on").addColorLightValue(0.933F, 0.933F, 0.733F);
			Block.blocksList[Block.fire.blockID] = (BlockFire) (new BlockFire(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("fire").disableStats().setTextureName("fire").addColorLightValue(1.0F, 0.933F, 0.733F);
			Block.blocksList[Block.oreRedstoneGlowing.blockID] = (new BlockRedstoneOre(74, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("oreRedstone").setTextureName("redstone_ore").addColorLightValue(0.6F, 0.533F, 0.533F);
			Block.blocksList[Block.torchRedstoneActive.blockID] = (new BlockRedstoneTorch(76, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("redstone_torch_on").addColorLightValue(0.467F, 0.333F, 0.333F);
			Block.blocksList[Block.portal.blockID] = (BlockPortal) (new BlockPortal(90)).setHardness(-1.0F).setStepSound(Block.soundGlassFootstep).setLightValue(0.75F).setUnlocalizedName("portal").setTextureName("portal").addColorLightValue(0.333F, 0.067F, 0.733F);
			Block.blocksList[Block.redstoneRepeaterActive.blockID] = (BlockRedstoneRepeater) (new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("diode").disableStats().setTextureName("repeater_on").addColorLightValue(0.6F, 0.467F, 0.467F);
			Block.blocksList[Block.redstoneComparatorActive.blockID] = (BlockComparator) (new BlockComparator(150, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("comparator").disableStats().setTextureName("comparator_on").addColorLightValue(0.6F, 0.467F, 0.467F);
		}
		catch (Throwable e)
		{
			FMLLog.log(Level.INFO, "ColoredLightsCore FAILED patching vanilla blocks!");
			System.err.println(e.getMessage()); 
			return;
		}
		FMLLog.log(Level.INFO, "ColoredLightsCore is FINISHED vanilla blocks");
	}
}