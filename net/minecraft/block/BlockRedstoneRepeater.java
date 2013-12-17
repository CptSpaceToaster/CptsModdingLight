package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneLogic
{
    /** The offsets for the two torches in redstone repeater blocks. */
    public static final double[] repeaterTorchOffset = new double[] { -0.0625D, 0.0625D, 0.1875D, 0.3125D};

    /** The states in which the redstone repeater blocks can be. */
    private static final int[] repeaterState = new int[] {1, 2, 3, 4};

    public BlockRedstoneRepeater(int par1, boolean par2)
    {
        super(par1, par2);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        int i1 = par1World.getBlockMetadata(par2, par3, par4);
        int j1 = (i1 & 12) >> 2;
        j1 = j1 + 1 << 2 & 12;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, j1 | i1 & 3, 3);
        return true;
    }

    protected int func_94481_j_(int par1)
    {
        return repeaterState[(par1 & 12) >> 2] * 2;
    }

    protected BlockRedstoneLogic func_94485_e()
    {
        return Block.redstoneRepeaterActive;
    }

    protected BlockRedstoneLogic func_94484_i()
    {
        return Block.redstoneRepeaterIdle;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Item.redstoneRepeater.itemID;
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Item.redstoneRepeater.itemID;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 15;
    }

    public boolean func_94476_e(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.func_94482_f(par1IBlockAccess, par2, par3, par4, par5) > 0;
    }

    protected boolean func_94477_d(int par1)
    {
        return isRedstoneRepeaterBlockID(par1);
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.isRepeaterPowered)
        {
            int l = par1World.getBlockMetadata(par2, par3, par4);
            int i1 = getDirection(l);
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.4F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.0D;
            double d4 = 0.0D;

            if (par5Random.nextInt(2) == 0)
            {
                switch (i1)
                {
                    case 0:
                        d4 = -0.3125D;
                        break;
                    case 1:
                        d3 = 0.3125D;
                        break;
                    case 2:
                        d4 = 0.3125D;
                        break;
                    case 3:
                        d3 = -0.3125D;
                }
            }
            else
            {
                int j1 = (l & 12) >> 2;

                switch (i1)
                {
                    case 0:
                        d4 = repeaterTorchOffset[j1];
                        break;
                    case 1:
                        d3 = -repeaterTorchOffset[j1];
                        break;
                    case 2:
                        d4 = -repeaterTorchOffset[j1];
                        break;
                    case 3:
                        d3 = repeaterTorchOffset[j1];
                }
            }

            par1World.spawnParticle("reddust", d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        this.func_94483_i_(par1World, par2, par3, par4);
    }
}
