package kovukore.coloredlights.src.api;

import net.minecraft.block.Block;

public class CLBlockHelper
{
	public static Block addColorLightValue(Block block, float r, float g, float b)
	{
//		block.field_149784_t &= 15;
//		block.field_149784_t |= ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
		return block;
	}

	public static Block func_149715_a(Block block, float par1)
	{
//		block.field_149784_t = (int) (15.0F * par1);
		return addColorLightValue(block, par1, par1, par1);
	}
}