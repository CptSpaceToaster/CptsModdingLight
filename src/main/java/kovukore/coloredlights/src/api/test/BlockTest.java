package kovukore.coloredlights.src.api.test;

import net.minecraft.block.material.Material;
import kovukore.coloredlights.src.api.ColoredLightsBlock;

public class BlockTest extends ColoredLightsBlock
{
	public BlockTest(Material material)
	{
		super(material);
		this.addColorLightValue(15F, 0F, 0F);
	}
}