package yamhaven.easycoloredlights.blocks;

import kovukore.coloredlights.src.api.CLApi;
import net.minecraft.init.Blocks;
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
		
		// Inject RGB values into vanilla blocks		
		CLApi.injectCLV(Blocks.lava, CLApi.l[15], CLApi.l[12], CLApi.l[10]);
		CLApi.injectCLV(Blocks.torch, CLApi.l[14], CLApi.l[14], CLApi.l[11]);
		CLApi.injectCLV(Blocks.fire, CLApi.l[15], CLApi.l[14], CLApi.l[11]);
		CLApi.injectCLV(Blocks.redstone_ore, CLApi.l[9], CLApi.l[8], CLApi.l[8]);
		CLApi.injectCLV(Blocks.redstone_torch, CLApi.l[7], CLApi.l[5], CLApi.l[5]);
		CLApi.injectCLV(Blocks.portal, CLApi.l[5], CLApi.l[1], CLApi.l[11]);

		// TODO: Moving lava?   lavaMoving.addColorLightValue(l[15], l[12], l[10]);
		// TODO: Redstone repeaters?  redstoneRepeaterActive.addColorLightValue(l[9], l[7], l[7]);		
	}
	
	public static void addBlockRecipes()
	{
		for (int i = 0; i < 16; i++)
		{
			// add recipes
		}
	}
}
