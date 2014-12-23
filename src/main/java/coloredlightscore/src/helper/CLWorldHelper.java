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

    public static long[] lightUpdateBlockList = new long[32768]; // Note... this is ridiculously huge...  something tells me that we can size this down safely  near 15000 or so
    public static long[] sortedBlockList = new long[4991];
    public static int[] manhattenShellIndexes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static final int[] manhattanShellOffsets = {0, 1, 7, 25, 63, 129, 231, 377, 575, 833, 1159, 1561, 2047, 2625, 3303, 4089}; // Number of blocks in the diamond shell where index == manhattanDistance

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

                if ((l & 0xf) > (l1 & 0xf)) {
                    l1 = l;
                }

                if ((i1 & 0xf) > (l1 & 0xf)) {
                    l1 = i1;
                }

                if ((j1 & 0xf) > (l1 & 0xf)) {
                    l1 = j1;
                }

                if ((k1 & 0xf) > (l1 & 0xf)) {
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
                x &= 0xf;
                z &= 0xf;

                //CLLog.info("NEWTEST {},{}:{}", cx, cz, Integer.toBinaryString(chunk.getBlockLightValue(0, 0, 0, 15)));

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

        lightValue = ((lightValue & 0xf) | ((lightValue & 0x1e0) >> 1) | ((lightValue & 0x3c00) >> 2) | ((lightValue & 0x78000) >> 3));

        blockBrightness = ((blockBrightness & 0xf) | ((blockBrightness & 0x1e0) >> 1) | ((blockBrightness & 0x3c00) >> 2) | ((blockBrightness & 0x78000) >> 3));

        if ((blockBrightness & 0x000f) < (lightValue & 0x000f)) {
            blockBrightness = blockBrightness & 0xfff0 | lightValue & 0x000f;
        }
        if ((blockBrightness & 0x00f0) < (lightValue & 0x00f0)) {
            blockBrightness = blockBrightness & 0xff0f | lightValue & 0x00f0;
        }
        if ((blockBrightness & 0x0f00) < (lightValue & 0x0f00)) {
            blockBrightness = blockBrightness & 0xf0ff | lightValue & 0x0f00;
        }
        if ((blockBrightness & 0xf000) < (lightValue & 0xf000)) {
            blockBrightness = blockBrightness & 0x0fff | lightValue & 0xf000;
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

    //Copied from the world class in 1.7.2, modified from the source from 1.6.4, made the method STATIC, made it PUBLIC
    //Added the parameter 'World world, ' and then replaces all instances of 'this', with world
    public static int computeLightValue(World world, int parX, int parY, int parZ, EnumSkyBlock par1Enu) {
        if (par1Enu == EnumSkyBlock.Sky && world.canBlockSeeTheSky(parX, parY, parZ)) {
            return 15;
        } else {
            Block block = world.getBlock(parX, parY, parZ);
            int blockLight = (block == null ? 0 : block.getLightValue(world, parX, parY, parZ));
            if (block != null && blockLight > 0 && par1Enu == EnumSkyBlock.Block) {
                return blockLight;
            }
            int currentLight = par1Enu == EnumSkyBlock.Sky ? 0 : blockLight;
            int opacity = (block == null ? 0 : block.getLightOpacity(world, parX, parY, parZ));

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
                    int l1 = parX + Facing.offsetsXForSide[faceIndex];
                    int i2 = parY + Facing.offsetsYForSide[faceIndex];
                    int j2 = parZ + Facing.offsetsZForSide[faceIndex];

                    int neighborLight = world.getSavedLightValue(par1Enu, l1, i2, j2);
                    int ll = neighborLight & 0x0000F;
                    int rl = neighborLight & 0x001E0;
                    int gl = neighborLight & 0x03C00;
                    int bl = neighborLight & 0x78000;

                    ll -= opacity & 0x0000F;
                    /* TODO: Colored Opacity
                    rl -= opacity & 0x001E0;
                    gl -= opacity & 0x03C00;
                    bl -= opacity & 0x78000;
                    */
                    //Use vanilla light opacity for now
                    rl =  Math.max(0, rl - (opacity << 5));
                    gl =  Math.max(0, gl - (opacity << 10));
                    bl =  Math.max(0, bl - (opacity << 15));

                    // For each component, retain greater of currentLight, (neighborLight - opacity)
                    if (ll > (currentLight & 0x0000F)) {
                        currentLight = (currentLight & 0x7BDE0) | ll; // 0x1E0 | 0x3C00 | 0x78000
                    }
                    if (rl > (currentLight & 0x001E0)) {
                        currentLight = (currentLight & 0x7BC0F) | rl; // 0x00F | 0x3C00 | 0x78000
                    }   
                    if (gl > (currentLight & 0x03C00)) {
                        currentLight = (currentLight & 0x781EF) | gl; // 0x00F | 0x01E0 | 0x78000
                    }
                    if (bl > (currentLight & 0x78000)) {
                        currentLight = (currentLight & 0x03DEF) | bl; // 0x00F | 0x01E0 | 0x03C00
                    }
                }
                return currentLight;
            }
        }
    }

    public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int parX, int parY, int parZ) {
        if (!world.doChunksNearChunkExist(parX, parY, parZ, 17)) {
            return false;
        } else {
            int l = 0;
            int i1 = 0;
            world.theProfiler.startSection("getBrightness");
            long savedLightValue = world.getSavedLightValue(par1Enu, parX, parY, parZ);
            //TODO: Most of ths call to computeLightValue was crammed in here... it's probably not necessary anymore
            long compLightValue = CLWorldHelper.computeLightValue(world, parX, parY, parZ, par1Enu);
            long l1;
            int x1;
            int y1;
            int z1;
            int x2;
            int y2;
            int z2;
            int xFace;
            int zFace;
            int yFace;
            int opacity;
            int faceIndex;
            int lightEntry;
            int edgeLightEntry;
            long manhattan_distance;
            long ll;
            long rl;
            long gl;
            long bl;

            // Format of lightUpdateBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets

            if ((compLightValue&0x0000F) > (savedLightValue&0x0000F)) { //compLightValue has components that are larger than savedLightValue, the block at the current position is brighter than the saved value at the current positon... it must have been made brighter somehow
                CLWorldHelper.lightUpdateBlockList[i1++] = (0x20820L | (compLightValue << 18L));

                while (l < i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry at l, which starts at 0
                    x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    lightEntry = ((int) ((l1 >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)
                    edgeLightEntry = world.getSavedLightValue(par1Enu, x1, y1, z1); //Get the saved Light Level at the entry's location - Instead of comparing against the value saved on disk every iteration, and checking to see if it's been updated already... Consider storing values in a temp 3D array as they are gathered and applying changes all at once

                    //if ((lightEntry&0x0000F) > (edgeEntryLight&0x0000F)) { // No splat overlap lightEntry's L value is brighter than the edgeLightEntry
                    if ((((0x100000 | edgeLightEntry) - lightEntry) & 0x84210) > 0) { // Components in lightEntry are brighter than in edgeLightEntry
                        x2 = MathHelper.abs_int(x1 - parX);
                        y2 = MathHelper.abs_int(y1 - parY);
                        z2 = MathHelper.abs_int(z1 - parZ);
                        manhattan_distance = x2 + y2 + z2;

                        world.setLightValue(par1Enu, x1, y1, z1, lightEntry);

                        if (manhattan_distance < ((compLightValue&0x0000F) - 1)) { //Limits the splat size to the initial brightness value
                            for (faceIndex = 0; faceIndex < 6; ++faceIndex) {
                                xFace = x1 + Facing.offsetsXForSide[faceIndex];
                                yFace = y1 + Facing.offsetsYForSide[faceIndex];
                                zFace = z1 + Facing.offsetsZForSide[faceIndex];

                                //x2 = MathHelper.abs_int(xFace - parX);
                                //y2 = MathHelper.abs_int(yFace - parY);
                                //z2 = MathHelper.abs_int(zFace - parZ);

                                //if (x2 + y2 + z2 > manhattan_distance) { // Only look outwards as the cube expands out

                                    opacity = Math.max(1, world.getBlock(xFace, yFace, zFace).getLightOpacity(world, xFace, yFace, zFace));

                                    if (opacity < 15) {
                                        //Get Saved light value from face
                                        edgeLightEntry = world.getSavedLightValue(par1Enu, xFace, yFace, zFace);
                                        //TODO: Colored Opacity
                                        ll = (lightEntry & 0x0000F) > (edgeLightEntry & 0x0000F) ? Math.max(0, (lightEntry & 0x0000F) - (opacity)) : edgeLightEntry & 0x0000F;
                                        rl = (lightEntry & 0x001E0) > (edgeLightEntry & 0x001E0) ? Math.max(0, (lightEntry & 0x001E0) - (opacity << 5)) : edgeLightEntry & 0x001E0;
                                        gl = (lightEntry & 0x03C00) > (edgeLightEntry & 0x03C00) ? Math.max(0, (lightEntry & 0x03C00) - (opacity << 10)) : edgeLightEntry & 0x03C00;
                                        bl = (lightEntry & 0x78000) > (edgeLightEntry & 0x78000) ? Math.max(0, (lightEntry & 0x78000) - (opacity << 15)) : edgeLightEntry & 0x78000;

                                        if (((ll > (edgeLightEntry & 0x0000F)) ||
                                             (rl > (edgeLightEntry & 0x001E0)) ||
                                             (gl > (edgeLightEntry & 0x03C00)) ||
                                             (bl > (edgeLightEntry & 0x78000))) && (i1 < CLWorldHelper.lightUpdateBlockList.length)) {
                                            CLWorldHelper.lightUpdateBlockList[i1++] = ((long)xFace - (long)parX + 32L) | (((long)yFace - (long)parY + 32L) << 6L) | (((long)zFace - (long)parZ + 32L) << 12L) | ((ll | rl | gl | bl) << 18L);
                                        }
                                    }
                                //}
                            }
                        }
                    }
                }
                //world.setLightValue(par1Enu, x1, y1, z1, tempStorageLightValue);
            } else if ((savedLightValue&0x0000F) > (compLightValue&0x0000F)) { //savedLightValue has components that are larger than compLightValue
                //TODO: clear
                for(int i=0; i<16; i++)  {
                    manhattenShellIndexes[i] = 0; // Clean up the index array
                }

                world.setLightValue(par1Enu, parX, parY, parZ, (int)compLightValue); // This kills the light - Does this need to be set to compLightValue
                CLWorldHelper.sortedBlockList[(CLWorldHelper.manhattenShellIndexes[0]++) + CLWorldHelper.manhattanShellOffsets[0]] = 0x20820L;
                CLWorldHelper.lightUpdateBlockList[i1++] = (0x20820L | (savedLightValue << 18L));

                while (l <= i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry at l, which starts at 0
                    x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    lightEntry = ((int) ((l1 >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)

                    x2 = MathHelper.abs_int(x1 - parX);
                    y2 = MathHelper.abs_int(y1 - parY);
                    z2 = MathHelper.abs_int(z1 - parZ);
                    manhattan_distance = x2 + y2 + z2;

                    if (manhattan_distance < ((savedLightValue & 0x0000F) - 1)) { //Limits the splat size to the initial brightness value
                        for (faceIndex = 0; faceIndex < 6; ++faceIndex) {
                            xFace = x1 + Facing.offsetsXForSide[faceIndex];
                            yFace = y1 + Facing.offsetsYForSide[faceIndex];
                            zFace = z1 + Facing.offsetsZForSide[faceIndex];

                            x2 = MathHelper.abs_int(xFace - parX);
                            y2 = MathHelper.abs_int(yFace - parY);
                            z2 = MathHelper.abs_int(zFace - parZ);

                            //if (x2 + y2 + z2 > manhattan_distance) { // Only look outwards as the cube expands out
                                opacity = Math.max(1, world.getBlock(xFace, yFace, zFace).getLightOpacity(world, xFace, yFace, zFace));

                                if (opacity < 15) {
                                    //Get Saved light value from face
                                    edgeLightEntry = world.getSavedLightValue(par1Enu, xFace, yFace, zFace);

                                    //   |-------------maximum theoretical light value--------------|    |----saved light value----|
                                    ll = (Math.max((savedLightValue & 0x0000F) - ((x2 + y2 + z2)), 0) >= (edgeLightEntry & 0x0000F)) ? 0 : (edgeLightEntry & 0x0000F);
                                    rl = (Math.max((savedLightValue & 0x001E0) - ((x2 + y2 + z2) << 5), 0) >= (edgeLightEntry & 0x001E0)) ? 0 : (edgeLightEntry & 0x001E0);;
                                    gl = (Math.max((savedLightValue & 0x03C00) - ((x2 + y2 + z2) << 10), 0) >= (edgeLightEntry & 0x03C00)) ? 0 : (edgeLightEntry & 0x03C00);;
                                    bl = (Math.max((savedLightValue & 0x78000) - ((x2 + y2 + z2) << 15), 0) >= (edgeLightEntry & 0x78000)) ? 0 : (edgeLightEntry & 0x78000);;

                                    //If the light we are looking at on the edge is brighter or equal to the current light in any way, then there must be a light over there that's doing it, so we'll stop eating colors and lights in that direction
                                    //if (((((0x100000 | edgeLightEntry) - lightEntry) & 0x84210) > 0) && (edgeLightEntry != 0) && (i1 < CLWorldHelper.lightUpdateBlockList.length)) { // Components in lightEntry are brighter than in edgeLightEntry
                                    if (edgeLightEntry != (ll | rl | gl | bl)) {
                                        //On a per-channel basis, calculate each color component at the location, and compare it to the distance we're out.  Destroy if it matches

                                        //if (i1 == 4000) {
                                        //    int test = (int) (ll | rl | gl | bl); //NOP TO BREAKPOINT
                                        //}

                                        world.setLightValue(par1Enu, xFace, yFace, zFace, (int)(ll | rl | gl | bl)); // This kills the light
                                        CLWorldHelper.manhattenShellIndexes[x2 + y2 + z2]++;

                                        //CLWorldHelper.sortedBlockList[(CLWorldHelper.manhattenShellIndexes[x2 + y2 + z2]++) + CLWorldHelper.manhattanShellOffsets[x2 + y2 + z2]] = ((long)xFace - (long)parX + 32L) | (((long)yFace - (long)parY + 32L) << 6L) | (((long)zFace - (long)parZ + 32L) << 12L); //sort entries into piles based on their manhattan distance
                                        CLWorldHelper.lightUpdateBlockList[i1++] = ((long)xFace - (long)parX + 32L) | (((long)yFace - (long)parY + 32L) << 6L) | (((long)zFace - (long)parZ + 32L) << 12L) | ((long)edgeLightEntry << 18L); //this array keeps the algorithm going, don't touch
                                    }
                                }
                            //}
                        }
                    }
                }



                /*
                l = (int)(savedLightValue&0x0000F); //NOP TO BREAKPOINT

                //TODO:  backfill
                for (l = (int)(savedLightValue&0x0000F); l >= 0; l--) {
                    while (CLWorldHelper.manhattenShellIndexes[l] > 0) {
                        l1 = CLWorldHelper.sortedBlockList[(--CLWorldHelper.manhattenShellIndexes[l]) + CLWorldHelper.manhattanShellOffsets[l]]; //Get furthest light entries
                        x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                        y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                        z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord

                        compLightValue = CLWorldHelper.computeLightValue(world, x1, y1, z1, par1Enu);

                        if (compLightValue > 0) {
                            world.setLightValue(par1Enu, x1, y1, z1, (int) compLightValue);
                        }
                    }
                }
                */

                /*
                while (l > 1) {

                    l1 = CLWorldHelper.lightUpdateBlockList[--l]; //Get Entry at l, which starts at wherever i1 ended...
                    x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    //lightEntry = ((int) ((l1 >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)  Not needed for now
                    //TODO: Optimize computeLightValue() to take manhatten distance into account, and only look away from the current position
                    //TODO: This is called up to 4091 times (one for each block bothered in the destruction routine above, but it the edges is black, it doesn't need to travel farther in...
                    compLightValue = CLWorldHelper.computeLightValue(world, x1, y1, z1, par1Enu);

                    if (compLightValue > 0) { // oop... there should be light here and there isn't!
                        world.setLightValue(par1Enu, x1, y1, z1, (int)compLightValue); //TODO: This is a redundent call... mostly...
                        CLWorldHelper.lightUpdateBlockList[i1++] = ((long)x1 + 32L) | (((long)y1 + 32L) << 6L) | (((long)z1 + 32L) << 12L) | (compLightValue << 18L);
                    }
                }

                while (j1 < i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[j1++]; //Get Entry at j1, which starts at wherever i1 ended...
                    x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    //lightEntry = ((int) ((l1 >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)  Not needed for now

                    x2 = MathHelper.abs_int(x1 - parX);
                    y2 = MathHelper.abs_int(y1 - parY);
                    z2 = MathHelper.abs_int(z1 - parZ);
                    manhattan_distance = x2 + y2 + z2;

                    //world.setLightValue(par1Enu, x1, y1, z1, lightEntry); //TODO: This is a redundent call... mostly...

                    for (faceIndex = 0; faceIndex < 6; ++faceIndex) {
                        xFace = x1 + Facing.offsetsXForSide[faceIndex];
                        yFace = y1 + Facing.offsetsYForSide[faceIndex];
                        zFace = z1 + Facing.offsetsZForSide[faceIndex];

                        x2 = MathHelper.abs_int(xFace - parX);
                        y2 = MathHelper.abs_int(yFace - parY);
                        z2 = MathHelper.abs_int(zFace - parZ);

                        if (x2 + y2 + z2 < manhattan_distance) { // Only look inwards as the cube shrinks back in
                            if (world.getSavedLightValue(par1Enu, xFace, yFace, zFace) == 0) {

                                compLightValue = CLWorldHelper.computeLightValue(world, xFace, yFace, zFace, par1Enu);

                                if (compLightValue > 0) { // oop... there should be light here and there isn't!
                                    world.setLightValue(par1Enu, xFace, yFace, zFace, (int) compLightValue);
                                    CLWorldHelper.lightUpdateBlockList[i1++] = ((long) xFace - (long) parX + 32L) | (((long) yFace - (long) parY + 32L) << 6L) | (((long) zFace - (long) parZ + 32L) << 12L) | (compLightValue << 18L);
                                }
                            }
                        }
                    }
                }
                */
            }


            world.theProfiler.endSection();
            world.theProfiler.startSection("checkedPosition < toCheckCount");
            world.theProfiler.endSection();
            return true;
        }
    }

    /*public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int x, int y, int z) {
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
                CLWorldHelper.lightUpdateBlockList[i1++] = 0x20820; //Save Entry at pos 0 (move i1)
            } else if (compLightValue < savedLightValue) {
                // Do nothing here, let the computed light take over
                // push (0,0,0):savedLightVale
                CLWorldHelper.lightUpdateBlockList[i1++] = 0x20820 | savedLightValue << 18; //Save Entry at pos 0 with its Light Value (move i1)

                while (l < i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry at l, which starts at 0
                    x1 = ((int) (l1 & 0x3f) - 32 + x); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + y); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + z); //Get Entry Z coord
                    lightEntry = (int) (l1 >>> 18) & 0x7bdef; //Get Entry's saved Light (0111 1011 1101 1110 1111)
                    expectedEntryLight = world.getSavedLightValue(par1Enu, x1, y1, z1); //Get the saved Light Level at the entry's location

                    if ((expectedEntryLight & 0xf) >= (lightEntry & 0xf)) // if we pushed black earlier....
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
                                    int ll = lightEntry & 0xf;
                                    int rl = lightEntry & 0x1e0;
                                    int gl = lightEntry & 0x3c00;
                                    int bl = lightEntry & 0x78000;

                                    ll -= opacity;
                                    rl -= 0x20 * opacity;
                                    gl -= 0x400 * opacity;
                                    bl -= 0x8000 * opacity;

                                    if (((expectedEntryLight & 0xf) >= ll) && (i1 < CLWorldHelper.lightUpdateBlockList.length))
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
                x1 = ((int) (l1 & 0x3f) - 32 + x); //Get Entry X coord
                y1 = ((int) (l1 >> 6 & 0x3f) - 32 + y); //Get Entry Y coord
                z1 = ((int) (l1 >> 12 & 0x3f) - 32 + z); //Get Entry Z coord

                //Get the Saved Light at the Entry's Position
                lightEntry = world.getSavedLightValue(par1Enu, x1, y1, z1);

                //Compute the light level at the entry's location.  If the light's have been set to zero before this occurs,
                //then the computation will change dynamically
                expectedEntryLight = CLWorldHelper.computeLightValue(world, x1, y1, z1, par1Enu);

                if (expectedEntryLight != lightEntry) {
                    int tempStorageLightValue = lightEntry;

                    // For each component, choose the greater of expectedEntryLight and lightEntry
                    if ((expectedEntryLight & 0xf) > (lightEntry & 0xf))
                        tempStorageLightValue = tempStorageLightValue & 0x7bde0 | expectedEntryLight & 0xf;
                    if ((expectedEntryLight & 0x1e0) > (lightEntry & 0x1e0))
                        tempStorageLightValue = tempStorageLightValue & 0x7bc0f | expectedEntryLight & 0x1e0;
                    if ((expectedEntryLight & 0x3c00) > (lightEntry & 0x3c00))
                        tempStorageLightValue = tempStorageLightValue & 0x781ef | expectedEntryLight & 0x3c00;
                    if ((expectedEntryLight & 0x78000) > (lightEntry & 0x78000))
                        tempStorageLightValue = tempStorageLightValue & 0x3def | expectedEntryLight & 0x78000;

                    if ((((0x100000 | lightEntry) - expectedEntryLight) & 0x84210) > 0)//If any component of the light entry is smaller
                    {
                        //Moved this here, from the lines above
                        world.setLightValue(par1Enu, x1, y1, z1, tempStorageLightValue);

                        x2 = Math.abs(x1 - x);
                        y2 = Math.abs(y1 - y);
                        z2 = Math.abs(z1 - z);
                        boolean flag = i1 < CLWorldHelper.lightUpdateBlockList.length - 6; //Sanity check to avoid going beyond the array bounds below

                        if (x2 + y2 + z2 < 17 && flag) {
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1 - 1, y1, z1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1 + 1, y1, z1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1, y1 - 1, z1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1, y1 + 1, z1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1, y1, z1 - 1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
                            if ((((0x100000 | world.getSavedLightValue(par1Enu, x1, y1, z1 + 1)) - expectedEntryLight) & 0x84210) > 0)
                                CLWorldHelper.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
                        }
                    }
                }
            }

            world.theProfiler.endSection();
            return true;
        }
    }*/
}
