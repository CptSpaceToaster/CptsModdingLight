package yamhaven.easycoloredlights.items;

import java.util.List;

import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class CLDust extends Item {
    public CLDust() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        setCreativeTab(CreativeTabs.tabMaterials);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon icons[];

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + itemstack.getItemDamage();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons = new IIcon[16];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLDust + i);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        return icons[damage];
    }
    
    @Override
    public int getMetadata(int meta) {
        return meta;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < 16; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }
}
