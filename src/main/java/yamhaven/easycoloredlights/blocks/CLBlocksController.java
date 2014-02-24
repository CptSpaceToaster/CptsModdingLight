package yamhaven.easycoloredlights.blocks;

import yamhaven.easycoloredlights.items.ItemCLBlock;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;

public class CLBlocksController {
	public static CLLamp CLBlockIdle;
	public static CLLamp CLBlockOn;
	public static CLStone CLStone;
	
	public static void init()
	{
		CLBlockIdle = (CLLamp) new CLLamp(false).setBlockName(BlockInfo.CLLamp);
		CLBlockOn = (CLLamp) new CLLamp(true).setBlockName(BlockInfo.CLLamp + "On");
		CLStone = (CLStone) new CLStone().setBlockName(BlockInfo.CLStone);
		
		CLBlockIdle.setSwitchBlock(CLBlockOn);
		CLBlockOn.setSwitchBlock(CLBlockIdle);
	
	}
	
	public static void registerBlocks() 
	{
		GameRegistry.registerBlock(CLBlockIdle, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp);
		GameRegistry.registerBlock(CLBlockOn, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLLamp + "On");
		GameRegistry.registerBlock(CLStone, ItemCLBlock.class, ModInfo.ID + BlockInfo.CLStone);
	}
	
	public static void addBlockRecipes()
	{
		for (int i = 0; i < 16; i++)
		{
			// add recipes
		}
	}
}
