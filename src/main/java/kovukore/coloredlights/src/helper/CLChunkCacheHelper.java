package kovukore.coloredlights.src.helper;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;

public class CLChunkCacheHelper {

	public CLChunkCacheHelper() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Any Light rendered on a 1.8 Block goes through here
     * 
     * Modified by CptSpaceToaster
     */
    public static int getLightBrightnessForSkyBlocks(ChunkCache instance, int par1, int par2, int par3, int par4)
    {
        int i1 = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = instance.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

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
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
     * values aren't linear for brightness). Args: x, y, z
     * 
     * Modified by CptSpaceToaster
     * 
     * Not present in 1.7.2... where it go?    - heaton84
    public float getLightBrightness(ChunkCache instance, int par1, int par2, int par3)
    {
        return instance.worldObj.provider.lightBrightnessTable[instance.getLightValue(par1, par2, par3)%15];
    }    
     */
    
}
