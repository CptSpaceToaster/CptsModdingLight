package yamhaven.easycoloredlights.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import yamhaven.easycoloredlights.CLMaterialsController;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.api.CLBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CLStone extends CLBlock {
    public CLStone() {
        super(Material.glass);
        setHardness(0.3F);
        setStepSound(soundTypeGlass);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @SideOnly(Side.CLIENT)
    private IIcon icons[];

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLStone + i);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < 16; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int p_149679_1_, Random p_149679_2_)
    {
        return MathHelper.clamp_int(this.quantityDropped(p_149679_2_) + p_149679_2_.nextInt(p_149679_1_ + 1), 1, 4);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 2 + p_149745_1_.nextInt(3);
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return CLMaterialsController.CLDust;
    }

    @Override
    public int getColorLightValue(int meta) {
        if (meta == 0) {
            //Temporary
            return CLApi.makeRGBLightValue(15, 15, 15);
        } else {
            return CLApi.makeRGBLightValue(CLApi.r[meta], CLApi.g[meta], CLApi.b[meta]);
        }
    }
}
