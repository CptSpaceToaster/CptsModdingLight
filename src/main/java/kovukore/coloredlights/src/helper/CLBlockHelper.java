package kovukore.coloredlights.src.helper;

import kovukore.coloredlights.src.api.CLApi;
import net.minecraft.block.Block;

public class CLBlockHelper
{	
	public static Block setLightLevel(Block interceptedReturnValue, Block instance, float par1)
	{
		return CLApi.injectCLV(instance, par1, par1, par1);
	}
}