package coloredlightscore.src.helper;


import net.minecraft.init.Blocks;
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
    public static int[] lightBackfillIndexes = new int[15]; // indexes for how many values we added at the index's brightness
    public static int[][] lightBackfillBlockList = new int[15][4991]; // theoretical maximum... "I think"
    public static boolean[][][] lightBackfillNeeded = new boolean[29][29][29];


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

    public static int computeLightValue(World world, int parX, int parY, int parZ, EnumSkyBlock par1Enu) {
        if (par1Enu == EnumSkyBlock.Sky && world.canBlockSeeTheSky(parX, parY, parZ)) {
            return 15;
        } else {
            Block block = world.getBlock(parX, parY, parZ);
            int blockLight = (block == null ? 0 : block.getLightValue(world, parX, parY, parZ));
            //if (block != null && blockLight > 0 && par1Enu == EnumSkyBlock.Block) {
            //    return blockLight;
            //}
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
            else if ((currentLight&15) >= 14) {
                return currentLight;
            }
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
            int filler = 0;
            int getter = 0;

            world.theProfiler.startSection("getBrightness");
            world.theProfiler.endSection();
            world.theProfiler.startSection("checkedPosition < toCheckCount");
            world.theProfiler.endSection();

            long savedLightValue = world.getSavedLightValue(par1Enu, parX, parY, parZ);
            long compLightValue = CLWorldHelper.computeLightValue(world, parX, parY, parZ, par1Enu);
            long queueEntry;
            int queue_x;
            int queue_y;
            int queue_z;
            int queueLightEntry;

            int man_x;
            int man_y;
            int man_z;
            long manhattan_distance;

            long ll;
            long rl;
            long gl;
            long bl;
            int sortValue;
            int opacity;

            int xNeighbor;
            int zNeighbor;
            int yNeighbor;

            int neighborIndex;
            int neighborLightEntry;


            if (savedLightValue == (0xF | 0xF<<5)) {
                nop();
            }


            // Format of lightUpdateBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets
            if ((((0x100000 | savedLightValue) - compLightValue) & 0x84210) > 0) { //compLightValue has components that are larger than savedLightValue, the block at the current position is brighter than the saved value at the current positon... it must have been made brighter somehow
            //if ((compLightValue&0x0000F) > (savedLightValue&0x0000F)) { //compLightValue has components that are larger than savedLightValue, the block at the current position is brighter than the saved value at the current positon... it must have been made brighter somehow
                //Light Splat/Spread
                CLWorldHelper.lightUpdateBlockList[getter++] = (0x20820L | (compLightValue << 18L));

                while (filler < getter) {
                    queueEntry = CLWorldHelper.lightUpdateBlockList[filler++]; //Get Entry at l, which starts at 0
                    queue_x = ((int) (queueEntry & 0x3f) - 32 + parX); //Get Entry X coord
                    queue_y = ((int) (queueEntry >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    queue_z = ((int) (queueEntry >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    queueLightEntry = ((int) ((queueEntry >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)
                    neighborLightEntry = world.getSavedLightValue(par1Enu, queue_x, queue_y, queue_z); //Get the saved Light Level at the entry's location - Instead of comparing against the value saved on disk every iteration, and checking to see if it's been updated already... Consider storing values in a temp 3D array as they are gathered and applying changes all at once

                    if ((((0x100000 | neighborLightEntry) - queueLightEntry) & 0x84210) > 0) { // Components in lightEntry are brighter than in edgeLightEntry
                        man_x = MathHelper.abs_int(queue_x - parX);
                        man_y = MathHelper.abs_int(queue_y - parY);
                        man_z = MathHelper.abs_int(queue_z - parZ);
                        manhattan_distance = man_x + man_y + man_z;

                        world.setLightValue(par1Enu, queue_x, queue_y, queue_z, queueLightEntry);
                        CLWorldHelper.lightBackfillNeeded[queue_x - parX + 14][queue_y - parY + 14][queue_z - parZ + 14] = false;

                        if (manhattan_distance < ((compLightValue&0x0000F) - 1)) { //Limits the splat size to the initial brightness value
                            for (neighborIndex = 0; neighborIndex < 6; ++neighborIndex) {
                                xNeighbor = queue_x + Facing.offsetsXForSide[neighborIndex];
                                yNeighbor = queue_y + Facing.offsetsYForSide[neighborIndex];
                                zNeighbor = queue_z + Facing.offsetsZForSide[neighborIndex];

                                opacity = Math.max(1, world.getBlock(xNeighbor, yNeighbor, zNeighbor).getLightOpacity(world, xNeighbor, yNeighbor, zNeighbor));

                                if (opacity < 15) {
                                    //Get Saved light value from face
                                    neighborLightEntry = world.getSavedLightValue(par1Enu, xNeighbor, yNeighbor, zNeighbor);
                                    //Subtract by 1, as channels diminish by one every block
                                    //TODO: Colored Opacity
                                    ll = (queueLightEntry & 0x0000F) > (neighborLightEntry & 0x0000F) ? Math.max(0, (queueLightEntry & 0x0000F) - (opacity)) : neighborLightEntry & 0x0000F;
                                    rl = (queueLightEntry & 0x001E0) > (neighborLightEntry & 0x001E0) ? Math.max(0, (queueLightEntry & 0x001E0) - (opacity << 5)) : neighborLightEntry & 0x001E0;
                                    gl = (queueLightEntry & 0x03C00) > (neighborLightEntry & 0x03C00) ? Math.max(0, (queueLightEntry & 0x03C00) - (opacity << 10)) : neighborLightEntry & 0x03C00;
                                    bl = (queueLightEntry & 0x78000) > (neighborLightEntry & 0x78000) ? Math.max(0, (queueLightEntry & 0x78000) - (opacity << 15)) : neighborLightEntry & 0x78000;

                                    if (((ll > (neighborLightEntry & 0x0000F)) ||
                                         (rl > (neighborLightEntry & 0x001E0)) ||
                                         (gl > (neighborLightEntry & 0x03C00)) ||
                                         (bl > (neighborLightEntry & 0x78000))) && (getter < CLWorldHelper.lightUpdateBlockList.length)) {
                                        CLWorldHelper.lightUpdateBlockList[getter++] = ((long)xNeighbor - (long)parX + 32L) | (((long)yNeighbor - (long)parY + 32L) << 6L) | (((long)zNeighbor - (long)parZ + 32L) << 12L) | ((ll | rl | gl | bl) << 18L);
                                    }
                                    else if (((queueLightEntry & 0x0000F) < (neighborLightEntry & 0x0000F) + (opacity)) ||
                                               ((queueLightEntry & 0x001E0) < (neighborLightEntry & 0x001E0) + (opacity<<5)) ||
                                               ((queueLightEntry & 0x03C00) < (neighborLightEntry & 0x03C00) + (opacity<<10)) ||
                                               ((queueLightEntry & 0x78000) < (neighborLightEntry & 0x78000) + (opacity<<15))) {
                                        CLWorldHelper.lightBackfillNeeded[queue_x - parX + 14][queue_y - parY + 14][queue_z - parZ + 14] = true; // TODO:
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Reset indexes
            filler = 0;
            getter = 0;


            if ((((0x100000 | compLightValue) - savedLightValue) & 0x84210) > 0) { //savedLightValue has components that are larger than compLightValue
            //if ((savedLightValue&0x0000F) > (compLightValue&0x0000F)) { //savedLightValue has components that are larger than compLightValue
                //Light Destruction

                for(int i=0; i<CLWorldHelper.lightBackfillIndexes.length; i++)  {
                    CLWorldHelper.lightBackfillIndexes[i] = 0; // Clean up the index array - May not be necessary
                }

                world.setLightValue(par1Enu, parX, parY, parZ, (int)compLightValue); // This kills the light
                CLWorldHelper.lightUpdateBlockList[getter++] = (0x20820L | (savedLightValue << 18L));

                while (filler <= getter) {
                    queueEntry = CLWorldHelper.lightUpdateBlockList[filler++]; //Get Entry at l, which starts at 0
                    queue_x = ((int) (queueEntry & 0x3f) - 32 + parX); //Get Entry X coord
                    queue_y = ((int) (queueEntry >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    queue_z = ((int) (queueEntry >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    queueLightEntry = ((int) ((queueEntry >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)

                    man_x = MathHelper.abs_int(queue_x - parX);
                    man_y = MathHelper.abs_int(queue_y - parY);
                    man_z = MathHelper.abs_int(queue_z - parZ);
                    manhattan_distance = man_x + man_y + man_z;

                    if (manhattan_distance < ((savedLightValue & 0x0000F))) { //Limits the splat size to the initial brightness value
                        for (neighborIndex = 0; neighborIndex < 6; ++neighborIndex) {
                            xNeighbor = queue_x + Facing.offsetsXForSide[neighborIndex];
                            yNeighbor = queue_y + Facing.offsetsYForSide[neighborIndex];
                            zNeighbor = queue_z + Facing.offsetsZForSide[neighborIndex];

                            man_x = MathHelper.abs_int(xNeighbor - parX);
                            man_y = MathHelper.abs_int(yNeighbor - parY);
                            man_z = MathHelper.abs_int(zNeighbor - parZ);

                            opacity = Math.max(1, world.getBlock(xNeighbor, yNeighbor, zNeighbor).getLightOpacity(world, xNeighbor, yNeighbor, zNeighbor));
                            neighborLightEntry = world.getSavedLightValue(par1Enu, xNeighbor, yNeighbor, zNeighbor);

                            if (opacity < 15 || neighborLightEntry > 0) {
                                //Get Saved light value from face

                                //   |-----------maximum theoretical light value-----------|    |----saved light value----|
                                ll = (Math.max((queueLightEntry & 0x0000F) - ((man_x + man_y + man_z)), 0) >= (neighborLightEntry & 0x0000F)) ? 0 : (neighborLightEntry & 0x0000F);
                                rl = (Math.max((queueLightEntry & 0x001E0) - ((man_x + man_y + man_z) << 5), 0) >= (neighborLightEntry & 0x001E0)) ? 0 : (neighborLightEntry & 0x001E0);
                                gl = (Math.max((queueLightEntry & 0x03C00) - ((man_x + man_y + man_z) << 10), 0) >= (neighborLightEntry & 0x03C00)) ? 0 : (neighborLightEntry & 0x03C00);
                                bl = (Math.max((queueLightEntry & 0x78000) - ((man_x + man_y + man_z) << 15), 0) >= (neighborLightEntry & 0x78000)) ? 0 : (neighborLightEntry & 0x78000);

                                sortValue = 0;
                                if (((queueLightEntry & 0x0000F) > 0) && (ll != 0)) {
                                    sortValue = (int)ll;
                                }
                                if (((queueLightEntry & 0x001E0) > 0) && ((rl >> 5) > sortValue )) {
                                    sortValue = (int)(rl >> 5);
                                }
                                if (((queueLightEntry & 0x03C00) > 0) && ((gl >> 10) > sortValue )) {
                                    sortValue = (int)(gl >> 10);
                                }
                                if (((queueLightEntry & 0x78000) > 0) && ((bl >> 15) > sortValue )) {
                                    sortValue = (int)(bl >> 15);
                                }

                                //If the light we are looking at on the edge is brighter or equal to the current light in any way, then there must be a light over there that's doing it, so we'll stop eating colors and lights in that direction
                                if (neighborLightEntry != (ll | rl | gl | bl)) {

                                    if (sortValue != 0) {
                                        if (ll == sortValue) {
                                            queueLightEntry &= ~(0x0000F);
                                        }
                                        if ((rl>>5) == sortValue) {
                                            queueLightEntry &= ~(0x001E0);
                                        }
                                        if ((gl>>10) == sortValue) {
                                            queueLightEntry &= ~(0x03C00);
                                        }
                                        if ((bl>>15) == sortValue) {
                                            queueLightEntry &= ~(0x78000);
                                        }
                                        CLWorldHelper.lightBackfillNeeded[queue_x - parX + 14][queue_y - parY + 14][queue_z - parZ + 14] = true;
                                        CLWorldHelper.lightBackfillBlockList[sortValue-1][CLWorldHelper.lightBackfillIndexes[sortValue-1]++] = (xNeighbor - parX + 32) | ((yNeighbor - parY + 32) << 6) | ((zNeighbor - parZ + 32) << 12); //record coordinates for backfill
                                    }

                                    world.setLightValue(par1Enu, xNeighbor, yNeighbor, zNeighbor, (int) (ll | rl | gl | bl)); // This kills the light
                                    CLWorldHelper.lightUpdateBlockList[getter++] = ((long) xNeighbor - (long) parX + 32L) | (((long) yNeighbor - (long) parY + 32L) << 6L) | (((long) zNeighbor - (long) parZ + 32L) << 12L) | ((long) queueLightEntry << 18L); //this array keeps the algorithm going, don't touch
                                } else {
                                    if (sortValue != 0) {
                                        CLWorldHelper.lightBackfillNeeded[queue_x - parX + 14][queue_y - parY + 14][queue_z - parZ + 14] = true;
                                        CLWorldHelper.lightBackfillBlockList[sortValue-1][CLWorldHelper.lightBackfillIndexes[sortValue-1]++] = (queue_x - parX + 32) | ((queue_y - parY + 32) << 6) | ((queue_z - parZ + 32) << 12); //record coordinates for backfill
                                    }
                                }
                            }
                        }
                    }
                }

                //Backfill
                for (filler=CLWorldHelper.lightBackfillIndexes.length - 1; filler >= 0; filler--) {
                    while (CLWorldHelper.lightBackfillIndexes[filler] > 0) {
                        getter = CLWorldHelper.lightBackfillBlockList[filler][--CLWorldHelper.lightBackfillIndexes[filler]];
                        queue_x = (getter & 0x3f) - 32; //Get Entry X coord
                        queue_y = (getter >> 6 & 0x3f) - 32; //Get Entry Y coord
                        queue_z = (getter >> 12 & 0x3f) - 32; //Get Entry Z coord

                        if (CLWorldHelper.lightBackfillNeeded[queue_x + 14][queue_y + 14][queue_z + 14]) {
                            CLWorldHelper.lightBackfillNeeded[queue_x + 14][queue_y + 14][queue_z + 14] = false;

                            //world.setBlock(x1 + parX, y1 + parY, z1 + parZ, Blocks.stone, 0, 0);

                            world.setLightValue(par1Enu, queue_x + parX, queue_y + parY, queue_z + parZ, 0); // Forcibly clear the light, so the backfill routine notices it's missing, and fixes it!
                            updateLightByType(world, par1Enu, queue_x + parX, queue_y + parY, queue_z + parZ); ///oooooOOOOoooo spoooky!
                        }
                    }
                }
            }
            return true;
        }
    }

    //TODO: Remove nop()
    private static void nop() {
        return;
    }
}
