package yamhaven.easycoloredlights.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import yamhaven.easycoloredlights.lib.BlockIds;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.blocks.BlueColoredLightBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Blocks
{
	public static Block whiteColoredLightBlockIdle;
	public static Block blackColoredLightBlockIdle;
	public static Block redColoredLightBlockIdle;
	public static Block greenColoredLightBlockIdle;
	public static Block blueColoredLightBlockIdle;
	public static Block cyanColoredLightBlockIdle;
	public static Block yellowColoredLightBlockIdle;
	public static Block magentaColoredLightBlockIdle;

	public static Block whiteColoredLightBlockOn;
	public static Block blackColoredLightBlockOn;
	public static Block redColoredLightBlockOn;
	public static Block greenColoredLightBlockOn;
	public static Block blueColoredLightBlockOn;
	public static Block cyanColoredLightBlockOn;
	public static Block yellowColoredLightBlockOn;
	public static Block magentaColoredLightBlockOn;
<<<<<<< HEAD
	public static void init() {
		
=======

	public static void init()
	{

>>>>>>> Updated stuffz
		whiteColoredLightBlockIdle = new WhiteColoredLightBlock(BlockIds.whiteLightBlockID_actual, false);
		redColoredLightBlockIdle = new RedColoredLightBlock(BlockIds.redLightBlockID_actual, false);
		greenColoredLightBlockIdle = new GreenColoredLightBlock(BlockIds.greenLightBlockID_actual, false);
		blueColoredLightBlockIdle = new BlueColoredLightBlock(BlockIds.blueLightBlockID_actual, false);
		cyanColoredLightBlockIdle = new CyanColoredLightBlock(BlockIds.cyanLightBlockID_actual, false);
		yellowColoredLightBlockIdle = new YellowColoredLightBlock(BlockIds.yellowLightBlockID_actual, false);
		magentaColoredLightBlockIdle = new MagentaColoredLightBlock(BlockIds.magentaLightBlockID_actual, false);

		whiteColoredLightBlockOn = new WhiteColoredLightBlock(BlockIds.whiteLightBlockID_actual - 16, true);
		redColoredLightBlockOn = new RedColoredLightBlock(BlockIds.redLightBlockID_actual - 16, true);
		greenColoredLightBlockOn = new GreenColoredLightBlock(BlockIds.greenLightBlockID_actual - 16, true);
		blueColoredLightBlockOn = new BlueColoredLightBlock(BlockIds.blueLightBlockID_actual - 16, true);
		cyanColoredLightBlockOn = new CyanColoredLightBlock(BlockIds.cyanLightBlockID_actual - 16, true);
		yellowColoredLightBlockOn = new YellowColoredLightBlock(BlockIds.yellowLightBlockID_actual - 16, true);
		magentaColoredLightBlockOn = new MagentaColoredLightBlock(BlockIds.magentaLightBlockID_actual - 16, true);

		GameRegistry.registerBlock(whiteColoredLightBlockIdle, BlockInfo.whiteColoredLightBlock_name);
		GameRegistry.registerBlock(redColoredLightBlockIdle, BlockInfo.redColoredLightBlock_name);
		GameRegistry.registerBlock(greenColoredLightBlockIdle, BlockInfo.greenColoredLightBlock_name);
		GameRegistry.registerBlock(blueColoredLightBlockIdle, BlockInfo.blueColoredLightBlock_name);
		GameRegistry.registerBlock(cyanColoredLightBlockIdle, BlockInfo.cyanColoredLightBlock_name);
		GameRegistry.registerBlock(yellowColoredLightBlockIdle, BlockInfo.yellowColoredLightBlock_name);
		GameRegistry.registerBlock(magentaColoredLightBlockIdle, BlockInfo.magentaColoredLightBlock_name);
<<<<<<< HEAD
		
=======

>>>>>>> Updated stuffz
	}

	public static void addNames()
	{
		for (int i = 0; i < 16; i++)
		{
			// for 16 blocks
		}
<<<<<<< HEAD
		
=======

>>>>>>> Updated stuffz
		LanguageRegistry.addName(new ItemStack(whiteColoredLightBlockIdle, 1, 0), BlockInfo.whiteColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(redColoredLightBlockIdle, 1, 0), BlockInfo.redColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(greenColoredLightBlockIdle, 1, 0), BlockInfo.greenColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(blueColoredLightBlockIdle, 1, 0), BlockInfo.blueColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(cyanColoredLightBlockIdle, 1, 0), BlockInfo.cyanColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(yellowColoredLightBlockIdle, 1, 0), BlockInfo.yellowColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(magentaColoredLightBlockIdle, 1, 0), BlockInfo.magentaColoredLightBlock_name);
<<<<<<< HEAD
		
=======

>>>>>>> Updated stuffz
	}

	public static void addBlockRecipes()
	{
		for (int i = 0; i < 16; i++)
		{
			// add recipes
		}
	}
}