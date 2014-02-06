package yamhaven.easycoloredlights.blocks;

import yamhaven.easycoloredlights.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class CLStone extends Block
{
	private String unlocalizedName;
	
	public CLStone(String unlocalizedName)
	{
		super(Material.field_151592_s);
		this.unlocalizedName = unlocalizedName;
		
		func_149663_c(unlocalizedName);
		func_149711_c(0.3F);
		func_149672_a(field_149778_k);
		turnLightsOn();
		func_149647_a(CreativeTabs.tabDecorations);
	}

	
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149651_a(IIconRegister iconRegister) {
		icon = iconRegister.registerIcon(ModInfo.ID + ":" + unlocalizedName);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_149691_a(int side, int meta)
	{
		return icon;
	}
	
	private void turnLightsOn() {
		func_149715_a(1.0F);
	}
}
