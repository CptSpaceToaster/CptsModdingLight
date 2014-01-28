package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;

public class ChunkCache implements IBlockAccess
{
    private int chunkX;
    private int chunkZ;
    private Chunk[][] chunkArray;

    /** True if the chunk cache is empty. */
    private boolean isEmpty;

    /** Reference to the World object. */
    private World worldObj;

    public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7, int par8)
    {
        this.worldObj = par1World;
        this.chunkX = par2 - par8 >> 4;
        this.chunkZ = par4 - par8 >> 4;
        int l1 = par5 + par8 >> 4;
        int i2 = par7 + par8 >> 4;
        this.chunkArray = new Chunk[l1 - this.chunkX + 1][i2 - this.chunkZ + 1];
        this.isEmpty = true;
        int j2;
        int k2;
        Chunk chunk;

        for (j2 = this.chunkX; j2 <= l1; ++j2)
        {
            for (k2 = this.chunkZ; k2 <= i2; ++k2)
            {
                chunk = par1World.getChunkFromChunkCoords(j2, k2);

                if (chunk != null)
                {
                    this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ] = chunk;
                }
            }
        }

        for (j2 = par2 >> 4; j2 <= par5 >> 4; ++j2)
        {
            for (k2 = par4 >> 4; k2 <= par7 >> 4; ++k2)
            {
                chunk = this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ];

                if (chunk != null && !chunk.getAreLevelsEmpty(par3, par6))
                {
                    this.isEmpty = false;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache()
    {
        return this.isEmpty;
    }

    /**
     * Returns the block ID at coords x,y,z
     */
    public int getBlockId(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int l = (par1 >> 4) - this.chunkX;
            int i1 = (par3 >> 4) - this.chunkZ;

            if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
            {
                Chunk chunk = this.chunkArray[l][i1];
                return chunk == null ? 0 : chunk.getBlockID(par1 & 15, par2, par3 & 15);
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Returns the TileEntity associated with a given block in X,Y,Z coordinates, or null if no TileEntity exists
     */
    public TileEntity getBlockTileEntity(int par1, int par2, int par3)
    {
        int l = (par1 >> 4) - this.chunkX;
        int i1 = (par3 >> 4) - this.chunkZ;
        if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
        {
            Chunk chunk = this.chunkArray[l][i1];
            return chunk == null ? null : chunk.getChunkBlockTileEntity(par1 & 15, par2, par3 & 15);
        }
        else
        {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public float getBrightness(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getLightValue(par1, par2, par3);

        if (i1 < par4)
        {
            i1 = par4;
        }

        return this.worldObj.provider.lightBrightnessTable[i1];
    }

    @SideOnly(Side.CLIENT)

    /**
     * Any Light rendered on a 1.8 Block goes through here
     * 
     * Modified by CptSpaceToaster
     */
    public int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

        par4 = ((par4 & 15)			|
           	   ((par4 & 480) >> 1) 	|
          	   ((par4 & 15360) >> 2)|
          	   ((par4 & 491520) >> 3) );
         
         j1 =  ((j1 & 15)			|
        	   ((j1 & 480) >> 1) 	|
           	   ((j1 & 15360) >> 2)	|
           	   ((j1 & 491520) >> 3) );
        
        if (j1 < par4)
        {
            j1 = par4;
        }

        return i1 << 20 | j1 << 4;
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int l = (par1 >> 4) - this.chunkX;
            int i1 = (par3 >> 4) - this.chunkZ;
            if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
            {
                Chunk chunk = this.chunkArray[l][i1];
                return chunk == null ? 0 : chunk.getBlockMetadata(par1 & 15, par2, par3 & 15);
            }
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     * 
     * Modified by CptSpaceToaster
     */
    public float getLightBrightness(int par1, int par2, int par3)
    {
        return this.worldObj.provider.lightBrightnessTable[this.getLightValue(par1, par2, par3)%15];
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the light value of the specified block coords. Args: x, y, z
     */
    public int getLightValue(int par1, int par2, int par3)
    {
        return this.getLightValueExt(par1, par2, par3, true);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Get light value with flag
     * 
     * 
     * Does this need to be modified?
     */
    public int getLightValueExt(int par1, int par2, int par3, boolean par4)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 <= 30000000)
        {
            int l;
            int i1;

            if (par4)
            {
                l = this.getBlockId(par1, par2, par3);

                if (l == Block.stoneSingleSlab.blockID || l == Block.woodSingleSlab.blockID || l == Block.tilledField.blockID || l == Block.stairsWoodOak.blockID || l == Block.stairsCobblestone.blockID)
                {
                    i1 = this.getLightValueExt(par1, par2 + 1, par3, false);
                    int j1 = this.getLightValueExt(par1 + 1, par2, par3, false);
                    int k1 = this.getLightValueExt(par1 - 1, par2, par3, false);
                    int l1 = this.getLightValueExt(par1, par2, par3 + 1, false);
                    int i2 = this.getLightValueExt(par1, par2, par3 - 1, false);

                    if (j1 > i1)
                    {
                        i1 = j1;
                    }

                    if (k1 > i1)
                    {
                        i1 = k1;
                    }

                    if (l1 > i1)
                    {
                        i1 = l1;
                    }

                    if (i2 > i1)
                    {
                        i1 = i2;
                    }

                    return i1;
                }
            }

            if (par2 < 0)
            {
                return 0;
            }
            else if (par2 >= 256)
            {
                l = 15 - this.worldObj.skylightSubtracted;

                if (l < 0)
                {
                    l = 0;
                }

                return l;
            }
            else
            {
                l = (par1 >> 4) - this.chunkX;
                i1 = (par3 >> 4) - this.chunkZ;
                return this.chunkArray[l][i1].getBlockLightValue(par1 & 15, par2, par3 & 15, this.worldObj.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the block's material.
     */
    public Material getBlockMaterial(int par1, int par2, int par3)
    {
        int l = this.getBlockId(par1, par2, par3);
        return l == 0 ? Material.air : Block.blocksList[l].blockMaterial;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    public BiomeGenBase getBiomeGenForCoords(int par1, int par2)
    {
        return this.worldObj.getBiomeGenForCoords(par1, par2);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the block at the specified coordinates is an opaque cube. Args: x, y, z
     */
    public boolean isBlockOpaqueCube(int par1, int par2, int par3)
    {
        Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return block == null ? false : block.isOpaqueCube();
    }

    /**
     * Indicate if a material is a normal solid opaque cube.
     */
    public boolean isBlockNormalCube(int par1, int par2, int par3)
    {
        Block block = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return block == null ? false : block.blockMaterial.blocksMovement() && block.renderAsNormalBlock();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the block at the given coordinate has a solid (buildable) top surface.
     */
    public boolean doesBlockHaveSolidTopSurface(int par1, int par2, int par3)
    {
        return this.worldObj.doesBlockHaveSolidTopSurface(par1, par2, par3);
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3Pool getWorldVec3Pool()
    {
        return this.worldObj.getWorldVec3Pool();
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(int par1, int par2, int par3)
    {
        int id = getBlockId(par1, par2, par3);
        return id == 0 || Block.blocksList[id] == null || Block.blocksList[id].isAirBlock(this.worldObj, par1, par2, par3);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Brightness for SkyBlock.Sky is clear white and (through color computing it is assumed) DEPENDENT ON DAYTIME.
     * Brightness for SkyBlock.Block is yellowish and independent.
     */
    public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            if (par1EnumSkyBlock == EnumSkyBlock.Sky && this.worldObj.provider.hasNoSky)
            {
                return 0;
            }
            else
            {
                int l;
                int i1;

                if (Block.useNeighborBrightness[this.getBlockId(par2, par3, par4)])
                {
                    l = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3 + 1, par4);
                    i1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 + 1, par3, par4);
                    int j1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 - 1, par3, par4);
                    int k1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 + 1);
                    int l1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 - 1);

                    if (i1 > l)
                    {
                        l = i1;
                    }

                    if (j1 > l)
                    {
                        l = j1;
                    }

                    if (k1 > l)
                    {
                        l = k1;
                    }

                    if (l1 > l)
                    {
                        l = l1;
                    }

                    return l;
                }
                else
                {
                    l = (par2 >> 4) - this.chunkX;
                    i1 = (par4 >> 4) - this.chunkZ;
                    return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
                }
            }
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * is only used on stairs and tilled fields
     */
    public int getSpecialBlockBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            int l = (par2 >> 4) - this.chunkX;
            int i1 = (par4 >> 4) - this.chunkZ;
            return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns current world height.
     */
    public int getHeight()
    {
        return 256;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getBlockId(par1, par2, par3);
        return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingStrongPower(this, par1, par2, par3, par4);
    }

    public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
        {
            return _default;
        }

        int blockId = getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];

        if (block != null)
        {
            return block.isBlockSolidOnSide(this.worldObj, x, y, z, side);
        }

        return false;
    }
}
