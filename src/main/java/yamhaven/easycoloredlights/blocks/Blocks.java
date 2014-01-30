package yamhaven.easycoloredlights.blocks;

import net.minecraft.block.Block;

public class Blocks {
	public static Block greenCLBlockIdle;
	
	public static Block greenCLBlockOn;
	
	public static void init()
	{
		greenCLBlockIdle = new GreenCLBlock(3877, 3878, false);//I think Block ID's were removed entirely...
		
		//GameRegistry.registerBlock(greenCLBlockIdle, "Green Colored Lamp");
	}
	
	public static void addNames() 
	{
		for (int i = 0; i < 16; i++)
		{
			// for 16 blocks
		}
		//LanguageRegistry.addName(new ItemStack(greenCLBlockIdle, 1, 0), "Green Colored Lamp");
	}
	
	public static void addBlockRecipes()
	{
		for (int i = 0; i < 16; i++)
		{
			// add recipes
		}
	}
}
