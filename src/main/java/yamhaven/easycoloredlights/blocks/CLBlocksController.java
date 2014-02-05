package yamhaven.easycoloredlights.blocks;

import yamhaven.easycoloredlights.items.ItemCLBlock;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class CLBlocksController {
	public static Block CLBlockIdle;
	public static Block CLBlockOn;
	
	public static void init()
	{
		CLBlockIdle = new CLBlock(false).func_149663_c(BlockInfo.CLUnlocalizedName).func_149647_a(CreativeTabs.tabDecorations);	//setBlockName(BlockInfo.CLUnlocalizedName)
		CLBlockOn = new CLBlock(true).func_149663_c(BlockInfo.CLUnlocalizedName+"On");	//setBlockName(BlockInfo.CLUnlocalizedName+"On")
				
		((CLBlock) CLBlockIdle).setSwitchBlock(CLBlockOn);
		((CLBlock) CLBlockOn).setSwitchBlock(CLBlockIdle);
		
		GameRegistry.registerBlock(CLBlockIdle, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLUnlocalizedName);
		GameRegistry.registerBlock(CLBlockOn, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLUnlocalizedName+"On");
		
	}
	
	public static void addNames() 
	{
		for (int i = 0; i < 16; i++)
		{
			// for 16 blocks
		}
	}
	
	public static void addBlockRecipes()
	{
		for (int i = 0; i < 16; i++)
		{
			// add recipes
		}
	}
}
