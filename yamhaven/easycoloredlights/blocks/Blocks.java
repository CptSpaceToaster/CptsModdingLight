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

public class Blocks {
	public static Block whiteColoredLightBlock;
	public static Block blackColoredLightBlock;
	public static Block redColoredLightBlock;
	public static Block greenColoredLightBlock;
	public static Block blueColoredLightBlock;
	public static Block cyanColoredLightBlock;
	public static Block yellowColoredLightBlock;
	public static Block magentaColoredLightBlock;
	
	public static void init() {
		whiteColoredLightBlock = new WhiteColoredLightBlock(BlockIds.whiteLightBlockID_actual);
		blackColoredLightBlock = new BlackColoredLightBlock(BlockIds.blackLightBlockID_actual);
		redColoredLightBlock = new RedColoredLightBlock(BlockIds.redLightBlockID_actual);
		greenColoredLightBlock = new GreenColoredLightBlock(BlockIds.greenLightBlockID_actual);
		blueColoredLightBlock = new BlueColoredLightBlock(BlockIds.blueLightBlockID_actual);
		cyanColoredLightBlock = new CyanColoredLightBlock(BlockIds.cyanLightBlockID_actual);
		yellowColoredLightBlock = new YellowColoredLightBlock(BlockIds.yellowLightBlockID_actual);
		magentaColoredLightBlock = new MagentaColoredLightBlock(BlockIds.magentaLightBlockID_actual);

		GameRegistry.registerBlock(whiteColoredLightBlock, BlockInfo.whiteColoredLightBlock_name);
		GameRegistry.registerBlock(blackColoredLightBlock, BlockInfo.blackColoredLightBlock_name);
		GameRegistry.registerBlock(redColoredLightBlock, BlockInfo.redColoredLightBlock_name);
		GameRegistry.registerBlock(greenColoredLightBlock, BlockInfo.greenColoredLightBlock_name);
		GameRegistry.registerBlock(blueColoredLightBlock, BlockInfo.blueColoredLightBlock_name);
		GameRegistry.registerBlock(cyanColoredLightBlock, BlockInfo.cyanColoredLightBlock_name);
		GameRegistry.registerBlock(yellowColoredLightBlock, BlockInfo.yellowColoredLightBlock_name);
		GameRegistry.registerBlock(magentaColoredLightBlock, BlockInfo.magentaColoredLightBlock_name);
	}
	
	public static void addNames() {
		for(int i = 0; i<16; i++) {
			//for 16 blocks
		}
		LanguageRegistry.addName(new ItemStack(whiteColoredLightBlock, 1, 0), BlockInfo.whiteColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(blackColoredLightBlock, 1, 0), BlockInfo.blackColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(redColoredLightBlock, 1, 0), BlockInfo.redColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(greenColoredLightBlock, 1, 0), BlockInfo.greenColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(blueColoredLightBlock, 1, 0), BlockInfo.blueColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(cyanColoredLightBlock, 1, 0), BlockInfo.cyanColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(yellowColoredLightBlock, 1, 0), BlockInfo.yellowColoredLightBlock_name);
		LanguageRegistry.addName(new ItemStack(magentaColoredLightBlock, 1, 0), BlockInfo.magentaColoredLightBlock_name);
		
	}
	
	public static void addBlockRecipes() {
		for(int i = 0; i<16; i++) {
			//add recipes
		}
	}
}