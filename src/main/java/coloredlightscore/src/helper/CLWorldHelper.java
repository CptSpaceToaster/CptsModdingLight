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
            if (block != null && blockLight > 0 && par1Enu == EnumSkyBlock.Block) {
                return blockLight; // TODO: This looked alright to start... but placing a dim redstone torch in a red light signals to the lighting calcs thatthe engine should DIM... for no good reason.
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

            // I have no idea what this messes with...
            world.theProfiler.startSection("getBrightness");
            world.theProfiler.endSection();
            world.theProfiler.startSection("checkedPosition < toCheckCount");
            world.theProfiler.endSection();

            long savedLightValue = world.getSavedLightValue(par1Enu, parX, parY, parZ);
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
            int sortValue;

            // Format of lightUpdateBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets

            if ((compLightValue&0x0000F) > (savedLightValue&0x0000F)) { //compLightValue has components that are larger than savedLightValue, the block at the current position is brighter than the saved value at the current positon... it must have been made brighter somehow
                //Light Splat/Spread
                CLWorldHelper.lightUpdateBlockList[i1++] = (0x20820L | (compLightValue << 18L));

                while (l < i1) {
                    l1 = CLWorldHelper.lightUpdateBlockList[l++]; //Get Entry at l, which starts at 0
                    x1 = ((int) (l1 & 0x3f) - 32 + parX); //Get Entry X coord
                    y1 = ((int) (l1 >> 6 & 0x3f) - 32 + parY); //Get Entry Y coord
                    z1 = ((int) (l1 >> 12 & 0x3f) - 32 + parZ); //Get Entry Z coord
                    lightEntry = ((int) ((l1 >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)
                    edgeLightEntry = world.getSavedLightValue(par1Enu, x1, y1, z1); //Get the saved Light Level at the entry's location - Instead of comparing against the value saved on disk every iteration, and checking to see if it's been updated already... Consider storing values in a temp 3D array as they are gathered and applying changes all at once

                    if ((((0x100000 | edgeLightEntry) - lightEntry) & 0x84210) > 0) { // Components in lightEntry are brighter than in edgeLightEntry
                        x2 = MathHelper.abs_int(x1 - parX);
                        y2 = MathHelper.abs_int(y1 - parY);
                        z2 = MathHelper.abs_int(z1 - parZ);
                        manhattan_distance = x2 + y2 + z2;

                        world.setLightValue(par1Enu, x1, y1, z1, lightEntry);
                        CLWorldHelper.lightBackfillNeeded[x1 - parX + 14][y1 - parY + 14][z1 - parZ + 14] = false; // TODO: Not always the case... sometimes, the values on the outside indicate that you SHOULDN'T set this to false.

                        if (manhattan_distance < ((compLightValue&0x0000F) - 1)) { //Limits the splat size to the initial brightness value
                            for (faceIndex = 0; faceIndex < 6; ++faceIndex) {
                                xFace = x1 + Facing.offsetsXForSide[faceIndex];
                                yFace = y1 + Facing.offsetsYForSide[faceIndex];
                                zFace = z1 + Facing.offsetsZForSide[faceIndex];

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
                            }
                        }
                    }
                }
            } else if ((savedLightValue&0x0000F) > (compLightValue&0x0000F)) { //savedLightValue has components that are larger than compLightValue
                //Light Destruction

                for(int i=0; i<CLWorldHelper.lightBackfillIndexes.length; i++)  {
                    CLWorldHelper.lightBackfillIndexes[i] = 0; // Clean up the index array - May not be necessary
                }

                world.setLightValue(par1Enu, parX, parY, parZ, (int)compLightValue); // This kills the light
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

                    if (manhattan_distance < ((savedLightValue & 0x0000F))) { //Limits the splat size to the initial brightness value
                        for (faceIndex = 0; faceIndex < 6; ++faceIndex) {
                            xFace = x1 + Facing.offsetsXForSide[faceIndex];
                            yFace = y1 + Facing.offsetsYForSide[faceIndex];
                            zFace = z1 + Facing.offsetsZForSide[faceIndex];

                            x2 = MathHelper.abs_int(xFace - parX);
                            y2 = MathHelper.abs_int(yFace - parY);
                            z2 = MathHelper.abs_int(zFace - parZ);

                            opacity = Math.max(1, world.getBlock(xFace, yFace, zFace).getLightOpacity(world, xFace, yFace, zFace));

                            if (opacity < 15) {
                                //Get Saved light value from face
                                edgeLightEntry = world.getSavedLightValue(par1Enu, xFace, yFace, zFace);

                                //   |-----------maximum theoretical light value-----------|    |----saved light value----|
                                ll = (Math.max((lightEntry & 0x0000F) - ((x2 + y2 + z2)), 0) >= (edgeLightEntry & 0x0000F)) ? 0 : (edgeLightEntry & 0x0000F);
                                rl = (Math.max((lightEntry & 0x001E0) - ((x2 + y2 + z2) << 5), 0) >= (edgeLightEntry & 0x001E0)) ? 0 : (edgeLightEntry & 0x001E0);
                                gl = (Math.max((lightEntry & 0x03C00) - ((x2 + y2 + z2) << 10), 0) >= (edgeLightEntry & 0x03C00)) ? 0 : (edgeLightEntry & 0x03C00);
                                bl = (Math.max((lightEntry & 0x78000) - ((x2 + y2 + z2) << 15), 0) >= (edgeLightEntry & 0x78000)) ? 0 : (edgeLightEntry & 0x78000);


                                sortValue = 0;
                                if (((lightEntry & 0x0000F) > 0) && (ll != 0)) {
                                    sortValue = (int)ll;
                                }
                                if (((lightEntry & 0x001E0) > 0) && ((rl >> 5) > sortValue )) {
                                    sortValue = (int)(rl >> 5);
                                }
                                if (((lightEntry & 0x03C00) > 0) && ((gl >> 10) > sortValue )) {
                                    sortValue = (int)(gl >> 10);
                                }
                                if (((lightEntry & 0x78000) > 0) && ((bl >> 15) > sortValue )) {
                                    sortValue = (int)(bl >> 15);
                                }

                                //If the light we are looking at on the edge is brighter or equal to the current light in any way, then there must be a light over there that's doing it, so we'll stop eating colors and lights in that direction
                                if (edgeLightEntry != (ll | rl | gl | bl)) {

                                    if (sortValue != 0) {
                                        if (ll == sortValue) {
                                            lightEntry &= ~(0x0000F);
                                        }
                                        if ((rl>>5) == sortValue) {
                                            lightEntry &= ~(0x001E0);
                                        }
                                        if ((gl>>10) == sortValue) {
                                            lightEntry &= ~(0x03C00);
                                        }
                                        if ((bl>>15) == sortValue) {
                                            lightEntry &= ~(0x78000);
                                        }
                                        CLWorldHelper.lightBackfillNeeded[x1 - parX + 14][y1 - parY + 14][z1 - parZ + 14] = true;
                                        CLWorldHelper.lightBackfillBlockList[sortValue-1][CLWorldHelper.lightBackfillIndexes[sortValue-1]++] = (x1 - parX + 32) | ((y1 - parY + 32) << 6) | ((z1 - parZ + 32) << 12); //record coordinates for backfill
                                    }

                                    world.setLightValue(par1Enu, xFace, yFace, zFace, (int) (ll | rl | gl | bl)); // This kills the light
                                    CLWorldHelper.lightUpdateBlockList[i1++] = ((long) xFace - (long) parX + 32L) | (((long) yFace - (long) parY + 32L) << 6L) | (((long) zFace - (long) parZ + 32L) << 12L) | ((long) lightEntry << 18L); //this array keeps the algorithm going, don't touch
                                } else {
                                    if (sortValue != 0) {
                                        CLWorldHelper.lightBackfillNeeded[x1 - parX + 14][y1 - parY + 14][z1 - parZ + 14] = true;
                                        CLWorldHelper.lightBackfillBlockList[sortValue-1][CLWorldHelper.lightBackfillIndexes[sortValue-1]++] = (x1 - parX + 32) | ((y1 - parY + 32) << 6) | ((z1 - parZ + 32) << 12); //record coordinates for backfill
                                    }
                                }
                            }
                        }
                    }
                }
                /*
                //Backfill
                for (l=CLWorldHelper.lightBackfillIndexes.length - 1; l >= 0; l--) {
                    while (CLWorldHelper.lightBackfillIndexes[l] > 0) {
                        i1 = CLWorldHelper.lightBackfillBlockList[l][--CLWorldHelper.lightBackfillIndexes[l]];
                        x1 = (i1 & 0x3f) - 32; //Get Entry X coord
                        y1 = (i1 >> 6 & 0x3f) - 32; //Get Entry Y coord
                        z1 = (i1 >> 12 & 0x3f) - 32; //Get Entry Z coord

                        if (CLWorldHelper.lightBackfillNeeded[x1 + 14][y1 + 14][z1 + 14]) {
                            world.setLightValue(par1Enu, x1 + parX, y1 + parY, z1 + parZ, 0); // Forcibly clear the light, so the backfill routine notices it's missing, and fixes it!
                            updateLightByType(world, par1Enu, x1 + parX, y1 + parY, z1 + parZ); ///oooooOOOOoooo spoooky!
                        }
                    }
                }
                */
            }
            return true;
        }
    }

    //TODO: Remove nop()
    private static void nop() {
        return;
    }
}
