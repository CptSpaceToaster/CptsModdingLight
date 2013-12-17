package kovukore.asm.overriddenclasses;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeDirection;

public class Lights_ChunkCache {
	//Does this need to implement IBlockAccess just like ChunkCache does?
	
	
	
	private int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4) {
		return 0;
	}
	
	@ASMReplaceMethod
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

	
}
