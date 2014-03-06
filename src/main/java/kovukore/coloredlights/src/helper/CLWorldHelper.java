package kovukore.coloredlights.src.helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CLWorldHelper {
	private long[] lightUpdateBlockList = new long[32768];; //world sort of variable addition should be fine... but I'm not 100% sure yet
	
	
	//Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
	//Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
	public static int getBlockLightValue_do(World world, int x, int y, int z, boolean par4)
    {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (par4 && world.getBlock(x, y, z).getUseNeighborBrightness())
            {
                int l1 = world.getBlockLightValue_do(x, y + 1, z, false);
                int l = world.getBlockLightValue_do(x + 1, y, z, false);
                int i1 = world.getBlockLightValue_do(x - 1, y, z, false);
                int j1 = world.getBlockLightValue_do(x, y, z + 1, false);
                int k1 = world.getBlockLightValue_do(x, y, z - 1, false);

                if ((l&15) > (l1&15))
                {
                    l1 = l;
                }

                if ((i1&15) > (l1&15))
                {
                    l1 = i1;
                }

                if ((j1&15) > (l1&15))
                {
                    l1 = j1;
                }

                if ((k1&15) > (l1&15))
                {
                    l1 = k1;
                }

                return l1;
            }
            else if (y < 0)
            {
                return 0;
            }
            else
            {
                if (y >= 256)
                {
                    y = 255;
                }

                Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                x &= 15;
                z &= 15;
                return chunk.getBlockLightValue(x, y, z, world.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }
	
	
	//Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
	//Refactored variable names to match the method from the 1.6.4 source place cursor over variable and (Alt + Shift + r)
	//Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
	@SideOnly(Side.CLIENT)
    public static int getLightBrightnessForSkyBlocks(World world, int x, int y, int z, int lightValue)
    {
        int i1 = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int j1 = world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        lightValue = ((lightValue & 15)	|
   			 ((lightValue & 480) >> 1) 	|
   			 ((lightValue & 15360) >> 2)|
   			 ((lightValue & 491520) >> 3) );
   
	    j1 =   ((j1 & 15)			|
	      	   ((j1 & 480) >> 1) 	|
	     	   ((j1 & 15360) >> 2)	|
	     	   ((j1 & 491520) >> 3) );
        
        if (j1 < lightValue)
        {
            j1 = lightValue;
        }

        return i1 << 20 | j1 << 4;
    }
	
	
	//getBrightness(x,y,z) appears to be missing... not sure what's up there
	
	
	//Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
	//Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
	public static float getLightBrightness(World world, int par1, int par2, int par3)
    {
        return world.provider.lightBrightnessTable[world.getBlockLightValue(par1, par2, par3)&15];
    }
	
	
	
	//Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC, made it PUBLIC
	//Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
	public static int computeLightValue(World world, int x, int y, int z, EnumSkyBlock par4EnumSkyBlock)
    {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky && world.canBlockSeeTheSky(x, y, z))
        {
            return 15;
        }
        else
        {
            Block block = world.getBlock(x, y, z);
            int blockLight = (block == null ? 0 : block.getLightValue(world, x, y, z));
            int currentLight = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
            int opacity = (block == null ? 0 : block.getLightOpacity(world, x, y, z));

            if (opacity >= 15 && blockLight > 0)
            {
                opacity = 1;
            }

            if (opacity < 1)
            {
                opacity = 1;
            }

            if (opacity >= 15)
            {
                return 0;
            }
//            else if ((currentLight&15) >= 14) {
//            	return currentLight;
//            }	
            else
            {
                for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
                {
                    int l1 = x + Facing.offsetsXForSide[faceIndex];
                    int i2 = y + Facing.offsetsYForSide[faceIndex];
                    int j2 = z + Facing.offsetsZForSide[faceIndex];
                    
                    int neighboorLight = world.getSavedLightValue(par4EnumSkyBlock, l1, i2, j2);
                    int ll = neighboorLight&15;
                    int rl = neighboorLight&480;
                    int gl = neighboorLight&15360;
                    int bl = neighboorLight&491520;
                    
                	ll-=opacity;
                	rl-=32*opacity;
                    gl-=1024*opacity;
                    bl-=32768*opacity;
                    
                    if (ll > (currentLight&15)) {	
                    	currentLight = (currentLight&507360) | ll;
                    } 
                    if (rl > (currentLight&480)) {
                        currentLight = (currentLight&506895) | rl;
                    }
                    if (gl > (currentLight&15360)) {
                    	currentLight = (currentLight&492015) | gl;
	                }
                    if (bl > (currentLight&491520)) {
                        currentLight = (currentLight&15855) | bl;
                    }
                }

                return currentLight;
            }
        }
    }
	
	
	
	//Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
	//Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
    public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int x, int y, int z)
    {
    	// This method is different than 1.6.4
    	// Once it is updated, uncomment the last string in TransformWorld.methodsToReplace
    	
    	return false;
    }
	
}
