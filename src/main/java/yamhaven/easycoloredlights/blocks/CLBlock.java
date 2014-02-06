package yamhaven.easycoloredlights.blocks;

import java.util.Random;

import yamhaven.easycoloredlights.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class CLBlock extends Block
{
	/** Whether this lamp block is the powered version of the block. */
	protected final boolean powered;
	/** The Block the lamp is supposed to switch to **/
	protected Block switchBlock = null;
	/** internal storage for our block's unlocalized name... this may redundant **/
	private String unlocalizedName;
	
	public CLBlock(boolean isPowered, String unlocalizedName)
	{
		super(Material.field_151591_t);
		this.powered = isPowered;
		this.unlocalizedName = unlocalizedName;
		
		func_149663_c(unlocalizedName);
		func_149711_c(0.3F);
		func_149672_a(field_149778_k);
		
		if (isPowered)
			turnLightsOn();
		else
			func_149647_a(CreativeTabs.tabDecorations);
	}

	protected void setSwitchBlock(Block switchBlock) {
		this.switchBlock = switchBlock;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	@SideOnly(Side.CLIENT)
	@Override
	public void func_149651_a(IIconRegister iconRegister) {	//registerIcons()
		icon = iconRegister.registerIcon(ModInfo.ID + ":" + unlocalizedName);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_149691_a(int side, int meta)
	{
		return icon;
	}

	@SideOnly(Side.CLIENT)
    public Item func_149694_d(World world, int x, int y, int z)
    {
        return Item.func_150898_a((powered)?switchBlock:this);
    }
	
	public Item func_149650_a(int par1, Random par2Random, int par3) {
		return Item.func_150898_a((powered)?switchBlock:this);
	}
	
	protected ItemStack func_149644_j(int meta)
    {
        return new ItemStack((powered)?switchBlock:this);
    }
	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void func_149726_b(World par1World, int par2, int par3, int par4)
	{
		if (!par1World.isRemote)
		{
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147464_a(par2, par3, par4, this, 4);
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
	@Override
	public void func_149695_a(World par1World, int par2, int par3, int par4, Block par5)
	{
		if (!par1World.isRemote)
		{
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			{
				par1World.func_147464_a(par2, par3, par4, this, 4);
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
	@Override
	public void func_149674_a(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
		{
			par1World.func_147465_d(par2, par3, par4, switchBlock, 0, 2);
		}
	}

	protected void turnLightsOn() {
		func_149715_a(1.0F);
	}
}
