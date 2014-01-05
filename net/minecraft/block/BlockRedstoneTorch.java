package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch
{
    /** Whether the redstone torch is currently active or not. */
    private boolean torchActive;

    /** Map of ArrayLists of RedstoneUpdateInfo. Key of map is World. */
    private static Map redstoneUpdateInfoCache = new HashMap();

    private boolean checkForBurnout(World par1World, int par2, int par3, int par4, boolean par5)
    {
        if (!redstoneUpdateInfoCache.containsKey(par1World))
        {
            redstoneUpdateInfoCache.put(par1World, new ArrayList());
        }

        List list = (List)redstoneUpdateInfoCache.get(par1World);

        if (par5)
        {
            list.add(new RedstoneUpdateInfo(par2, par3, par4, par1World.getTotalWorldTime()));
        }

        int l = 0;

        for (int i1 = 0; i1 < list.size(); ++i1)
        {
            RedstoneUpdateInfo redstoneupdateinfo = (RedstoneUpdateInfo)list.get(i1);

            if (redstoneupdateinfo.x == par2 && redstoneupdateinfo.y == par3 && redstoneupdateinfo.z == par4)
            {
                ++l;

                if (l >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockRedstoneTorch(int par1, boolean par2)
    {
        super(par1);
        this.torchActive = par2;
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World par1World)
    {
        return 2;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (par1World.getBlockMetadata(par2, par3, par4) == 0)
        {
            super.onBlockAdded(par1World, par2, par3, par4);
        }

        if (this.torchActive)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (this.torchActive)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        }
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        if (!this.torchActive)
        {
            return 0;
        }
        else
        {
            int i1 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
            return i1 == 5 && par5 == 1 ? 0 : (i1 == 3 && par5 == 3 ? 0 : (i1 == 4 && par5 == 2 ? 0 : (i1 == 1 && par5 == 5 ? 0 : (i1 == 2 && par5 == 4 ? 0 : 15))));
        }
    }

    /**
     * Returns true or false based on whether the block the torch is attached to is providing indirect power.
     */
    private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        return l == 5 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true : (l == 3 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true : (l == 4 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true : (l == 1 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true : l == 2 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5))));
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        boolean flag = this.isIndirectlyPowered(par1World, par2, par3, par4);
        List list = (List)redstoneUpdateInfoCache.get(par1World);

        while (list != null && !list.isEmpty() && par1World.getTotalWorldTime() - ((RedstoneUpdateInfo)list.get(0)).updateTime > 60L)
        {
            list.remove(0);
        }

        if (this.torchActive)
        {
            if (flag)
            {
                par1World.setBlock(par2, par3, par4, Block.torchRedstoneIdle.blockID, par1World.getBlockMetadata(par2, par3, par4), 3);

                if (this.checkForBurnout(par1World, par2, par3, par4, true))
                {
                    par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                    for (int l = 0; l < 5; ++l)
                    {
                        double d0 = (double)par2 + par5Random.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double)par3 + par5Random.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double)par4 + par5Random.nextDouble() * 0.6D + 0.2D;
                        par1World.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
        else if (!flag && !this.checkForBurnout(par1World, par2, par3, par4, false))
        {
            par1World.setBlock(par2, par3, par4, Block.torchRedstoneActive.blockID, par1World.getBlockMetadata(par2, par3, par4), 3);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!this.func_94397_d(par1World, par2, par3, par4, par5))
        {
            boolean flag = this.isIndirectlyPowered(par1World, par2, par3, par4);

            if (this.torchActive && flag || !this.torchActive && !flag)
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
            }
        }
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 0 ? this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5) : 0;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Block.torchRedstoneActive.blockID;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (this.torchActive)
        {
            int l = par1World.getBlockMetadata(par2, par3, par4);
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            if (l == 1)
            {
                par1World.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 2)
            {
                par1World.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 3)
            {
                par1World.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            }
            else if (l == 4)
            {
                par1World.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            }
            else
            {
                par1World.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Block.torchRedstoneActive.blockID;
    }

    /**
     * Returns true if the given block ID is equivalent to this one. Example: redstoneTorchOn matches itself and
     * redstoneTorchOff, and vice versa. Most blocks only match themselves.
     */
    public boolean isAssociatedBlockID(int par1)
    {
        return par1 == Block.torchRedstoneIdle.blockID || par1 == Block.torchRedstoneActive.blockID;
    }
}
