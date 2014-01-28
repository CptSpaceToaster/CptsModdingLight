package kovukore.coloredlightscore.src.asm.helpers;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;

public class HelperBlock
{
	public static void initBlockLightHelper()
	{
		Block.l = new Float[] { 0F, 1F / 15, 2F / 15, 3F / 15, 4F / 15, 5F / 15, 6F / 15, 7F / 15, 8F / 15, 9F / 15, 10F / 15, 11F / 15, 12F / 15, 13F / 15, 14F / 15, 1F };
		FMLLog.finer("Testing value of Block.l[1]: " + Block.l[1]);
	}
}