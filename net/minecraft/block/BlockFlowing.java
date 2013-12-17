package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowing extends BlockFluid
{
    /**
     * Number of horizontally adjacent liquid source blocks. Diagonal doesn't count. Only source blocks of the same
     * liquid as the block using the field are counted.
     */
    int numAdjacentSources;

    /**
     * Indicates whether the flow direction is optimal. Each array index corresponds to one of the four cardinal
     * directions.
     */
    boolean[] isOptimalFlowDirection = new boolean[4];

    /**
     * The estimated cost to flow in a given direction from the current point. Each array index corresponds to one of
     * the four cardinal directions.
     */
    int[] flowCost = new int[4];

    public BlockFlowing(int par1, Material par2Material)
    {
        super(par1, par2Material);
    }

    /**
     * Updates the flow for the BlockFlowing object.
     */
    private void updateFlow(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlock(par2, par3, par4, this.blockID + 1, l, 2);
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int l = this.getFlowDecay(par1World, par2, par3, par4);
        byte b0 = 1;

        if (this.blockMaterial == Material.lava && !par1World.provider.isHellWorld)
        {
            b0 = 2;
        }

        boolean flag = true;
        int i1 = this.tickRate(par1World);
        int j1;

        if (l > 0)
        {
            byte b1 = -100;
            this.numAdjacentSources = 0;
            int k1 = this.getSmallestFlowDecay(par1World, par2 - 1, par3, par4, b1);
            k1 = this.getSmallestFlowDecay(par1World, par2 + 1, par3, par4, k1);
            k1 = this.getSmallestFlowDecay(par1World, par2, par3, par4 - 1, k1);
            k1 = this.getSmallestFlowDecay(par1World, par2, par3, par4 + 1, k1);
            j1 = k1 + b0;

            if (j1 >= 8 || k1 < 0)
            {
                j1 = -1;
            }

            if (this.getFlowDecay(par1World, par2, par3 + 1, par4) >= 0)
            {
                int l1 = this.getFlowDecay(par1World, par2, par3 + 1, par4);

                if (l1 >= 8)
                {
                    j1 = l1;
                }
                else
                {
                    j1 = l1 + 8;
                }
            }

            if (this.numAdjacentSources >= 2 && this.blockMaterial == Material.water)
            {
                if (par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid())
                {
                    j1 = 0;
                }
                else if (par1World.getBlockMaterial(par2, par3 - 1, par4) == this.blockMaterial && par1World.getBlockMetadata(par2, par3 - 1, par4) == 0)
                {
                    j1 = 0;
                }
            }

            if (this.blockMaterial == Material.lava && l < 8 && j1 < 8 && j1 > l && par5Random.nextInt(4) != 0)
            {
                i1 *= 4;
            }

            if (j1 == l)
            {
                if (flag)
                {
                    this.updateFlow(par1World, par2, par3, par4);
                }
            }
            else
            {
                l = j1;

                if (j1 < 0)
                {
                    par1World.setBlockToAir(par2, par3, par4);
                }
                else
                {
                    par1World.setBlockMetadataWithNotify(par2, par3, par4, j1, 2);
                    par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, i1);
                    par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
                }
            }
        }
        else
        {
            this.updateFlow(par1World, par2, par3, par4);
        }

        if (this.liquidCanDisplaceBlock(par1World, par2, par3 - 1, par4))
        {
            if (this.blockMaterial == Material.lava && par1World.getBlockMaterial(par2, par3 - 1, par4) == Material.water)
            {
                par1World.setBlock(par2, par3 - 1, par4, Block.stone.blockID);
                this.triggerLavaMixEffects(par1World, par2, par3 - 1, par4);
                return;
            }

            if (l >= 8)
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, l);
            }
            else
            {
                this.flowIntoBlock(par1World, par2, par3 - 1, par4, l + 8);
            }
        }
        else if (l >= 0 && (l == 0 || this.blockBlocksFlow(par1World, par2, par3 - 1, par4)))
        {
            boolean[] aboolean = this.getOptimalFlowDirections(par1World, par2, par3, par4);
            j1 = l + b0;

            if (l >= 8)
            {
                j1 = 1;
            }

            if (j1 >= 8)
            {
                return;
            }

            if (aboolean[0])
            {
                this.flowIntoBlock(par1World, par2 - 1, par3, par4, j1);
            }

            if (aboolean[1])
            {
                this.flowIntoBlock(par1World, par2 + 1, par3, par4, j1);
            }

            if (aboolean[2])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 - 1, j1);
            }

            if (aboolean[3])
            {
                this.flowIntoBlock(par1World, par2, par3, par4 + 1, j1);
            }
        }
    }

    /**
     * flowIntoBlock(World world, int x, int y, int z, int newFlowDecay) - Flows into the block at the coordinates and
     * changes the block type to the liquid.
     */
    private void flowIntoBlock(World par1World, int par2, int par3, int par4, int par5)
    {
        if (this.liquidCanDisplaceBlock(par1World, par2, par3, par4))
        {
            int i1 = par1World.getBlockId(par2, par3, par4);

            if (i1 > 0)
            {
                if (this.blockMaterial == Material.lava)
                {
                    this.triggerLavaMixEffects(par1World, par2, par3, par4);
                }
                else if (i1 != Block.snow.blockID)//Forge: Damn snow.
                {
                    Block.blocksList[i1].dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
                }
            }

            par1World.setBlock(par2, par3, par4, this.blockID, par5, 3);
        }
    }

    /**
     * calculateFlowCost(World world, int x, int y, int z, int accumulatedCost, int previousDirectionOfFlow) - Used to
     * determine the path of least resistance, this method returns the lowest possible flow cost for the direction of
     * flow indicated. Each necessary horizontal flow adds to the flow cost.
     */
    private int calculateFlowCost(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        int j1 = 1000;

        for (int k1 = 0; k1 < 4; ++k1)
        {
            if ((k1 != 0 || par6 != 1) && (k1 != 1 || par6 != 0) && (k1 != 2 || par6 != 3) && (k1 != 3 || par6 != 2))
            {
                int l1 = par2;
                int i2 = par4;

                if (k1 == 0)
                {
                    l1 = par2 - 1;
                }

                if (k1 == 1)
                {
                    ++l1;
                }

                if (k1 == 2)
                {
                    i2 = par4 - 1;
                }

                if (k1 == 3)
                {
                    ++i2;
                }

                if (!this.blockBlocksFlow(par1World, l1, par3, i2) && (par1World.getBlockMaterial(l1, par3, i2) != this.blockMaterial || par1World.getBlockMetadata(l1, par3, i2) != 0))
                {
                    if (!this.blockBlocksFlow(par1World, l1, par3 - 1, i2))
                    {
                        return par5;
                    }

                    if (par5 < 4)
                    {
                        int j2 = this.calculateFlowCost(par1World, l1, par3, i2, par5 + 1, k1);

                        if (j2 < j1)
                        {
                            j1 = j2;
                        }
                    }
                }
            }
        }

        return j1;
    }

    /**
     * Returns a boolean array indicating which flow directions are optimal based on each direction's calculated flow
     * cost. Each array index corresponds to one of the four cardinal directions. A value of true indicates the
     * direction is optimal.
     */
    private boolean[] getOptimalFlowDirections(World par1World, int par2, int par3, int par4)
    {
        int l;
        int i1;

        for (l = 0; l < 4; ++l)
        {
            this.flowCost[l] = 1000;
            i1 = par2;
            int j1 = par4;

            if (l == 0)
            {
                i1 = par2 - 1;
            }

            if (l == 1)
            {
                ++i1;
            }

            if (l == 2)
            {
                j1 = par4 - 1;
            }

            if (l == 3)
            {
                ++j1;
            }

            if (!this.blockBlocksFlow(par1World, i1, par3, j1) && (par1World.getBlockMaterial(i1, par3, j1) != this.blockMaterial || par1World.getBlockMetadata(i1, par3, j1) != 0))
            {
                if (this.blockBlocksFlow(par1World, i1, par3 - 1, j1))
                {
                    this.flowCost[l] = this.calculateFlowCost(par1World, i1, par3, j1, 1, l);
                }
                else
                {
                    this.flowCost[l] = 0;
                }
            }
        }

        l = this.flowCost[0];

        for (i1 = 1; i1 < 4; ++i1)
        {
            if (this.flowCost[i1] < l)
            {
                l = this.flowCost[i1];
            }
        }

        for (i1 = 0; i1 < 4; ++i1)
        {
            this.isOptimalFlowDirection[i1] = this.flowCost[i1] == l;
        }

        return this.isOptimalFlowDirection;
    }

    /**
     * Returns true if block at coords blocks fluids
     */
    private boolean blockBlocksFlow(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockId(par2, par3, par4);

        if (l != Block.doorWood.blockID && l != Block.doorIron.blockID && l != Block.signPost.blockID && l != Block.ladder.blockID && l != Block.reed.blockID)
        {
            if (l == 0)
            {
                return false;
            }
            else
            {
                Material material = Block.blocksList[l].blockMaterial;
                return material == Material.portal ? true : material.blocksMovement();
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * getSmallestFlowDecay(World world, intx, int y, int z, int currentSmallestFlowDecay) - Looks up the flow decay at
     * the coordinates given and returns the smaller of this value or the provided currentSmallestFlowDecay. If one
     * value is valid and the other isn't, the valid value will be returned. Valid values are >= 0. Flow decay is the
     * amount that a liquid has dissipated. 0 indicates a source block.
     */
    protected int getSmallestFlowDecay(World par1World, int par2, int par3, int par4, int par5)
    {
        int i1 = this.getFlowDecay(par1World, par2, par3, par4);

        if (i1 < 0)
        {
            return par5;
        }
        else
        {
            if (i1 == 0)
            {
                ++this.numAdjacentSources;
            }

            if (i1 >= 8)
            {
                i1 = 0;
            }

            return par5 >= 0 && i1 >= par5 ? par5 : i1;
        }
    }

    /**
     * Returns true if the block at the coordinates can be displaced by the liquid.
     */
    private boolean liquidCanDisplaceBlock(World par1World, int par2, int par3, int par4)
    {
        Material material = par1World.getBlockMaterial(par2, par3, par4);
        return material == this.blockMaterial ? false : (material == Material.lava ? false : !this.blockBlocksFlow(par1World, par2, par3, par4));
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (par1World.getBlockId(par2, par3, par4) == this.blockID)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
        }
    }

    public boolean func_82506_l()
    {
        return true;
    }
}
