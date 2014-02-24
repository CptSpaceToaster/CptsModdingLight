package kovukore.coloredlights.src.api;

import net.minecraft.block.Block;

public class CLBlockHelper
{	
	public static Block injectCLV(Block block, float r, float g, float b) {
		block.lightValue = CLApi.makeColorLightValue(r, g, b, block.lightValue);
		return block;
	}
	
	public static Block setLightValue(Block block, float par1)
	{
		block.lightValue = (int) (15.0F * par1);
		return injectCLV(block, par1, par1, par1);
	}
}