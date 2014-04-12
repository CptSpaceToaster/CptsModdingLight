package yamhaven.easycoloredlights.blocks;

import java.util.List;

import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.api.CLBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CLTestBlock extends CLBlock {
	public CLTestBlock()
	{
		super(Material.glass);
		//setLightLevel(1.0f)
		CLApi.setBlockColorRGB(this, 1.0f, 0f, 0f);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		for(int i = 0; i < 16; i++)
        {
			par3List.add(new ItemStack(par1, 1, i));
        }
    }	
	
	@Override
	public int getColorLightValue(int metadata) {
		return CLApi.makeRGBLightValue(metadata, metadata, 0);
	}
}
