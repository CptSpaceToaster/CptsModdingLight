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

public class WhiteColoredLightBlock extends Block
{
	public WhiteColoredLightBlock(int id)
	{
		super(id, Material.glass);
		setUnlocalizedName(BlockInfo.whiteColoredLightBlock_unlocalizedName);
		setHardness(0.3F);
		setStepSound(Block.soundGlassFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		setLightValue(15);
		addColorLightValue(Block.l[15], Block.l[15], Block.l[15]);
	}

	@SideOnly(Side.CLIENT)
	private Icon blockIcon;

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon)
	{
		blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":" + BlockInfo.whiteColoredLightBlock_unlocalizedName);
	}

	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2)
	{
		return blockIcon;
	}
}