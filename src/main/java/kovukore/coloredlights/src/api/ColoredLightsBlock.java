package kovukore.coloredlights.src.api;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ColoredLightsBlock extends Block
{
	protected ColoredLightsBlock(Material material)
	{
		super(material);
	}

	public Block addColorLightValue(float r, float g, float b)
	{
		this.field_149784_t &= 15;
		this.field_149784_t |= ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
		return this;
	}

	@Override
	public Block func_149715_a(float par1)
	{
		this.field_149784_t = (int) (15.0F * par1);
		return this.addColorLightValue(par1, par1, par1);
	}
}