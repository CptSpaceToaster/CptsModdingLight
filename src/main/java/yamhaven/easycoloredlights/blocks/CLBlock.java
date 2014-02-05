package yamhaven.easycoloredlights.blocks;

import java.util.List;
import java.util.Random;

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
import net.minecraft.world.World;

public class CLBlock extends Block
{
	/** Whether this lamp block is the powered version of the block. */
	protected final boolean powered;
	/** The Block the lamp is supposed to switch to **/
	protected Block switchBlock = null;
	
	public CLBlock(boolean isPowered, Material matt)
	{
		super(matt);
		this.powered = isPowered;
		
		if (isPowered)
		{
			//turnLightsOn(new Random());
		}
		
		func_149711_c(0.3F);
		func_149672_a(field_149778_k);
	}

	protected void setSwitchBlock(Block switchBlock) {
		this.switchBlock = switchBlock;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon icons[];

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149651_a(IIconRegister iconRegister) {				//registerIcons()
		icons = new IIcon[16];
		for(int i = 0; i<icons.length; i++) {
			icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLUnlocalizedName + i);
		}
	}
	
	@SideOnly(Side.CLIENT)
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

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		if (!par1World.isRemote)
		{
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147464_a(par2, par3, par4, switchBlock, 4);
			}
			else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147465_d(par2, par3, par4, switchBlock, 0, 2);
			}
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
	{
		if (!par1World.isRemote)
		{
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147464_a(par2, par3, par4, switchBlock, 4);
			}
			else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147465_d(par2, par3, par4, switchBlock, 0, 2);
			}
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
		{
			par1World.func_147465_d(par2, par3, par4, switchBlock, 0, 2);
		}
	}

	//protected abstract void turnLightsOn(Random r);
}
