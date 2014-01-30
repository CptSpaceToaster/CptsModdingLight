package yamhaven.easycoloredlights.blocks;

import java.util.Random;

import kovukore.coloredlights.src.api.ColoredLightsBlock;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public abstract class CLBlock extends ColoredLightsBlock
{
	/** Whether this lamp block is the powered version of the block. */
	protected final boolean powered;
	/** The ID the lamp is supposed to switch to **/
	protected final int switchID;

	public CLBlock(int startID, int switchID, boolean isPowered)
	{
		super(Material.field_151591_t);				//Material.resdstoneLight
		this.powered = isPowered;
		this.switchID = switchID;
		if (isPowered)
		{
			turnLightsOn(new Random());
		}

		func_149711_c(0.3F);						//setHardness(0.3F);
		func_149672_a(field_149778_k);				//setStepSound(Block.soundGlassFootstep);
		func_149647_a(CreativeTabs.tabDecorations);	//setCreativeTab(CreativeTabs.tabDecorations);
	}

//	@SideOnly(Side.CLIENT)
//	protected Icon blockIcon;

//	@SideOnly(Side.CLIENT)
//	public Icon getIcon(int id, int meta)
//	{
//		return blockIcon;
//	}

//	@SideOnly(Side.CLIENT)
//	public int idPicked(World world, int x, int y, int z) {
//        return this.blockID+(this.blockID<BlockIds.whiteLightBlockID_actual?16:0);
//    }
	
//	@SideOnly(Side.CLIENT)
//	public int idDropped(int par1, Random random, int par3) {
//		return this.blockID+(this.blockID<BlockIds.whiteLightBlockID_actual?16:0);
//    }

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
//	public void onBlockAdded(World par1World, int par2, int par3, int par4)
//	{
//		if (!par1World.isRemote)
//		{
//			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
//			{
//				par1World.scheduleBlockUpdate(par2, par3, par4, switchID, 4);
//			}
//			else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
//			{
//				par1World.setBlock(par2, par3, par4, switchID, 0, 2);
//			}
//		}
//	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor blockID
	 */
//	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
//	{
//		if (!par1World.isRemote)
//		{
//			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
//			{
//				par1World.scheduleBlockUpdate(par2, par3, par4, switchID, 4);
//			}
//			else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
//			{
//				par1World.setBlock(par2, par3, par4, switchID, 0, 2);
//			}
//		}
//	}

	/**
	 * Ticks the block if it's been scheduled
	 */
//	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
//	{
//		if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
//		{
//			par1World.setBlock(par2, par3, par4, switchID, 0, 2);
//		}
//	}

	protected abstract void turnLightsOn(Random r);
}
