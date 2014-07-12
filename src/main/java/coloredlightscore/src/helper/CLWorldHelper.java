package coloredlightscore.src.helper;

import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CLWorldHelper {

    public static long[] lightUpdateBlockList = new long[32768];

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
    //Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
    public static int getBlockLightValue_do(World world, int x, int y, int z, boolean par4) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (par4 && world.getBlock(x, y, z).getUseNeighborBrightness()) {
                // heaton84 - should be world.getBlockLightValue_do,
                // switched to CLWorldHelper.getBlockLightValue_do
                // This will save an extra invoke
                int l1 = CLWorldHelper.getBlockLightValue_do(world, x, y + 1, z, false);
                int l = CLWorldHelper.getBlockLightValue_do(world, x + 1, y, z, false);
                int i1 = CLWorldHelper.getBlockLightValue_do(world, x - 1, y, z, false);
                int j1 = CLWorldHelper.getBlockLightValue_do(world, x, y, z + 1, false);
                int k1 = CLWorldHelper.getBlockLightValue_do(world, x, y, z - 1, false);

                if ((l & 15) > (l1 & 15)) {
                    l1 = l;
                }

                if ((i1 & 15) > (l1 & 15)) {
                    l1 = i1;
                }

                if ((j1 & 15) > (l1 & 15)) {
                    l1 = j1;
                }

                if ((k1 & 15) > (l1 & 15)) {
                    l1 = k1;
                }

                return l1;
            } else if (y < 0) {
                return 0;
            } else {
                if (y >= 256) {
                    y = 255;
                }

                //int cx = x >> 4;
                //int cz = z >> 4;
                Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
                x &= 15;
                z &= 15;

                //FMLLog.info("NEWTEST %s,%s:%s", cx, cz, Integer.toBinaryString(chunk.getBlockLightValue(0, 0, 0, 15)));

                return chunk.getBlockLightValue(x, y, z, world.skylightSubtracted);
            }
        } else {
            return 15;
        }
    }

    //Use this one if you want color
    @SideOnly(Side.CLIENT)
    public static int getLightBrightnessForSkyBlocks(World world, int x, int y, int z, int lightValue) {
        int skyBrightness = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int blockBrightness = world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        lightValue = ((lightValue & 15) | ((lightValue & 480) >> 1) | ((lightValue & 15360) >> 2) | ((lightValue & 491520) >> 3));

        blockBrightness = ((blockBrightness & 15) | ((blockBrightness & 480) >> 1) | ((blockBrightness & 15360) >> 2) | ((blockBrightness & 491520) >> 3));

        if (blockBrightness < lightValue) {
            blockBrightness = lightValue;
        }
        return skyBrightness << 20 | blockBrightness << 4;
    }

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
    //Refactored variable names to match the method from the 1.6.4 source place cursor over variable and (Alt + Shift + r)
    //Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
    /*
    //Use this one if you want color
    @SideOnly(Side.CLIENT)
    public static int getLightBrightnessForSkyBlocksWithColor(World world, int x, int y, int z, int lightValue)
    {
        int skyBrightness = world.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int blockBrightness = world.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

        lightValue = ((lightValue & 15)	|
    		 ((lightValue & 480) >> 1) 	|
    		 ((lightValue & 15360) >> 2)|
    		 ((lightValue & 491520) >> 3) );
    
        blockBrightness =   ((blockBrightness & 15)			|
          	   ((blockBrightness & 480) >> 1) 	|
         	   ((blockBrightness & 15360) >> 2)	|
         	   ((blockBrightness & 491520) >> 3) );
        
        if (blockBrightness < lightValue)
        {
        	blockBrightness = lightValue;
        }
        return skyBrightness << 20 | blockBrightness << 4;
    }
    */

    //getBrightness(x,y,z) appears to be missing... not sure what's up there

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
    //Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
    public static float getLightBrightness(World world, int par1, int par2, int par3) {
        return world.provider.lightBrightnessTable[world.getBlockLightValue(par1, par2, par3) & 15];
    }

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC, made it PUBLIC
    //Added the parameter 'World world, ' and then replaces all instances of 'this', with world
    public static int computeLightValue(World world, int x, int y, int z, EnumSkyBlock par4EnumSkyBlock) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky && world.canBlockSeeTheSky(x, y, z)) {
            return 15;
        } else {
            Block block = world.getBlock(x, y, z);
            int blockLight = (block == null ? 0 : block.getLightValue(world, x, y, z));
            int currentLight = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
            int opacity = (block == null ? 0 : block.getLightOpacity(world, x, y, z));

            if (opacity >= 15 && blockLight > 0) {
                opacity = 1;
            }

            if (opacity < 1) {
                opacity = 1;
            }

            if (opacity >= 15) {
                return 0;
            }
            //            else if ((currentLight&15) >= 14) {
            //            	return currentLight;
            //            }
            else {
                for (int faceIndex = 0; faceIndex < 6; ++faceIndex) {
                    int l1 = x + Facing.offsetsXForSide[faceIndex];
                    int i2 = y + Facing.offsetsYForSide[faceIndex];
                    int j2 = z + Facing.offsetsZForSide[faceIndex];

                    int neighboorLight = world.getSavedLightValue(par4EnumSkyBlock, l1, i2, j2);
                    int ll = neighboorLight & 15;
                    int rl = neighboorLight & 480;
                    int gl = neighboorLight & 15360;
                    int bl = neighboorLight & 491520;

                    ll -= opacity;
                    rl -= 32 * opacity;
                    gl -= 1024 * opacity;
                    bl -= 32768 * opacity;

                    if (ll > (currentLight & 15)) {
                        currentLight = (currentLight & 507360) | ll;
                    }
                    if (rl > (currentLight & 480)) {
                        currentLight = (currentLight & 506895) | rl;
                    }
                    if (gl > (currentLight & 15360)) {
                        currentLight = (currentLight & 492015) | gl;
                    }
                    if (bl > (currentLight & 491520)) {
                        currentLight = (currentLight & 15855) | bl;
                    }
                }

                return currentLight;
            }
        }
    }

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC
    //Added the parameter 'World world, ' and then replaces all instances of world, with WORLD
    /*
    public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int x, int y, int z)
    {
    	// This method is different than 1.6.4
    	// Once it is updated, uncomment the last string in TransformWorld.methodsToReplace
    	
        if (!world.doChunksNearChunkExist(x, y, z, 17))
        {
            return false;
        }
        else
        {
            int l = 0;
            int i1 = 0;
            world.theProfiler.startSection("getBrightness");
            int savedLightValue = world.getSavedLightValue(par1Enu, x, y, z);
            int computedLightValue = CLWorldHelper.computeLightValue(world, x, y, z, par1Enu);
            long l1;
            int x1;
            int y1;
            int z1;
            int lightEntry;
            int expectedEntryLight;
            int x2;
            int z2;
            int y2;
            
            // Format of lightUpdateBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets

            if ((computedLightValue&15) > (savedLightValue&15))
            {
            	// Do nothing here, let the computed light take over
                CLWorldHelper.lightUpdateBlockList[i1++] = 133152;   // x=32-32=0 y=32-32=0 z=32-32=0, no light value. It's a blank value.
            }
            else if (computedLightValue < savedLightValue)
            {
                CLWorldHelper.lightUpdateBlockList[i1++] = 133152 | savedLightValue << 18; // Store saved light value at rel(0,0,0)

                while (l < i1)
                {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++];
                    x1 = (int)((l1 & 63) - 32 + x);
                    y1 = (int)((l1 >> 6 & 63) - 32 + y);
                    z1 = (int)((l1 >> 12 & 63) - 32 + z);
                    lightEntry = (int)(l1 >> 18);
                    expectedEntryLight = world.getSavedLightValue(par1Enu, x1, y1, z1);

                    if ((expectedEntryLight&15) == (lightEntry&15))
                    {                    	
                        world.setLightValue(par1Enu, x1, y1, z1, 0);

                        if ((lightEntry&15) > 0)
                        {
                            x2 = MathHelper.abs_int(x1 - x);
                            y2 = MathHelper.abs_int(y1 - y);
                            z2 = MathHelper.abs_int(z1 - z);

                            if (x2 + y2 + z2 < 17)
                            {
                            	// Calculate light values for all surrounding blocks
                            	
                                for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
                                {
                                    int xFace = x1 + Facing.offsetsXForSide[faceIndex];
                                    int yFace = y1 + Facing.offsetsYForSide[faceIndex];
                                    int zFace = z1 + Facing.offsetsZForSide[faceIndex];
                                    int opacity = Math.max(1, world.getBlock(xFace, yFace, zFace).getLightOpacity(world, xFace, yFace, zFace));
                                    expectedEntryLight = world.getSavedLightValue(par1Enu, xFace, yFace, zFace);
                                    
                                    // heaton84 - RGB components are not needed here. See how lightEntry is set above.
                                    int ll = lightEntry&15;
                                    int rl = lightEntry&480;
                                    int gl = lightEntry&15360;
                                    int bl = lightEntry&491520;
                                                                        
                                   	ll-=opacity;
                                   	rl-=32*opacity;
                                   	gl-=1024*opacity;
                                   	bl-=32768*opacity;
                               	 
                                    if ((expectedEntryLight&15) == ll && i1 < CLWorldHelper.lightUpdateBlockList.length)
                                    {
                                        CLWorldHelper.lightUpdateBlockList[i1++] = xFace - x + 32 | yFace - y + 32 << 6 | zFace - z + 32 << 12 | (ll | rl | gl | bl) << 18;
                                    	//CLWorldHelper.lightUpdateBlockList[i1++] = xFace - x + 32 | yFace - y + 32 << 6 | zFace - z + 32 << 12 | ll  << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                l = 0;
            }

            world.theProfiler.endSection();
            world.theProfiler.startSection("checkedPosition < toCheckCount");

            while (l < i1)
            {
                l1 = CLWorldHelper.lightUpdateBlockList[l++];
                x1 = (int)((l1 & 63) - 32 + x);
                y1 = (int)((l1 >> 6 & 63) - 32 + y);
                z1 = (int)((l1 >> 12 & 63) - 32 + z);
                lightEntry = world.getSavedLightValue(par1Enu, x1, y1, z1);
                expectedEntryLight = CLWorldHelper.computeLightValue(world, x1, y1, z1, par1Enu);

                if (expectedEntryLight != lightEntry)
                {
                	int tempStorageLightValue = lightEntry;
                	
                    if((expectedEntryLight&15) > (lightEntry&15))
                    	tempStorageLightValue = tempStorageLightValue&507360 | expectedEntryLight&15;
                	if((expectedEntryLight&480) > (lightEntry&480))
                		tempStorageLightValue = tempStorageLightValue&506895 | expectedEntryLight&480;
                	if((expectedEntryLight&15360) > (lightEntry&15360))
                		tempStorageLightValue = tempStorageLightValue&492015 | expectedEntryLight&15360;
                	if((expectedEntryLight&491520) > (lightEntry&491520))
                		tempStorageLightValue = tempStorageLightValue&15855 | expectedEntryLight&491520;
                	
                    //world.setLightValue(par1Enu, x1, y1, z1, expectedEntryLight);

                    //if (expectedEntryLight > lightEntry)
                    if ((((1048576|lightEntry) - expectedEntryLight)&541200) > 0)
                    {
                    	
                    	world.setLightValue(par1Enu, x1, y1, z1, tempStorageLightValue);
                    	
                        x2 = Math.abs(x1 - x);
                        y2 = Math.abs(y1 - y);
                        z2 = Math.abs(z1 - z);
                        boolean flag = i1 < CLWorldHelper.lightUpdateBlockList.length - 6;

                        if (x2 + y2 + z2 < 17 && flag)
                        {
                            //if (world.getSavedLightValue(par1Enu, x1 - 1, y1, z1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1 - 1, y1, z1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                            //if (world.getSavedLightValue(par1Enu, x1 + 1, y1, z1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1 + 1, y1, z1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                            //if (world.getSavedLightValue(par1Enu, x1, y1 - 1, z1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1, y1 - 1, z1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                            //if (world.getSavedLightValue(par1Enu, x1, y1 + 1, z1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1, y1 + 1, z1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            }

                            //if (world.getSavedLightValue(par1Enu, x1, y1, z1 - 1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1, y1, z1 - 1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
                            }

                            //if (world.getSavedLightValue(par1Enu, x1, y1, z1 + 1) < expectedEntryLight)
                        	if ((((1048576|world.getSavedLightValue(par1Enu, x1, y1, z1 + 1))-expectedEntryLight)&541200) > 0)
                            {
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
                            }
                        }
                    }
                }
            }

            world.theProfiler.endSection();
            return true;
        }
    }*/

    public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int x, int y, int z) {
        if (!world.doChunksNearChunkExist(x, y, z, 17)) {
            return false;
        } else {
            int l = 0;
            int i1 = 0;
            world.theProfiler.startSection("getBrightness");
            int savedLightValue = world.getSavedLightValue(par1Enu, x, y, z);
            int compLightValue = CLWorldHelper.computeLightValue(world, x, y, z, par1Enu);
            long l1;
            int x1;
            int y1;
            int z1;
            int lightEntry;
            int expectedEntryLight;
            int x2;
            int z2;
            int y2;

            // Format of lightUpdateBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets

            if (compLightValue > savedLightValue) {
                // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
                // push               100000100000100000  (0,0,0):black
                CLWorldHelper.lightUpdateBlockList[i1++] = 133152; //Save Entry at pos 0 (move i1)
            } else if (compLightValue < savedLightValue) {
                // Do nothing here, let the computed light take over
                // push (0,0,0):savedLightVale
                CLWorldHelper.lightUpdateBlockList[i1++] = 133152 | savedLightValue << 18; //Save Entry at pos 0 with its Light Value (move i1)	

                while (l < i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry at l, which starts at 0
                    x1 = ((int) (l1 & 63) - 32 + x); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 63) - 32 + y); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 63) - 32 + z); //Get Entry Z coord
                    lightEntry = (int) (l1 >>> 18) & 507375; //Get Entry's saved Light
                    expectedEntryLight = world.getSavedLightValue(par1Enu, x1, y1, z1); //Get the saved Light Level at the entry's location

                    if ((expectedEntryLight & 15) >= (lightEntry & 15)) // if we pushed black earlier....
                    {
                        world.setLightValue(par1Enu, x1, y1, z1, 0);

                        if (lightEntry > 0) {
                            x2 = MathHelper.abs_int(x1 - x);
                            y2 = MathHelper.abs_int(y1 - y);
                            z2 = MathHelper.abs_int(z1 - z);

                            if (x2 + y2 + z2 < 17) {
                                for (int faceIndex = 0; faceIndex < 6; ++faceIndex) {
                                    int xFace = x1 + Facing.offsetsXForSide[faceIndex];
                                    int yFace = y1 + Facing.offsetsYForSide[faceIndex];
                                    int zFace = z1 + Facing.offsetsZForSide[faceIndex];

                                    int blockOpacity = world.getBlock(xFace, yFace, zFace).getLightOpacity(world, xFace, yFace, zFace);
                                    int opacity = Math.max(1, blockOpacity);
                                    //Get Saved light value from face
                                    expectedEntryLight = world.getSavedLightValue(par1Enu, xFace, yFace, zFace);
                                    int ll = lightEntry & 15;
                                    int rl = lightEntry & 480;
                                    int gl = lightEntry & 15360;
                                    int bl = lightEntry & 491520;

                                    ll -= opacity;
                                    rl -= 32 * opacity;
                                    gl -= 1024 * opacity;
                                    bl -= 32768 * opacity;

                                    if (((expectedEntryLight & 15) >= ll) && (i1 < CLWorldHelper.lightUpdateBlockList.length))
                                        CLWorldHelper.lightUpdateBlockList[i1++] = xFace - x + 32 | (yFace - y + 32 << 6) | (zFace - z + 32 << 12) | ((ll | rl | gl | bl) << 18);
                                }
                            }
                        }
                    }
                }
                //reset l, so we can loop through all of the updates again!
                l = 0;
            }

            world.theProfiler.endSection();
            world.theProfiler.startSection("checkedPosition < toCheckCount");

            while (l < i1) {
                l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry and it's light value (if there is one)
                x1 = ((int) (l1 & 63) - 32 + x); //Get Entry X coord
                y1 = ((int) (l1 >> 6 & 63) - 32 + y); //Get Entry Y coord
                z1 = ((int) (l1 >> 12 & 63) - 32 + z); //Get Entry Z coord

                //Get the Saved Light at the Entry's Position
                lightEntry = world.getSavedLightValue(par1Enu, x1, y1, z1);

                //Compute the light level at the entry's location.  If the light's have been set to zero before this occurs,
                //then the computation will change dynamically
                expectedEntryLight = CLWorldHelper.computeLightValue(world, x1, y1, z1, par1Enu);

                if (expectedEntryLight != lightEntry) {
                    int tempStorageLightValue = lightEntry;

                    if ((expectedEntryLight & 15) > (lightEntry & 15))
                        tempStorageLightValue = tempStorageLightValue & 507360 | expectedEntryLight & 15;
                    if ((expectedEntryLight & 480) > (lightEntry & 480))
                        tempStorageLightValue = tempStorageLightValue & 506895 | expectedEntryLight & 480;
                    if ((expectedEntryLight & 15360) > (lightEntry & 15360))
                        tempStorageLightValue = tempStorageLightValue & 492015 | expectedEntryLight & 15360;
                    if ((expectedEntryLight & 491520) > (lightEntry & 491520))
                        tempStorageLightValue = tempStorageLightValue & 15855 | expectedEntryLight & 491520;

                    if ((((1048576 | lightEntry) - expectedEntryLight) & 541200) > 0)//If the light entry is smaller
                    {
                        //Moved this here, from the lines above
                        world.setLightValue(par1Enu, x1, y1, z1, tempStorageLightValue);

                        x2 = Math.abs(x1 - x);
                        y2 = Math.abs(y1 - y);
                        z2 = Math.abs(z1 - z);
                        boolean flag = i1 < CLWorldHelper.lightUpdateBlockList.length - 6; //What's with the minus 6?  6 Sides on cube?

                        if (x2 + y2 + z2 < 17 && flag) {
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1 - 1, y1, z1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1 + 1, y1, z1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1, y1 - 1, z1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1, y1 + 1, z1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1, y1, z1 - 1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
                            if ((((1048576 | world.getSavedLightValue(par1Enu, x1, y1, z1 + 1)) - expectedEntryLight) & 541200) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
                        }
                    }
                }
            }

            world.theProfiler.endSection();
            return true;
        }
    }
}
