package kovukore.coloredlights.src.helper;

import kovukore.coloredlights.src.api.CLApi;
import net.minecraft.block.Block;

public class CLBlockHelper
{	
	public static Block setLightLevel(Block interceptedReturnValue, Block block, float par1)
	{
		// Transformed via HelperClassTransformer.addReturnMethod
		// Hijacks the vanilla return statement
		
		//Done via Mojang's code: block.lightValue = (int) (15.0F * par1);
		return CLApi.injectCLV(block, par1, par1, par1);
	}
}