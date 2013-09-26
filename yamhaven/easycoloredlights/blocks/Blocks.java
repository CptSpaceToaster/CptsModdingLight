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
	public static Block redColoredLightBlock;
	public static Block greenColoredLightBlock;
	public static Block blueColoredLightBlock;
	
	public static void init() {
		redColoredLightBlock = new RedColoredLightBlock(BlockIds.redLightBlockID_actual);
		greenColoredLightBlock = new GreenColoredLightBlock(BlockIds.greenLightBlockID_actual);
		blueColoredLightBlock = new BlueColoredLightBlock(BlockIds.blueLightBlockID_actual);

		GameRegistry.registerBlock(redColoredLightBlock, BlockInfo.redColoredLighBlock_name);
		GameRegistry.registerBlock(greenColoredLightBlock, BlockInfo.greenColoredLighBlock_name);
		GameRegistry.registerBlock(blueColoredLightBlock, BlockInfo.blueColoredLighBlock_name);
	}
	
	public static void addNames() {
		for(int i = 0; i<16; i++) {
			//for 16 blocks
		}
		LanguageRegistry.addName(new ItemStack(redColoredLightBlock, 1, 0), BlockInfo.redColoredLighBlock_name);
		LanguageRegistry.addName(new ItemStack(greenColoredLightBlock, 1, 0), BlockInfo.greenColoredLighBlock_name);
		LanguageRegistry.addName(new ItemStack(blueColoredLightBlock, 1, 0), BlockInfo.blueColoredLighBlock_name);
	}
	
	public static void addBlockRecipes() {
		for(int i = 0; i<16; i++) {
			//add recipes
		}
	}
}