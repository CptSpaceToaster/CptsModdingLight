package yamhaven.easycoloredlights.blocks;

import java.util.List;

import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class CLStone extends Block
{
	public CLStone()
	{
		super(Material.field_151592_s);
		
		func_149711_c(0.3F);
		func_149672_a(field_149778_k);
		func_149715_a(1.0F);
	}

	
	@SideOnly(Side.CLIENT)
	private IIcon icons[];

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149651_a(IIconRegister iconRegister) {	//registerIcons()
		icons = new IIcon[16];
		for(int i = 0; i<icons.length; i++) {
			icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLStone + i);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_149691_a(int side, int meta)
	{
		return icons[meta];
	}

	@Override
    public int func_149692_a(int meta)	//DamageDropped
    {
        return meta;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override
	public void func_149666_a(Item par1, CreativeTabs par2CreativeTabs, List par3List)	//getBlockSubtypes
    {
          for(int i = 0; i < 16; i++)
          {
                 par3List.add(new ItemStack(par1, 1, i));
          }
    }
}
