package yamhaven.easycoloredlights.blocks;

import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;

public class CLBlocksController {
	public static CLBlock[] CLBlockIdle = new CLBlock[16];
	public static CLBlock[] CLBlockOn = new CLBlock[16];
	public static CLStone[] CLStone = new CLStone[16];
	
	public static void init()
	{
		for (int i = 0; i < 16; i++)
		{
			CLBlockIdle[i] = new CLBlock(false, BlockInfo.CLBlock + i);
			CLBlockOn[i] = new CLBlock(true, BlockInfo.CLBlock + "On" + i);
			CLStone[i] = new CLStone(BlockInfo.CLStone + i);
			
			CLBlockIdle[i].setSwitchBlock(CLBlockOn[i]);
			CLBlockOn[i].setSwitchBlock(CLBlockIdle[i]);	
		}
	}
	
	public static void registerBlocks() 
	{
		for (int i = 0; i < 16; i++)
		{
			GameRegistry.registerBlock(CLBlockIdle[i], ModInfo.ID + BlockInfo.CLBlock + i);
			GameRegistry.registerBlock(CLBlockOn[i], ModInfo.ID + BlockInfo.CLBlock + "On" + i);
			GameRegistry.registerBlock(CLStone[i], ModInfo.ID + BlockInfo.CLStone + i);
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
