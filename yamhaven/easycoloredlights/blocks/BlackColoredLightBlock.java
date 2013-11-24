package yamhaven.easycoloredlights.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
  
public class BlackColoredLightBlock extends Block {
	public BlackColoredLightBlock(int id) {
		super(id, Material.glass);
		setUnlocalizedName(BlockInfo.blackColoredLightBlock_unlocalizedName);
		setHardness(0.3F);
		setStepSound(Block.soundGlassFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		setLightValue(1.0F);
		
		//Accepts RGB floats ranging from 0.0 to 1.0
		addColorLightValue(0F,0F,0F);
	}
	
	@SideOnly(Side.CLIENT)
	private Icon blockIcon;
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon) {
		blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":" + BlockInfo.blackColoredLightBlock_unlocalizedName);
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return blockIcon;
	}
}
