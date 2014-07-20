package yamhaven.easycoloredlights.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import yamhaven.easycoloredlights.lib.BlockInfo;
import yamhaven.easycoloredlights.lib.ModInfo;
import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.api.CLBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CLLamp extends CLBlock {
    /** Whether this lamp block is the powered version of the block. */
    protected final boolean powered;
    /** The Block the lamp is supposed to switch to **/
    protected Block switchBlock = null;

    public CLLamp(boolean isPowered) {
        super(Material.redstoneLight);
        this.powered = isPowered;

        setHardness(0.3F);
        setStepSound(soundTypeGlass);

        if (isPowered)
            setLightLevel(1.0F);
        else
            setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void setSwitchBlock(Block switchBlock) {
        this.switchBlock = switchBlock;
    }

    @SideOnly(Side.CLIENT)
    private IIcon icons[];

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) { //registerIcons()
        icons = new IIcon[16];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon(ModInfo.ID + ":" + BlockInfo.CLLamp + (powered ? "On" : "") + i);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock((powered) ? switchBlock : this);
    }

    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return Item.getItemFromBlock((powered) ? switchBlock : this);
    }

    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack((powered) ? switchBlock : this);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isRemote) {
            if (this.powered && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, this, 4);
            } else if (!this.powered && world.isBlockIndirectlyGettingPowered(x, y, z)) {
                int temp = world.getBlockMetadata(x, y, z);
                world.setBlock(x, y, z, switchBlock, 0, 0);
                world.setBlockMetadataWithNotify(x, y, z, temp, 2);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            if (this.powered && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, this, 4);
            } else if (!this.powered && world.isBlockIndirectlyGettingPowered(x, y, z)) {
                int temp = world.getBlockMetadata(x, y, z);
                world.setBlock(x, y, z, switchBlock, 0, 0);
                world.setBlockMetadataWithNotify(x, y, z, temp, 2);
            }
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote && this.powered && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
            int temp = world.getBlockMetadata(x, y, z);
            world.setBlock(x, y, z, switchBlock, 0, 0);
            world.setBlockMetadataWithNotify(x, y, z, temp, 2);
        }
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

    @Override
    public int getColorLightValue(int meta) {
        //System.out.println("Metadata: " + meta);
        //System.out.println(Integer.toBinaryString(CLApi.makeColorLightValue(CLApi.r[meta], CLApi.g[meta], CLApi.b[meta])) + System.lineSeparator());
        if (powered) {
            if (meta == 0) {
                //Temporary
                return CLApi.makeRGBLightValue(15, 15, 15);
            } else {
                return CLApi.makeRGBLightValue(CLApi.r[meta], CLApi.g[meta], CLApi.b[meta]);
            }
        } else {
            return 0;
        }
    }
}
