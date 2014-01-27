package yamhaven.easycoloredlights.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
  
public class RaveLightBlock extends BlockColoredLight {
	public RaveLightBlock(int id, boolean isPowered) {
		super(id, isPowered);
		setUnlocalizedName(BlockInfo.raveLightBlock_unlocalizedName);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon) {
		blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":" + BlockInfo.whiteColoredLightBlock_unlocalizedName + (powered?"On":""));
	}
	
	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
		{
			
			((BlockColoredLight)Blocks.raveLightBlockOn).turnLightsOn(new Random ());
			par1World.setBlock(par2, par3, par4, this.blockID + 16, 0, 2);
		}
	}
	
	@Override
	protected void turnLightsOn(Random r) {
		setLightValue(1.0F);
		try {
			switch (r.nextInt(3)) {
				case 0: addColorLightValue(1.0F, r.nextFloat(), r.nextFloat()); break;
				case 1: addColorLightValue(r.nextFloat(), 1.0F, r.nextFloat()); break;
				case 2:	addColorLightValue(r.nextFloat(), r.nextFloat(), 1.0F); break;
			}
		} catch (Throwable e) {
			System.out.println("The Colored Light Core appears to be missing, or broken"); 
		}
	}
}
