package yamhaven.easycoloredlights.blocks;

import yamhaven.easycoloredlights.items.ItemCLBlock;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class CLBlocksController {
	public static Block CLBlockIdle;
	public static Block CLBlockOn;
	
	public static void init()
	{
		System.out.println("     -----------------------      initing");
		CLBlockIdle = new CLBlock(null, false);
		GameRegistry.registerBlock(CLBlockIdle, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLUnlocalizedName);
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
