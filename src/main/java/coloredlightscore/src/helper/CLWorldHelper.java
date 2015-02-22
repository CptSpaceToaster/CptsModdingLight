package coloredlightscore.src.helper;

import coloredlightscore.src.asm.ColoredLightsCoreDummyContainer;
import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;

public class CLWorldHelper {
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
                Chunk chunk = world.pipe.getChunkFromChunkCoords(x >> 4, z >> 4);
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
        int skyBrightness = world.pipe.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
        int blockBrightness = world.pipe.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z);

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

    public static int computeLightValue(World world, int par_x, int par_y, int par_z, EnumSkyBlock par1Enu) {
        if (par1Enu == EnumSkyBlock.Sky && world.pipe.canBlockSeeTheSky(par_x, par_y, par_z)) {
            return 15;
        } else {
            Block block = world.getBlock(par_x, par_y, par_z);

            int currentLight = 0;
            if (par1Enu != EnumSkyBlock.Sky) {
                currentLight = (block == null ? 0 : getLightValueSomehow(block, world, par_x, par_y, par_z));
                if ((currentLight > 0) && (currentLight <= 0xF)) {
                    currentLight = (currentLight<<15) | (currentLight<<10) | (currentLight<<5) | currentLight; //copy vanilla brightness into each color component to make it white/grey if it is uncolored.
                }
            }
            int opacity = (block == null ? 0 : block.getLightOpacity(world, par_x, par_y, par_z));

            if (opacity >= 15 && currentLight > 0) {
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
                    int l1 = par_x + Facing.offsetsXForSide[faceIndex];
                    int i2 = par_y + Facing.offsetsYForSide[faceIndex];
                    int j2 = par_z + Facing.offsetsZForSide[faceIndex];

                    int neighborLight = world.pipe.getSavedLightValue(par1Enu, l1, i2, j2);

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

    public static boolean updateLightByType(World world, EnumSkyBlock par1Enu, int par_x, int par_y, int par_z) {
        return CLWorldHelper.updateLightByType_withIncrement(world, par1Enu, par_x, par_y, par_z, true, par_x, par_y, par_z);
    }

    public static boolean updateLightByType_withIncrement(World world, EnumSkyBlock par1Enu, int par_x, int par_y, int par_z, boolean shouldIncrement, int rel_x, int rel_y, int rel_z) {
        if (!world.pipe.doChunksNearChunkExist(par_x, par_y, par_z, 17)) {
            return false;
        } else {

            if (shouldIncrement) {
                //Increment the updateFlag ONLY on a fresh call... This keeps the updateFlag consistent when the algorithm recurses
                // if ((flag_entry != updateFlag) && (flag_entry != updateFlag+1)) { // Light has not been visited by the algorithm yet
                // if (flag_entry == updateFlag) { // Light has been marked for a later update
                // if (flag_entry == updateFlag+1) { // Light has been visited and processed, don't visit in the future generations of this algorithm
                world.pipe.updateFlag += 2;
                world.pipe.flagEntry = par1Enu;
            }

            world.theProfiler.startSection("getBrightness");

            int lightAdditionsSatisfied = 0;
            int lightAdditionsCalled = 0;
            int filler = 0;
            int getter = 0;
            int lightEntry;

            long savedLightValue = world.pipe.getSavedLightValue(par1Enu, par_x, par_y, par_z);
            long compLightValue = CLWorldHelper.computeLightValue(world, par_x, par_y, par_z, par1Enu);
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

            int neighbor_x;
            int neighbor_y;
            int neighbor_z;

            int neighborIndex;
            int neighborLightEntry;

            world.theProfiler.endStartSection("lightAddition");

            // Format of lightAdditionBlockList word:
            // rrrr.gggg.bbbb.LLLLzzzzzzyyyyyyxxxxxx
            // x/y/z are relative offsets
            if ((((0x100000 | savedLightValue) - compLightValue) & 0x84210) > 0) { //compLightValue has components that are larger than savedLightValue, the block at the current position is brighter than the saved value at the current positon... it must have been made brighter somehow
                //Light Splat/Spread

                world.pipe.lightAdditionNeeded[14][14][14] = world.pipe.updateFlag; // Light needs processing processed
                lightAdditionsCalled++;
                world.pipe.lightAdditionBlockList[getter++] = (0x20820L | (compLightValue << 18L));

                while (filler < getter) {
                    queueEntry = world.pipe.lightAdditionBlockList[filler++]; //Get Entry at l, which starts at 0
                    queue_x = ((int) (queueEntry & 0x3f) - 32 + par_x); //Get Entry X coord
                    queue_y = ((int) (queueEntry >> 6 & 0x3f) - 32 + par_y); //Get Entry Y coord
                    queue_z = ((int) (queueEntry >> 12 & 0x3f) - 32 + par_z); //Get Entry Z coord

                    if (world.pipe.lightAdditionNeeded[queue_x - par_x + 14][queue_y - par_y + 14][queue_z - par_z + 14] == world.pipe.updateFlag) { // Light has been marked for a later update

                        queueLightEntry = ((int) ((queueEntry >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)
                        neighborLightEntry = world.pipe.getSavedLightValue(par1Enu, queue_x, queue_y, queue_z); //Get the saved Light Level at the entry's location - Instead of comparing against the value saved on disk every iteration, and checking to see if it's been updated already... Consider storing values in a temp 3D array as they are gathered and applying changes all at once

                        if (Math.abs(queue_x - rel_x) < 14 && Math.abs(queue_y - rel_y) < 14 && Math.abs(queue_z - rel_z) < 14) {
                            world.pipe.lightBackfillNeeded[queue_x - rel_x + 14][queue_y - rel_y + 14][queue_z - rel_z + 14] = world.pipe.updateFlag + 1; // Light has been visited and processed
                        }
                        world.pipe.lightAdditionNeeded[queue_x - par_x + 14][queue_y - par_y + 14][queue_z - par_z + 14] = world.pipe.updateFlag + 1; // Light has been visited and processed

                        lightAdditionsSatisfied++;

                        if ((((0x100000 | neighborLightEntry) - queueLightEntry) & 0x84210) > 0) { // Components in queueLightEntry are brighter than in edgeLightEntry
                            man_x = MathHelper.abs_int(queue_x - par_x);
                            man_y = MathHelper.abs_int(queue_y - par_y);
                            man_z = MathHelper.abs_int(queue_z - par_z);
                            manhattan_distance = man_x + man_y + man_z;

                            world.pipe.setLightValue(par1Enu, queue_x, queue_y, queue_z, queueLightEntry);

                            //if ((manhattan_distance < ((compLightValue & 0x0000F) - 1)) || (par1Enu == EnumSkyBlock.Sky && (man_x<14) && (man_y<14) && (man_z<14))) { //Limits the splat size to the initial brightness value, skylight checks bypass this, as they aren't always diamond-shaped
                            if (manhattan_distance < ((compLightValue & 0x0000F) - 1)) {
                                for (neighborIndex = 0; neighborIndex < 6; ++neighborIndex) {
                                    neighbor_x = queue_x + Facing.offsetsXForSide[neighborIndex];
                                    neighbor_y = queue_y + Facing.offsetsYForSide[neighborIndex];
                                    neighbor_z = queue_z + Facing.offsetsZForSide[neighborIndex];
                                    if (neighbor_y < 0 || neighbor_y > 255)
                                        continue;

                                    lightEntry = world.pipe.lightAdditionNeeded[neighbor_x - par_x + 14][neighbor_y - par_y + 14][neighbor_z - par_z + 14];
                                    if (lightEntry != world.pipe.updateFlag && (lightEntry != world.pipe.updateFlag + 1 || !shouldIncrement)) { // on recursive calls, ignore instances of world.pipe.updateFlag being flag + 1

                                        opacity = Math.max(1, world.getBlock(neighbor_x, neighbor_y, neighbor_z).getLightOpacity(world, neighbor_x, neighbor_y, neighbor_z));

                                        //Proceed only if the block is non-solid
                                        if (opacity < 15) {

                                            //Get Saved light value from face
                                            neighborLightEntry = world.pipe.getSavedLightValue(par1Enu, neighbor_x, neighbor_y, neighbor_z);

                                            //Subtract by 1, as channels diminish by one every block
                                            //TODO: Colored Opacity
                                            ll = (queueLightEntry & 0x0000F) > (neighborLightEntry & 0x0000F) ? Math.max(0, (queueLightEntry & 0x0000F) - (opacity)) : (neighborLightEntry & 0x0000F);
                                            rl = (queueLightEntry & 0x001E0) > (neighborLightEntry & 0x001E0) ? Math.max(0, (queueLightEntry & 0x001E0) - (opacity << 5)) : (neighborLightEntry & 0x001E0);
                                            gl = (queueLightEntry & 0x03C00) > (neighborLightEntry & 0x03C00) ? Math.max(0, (queueLightEntry & 0x03C00) - (opacity << 10)) : (neighborLightEntry & 0x03C00);
                                            bl = (queueLightEntry & 0x78000) > (neighborLightEntry & 0x78000) ? Math.max(0, (queueLightEntry & 0x78000) - (opacity << 15)) : (neighborLightEntry & 0x78000);

                                            if (((ll > (neighborLightEntry & 0x0000F)) ||
                                                    (rl > (neighborLightEntry & 0x001E0)) ||
                                                    (gl > (neighborLightEntry & 0x03C00)) ||
                                                    (bl > (neighborLightEntry & 0x78000))) && (getter < world.pipe.lightAdditionBlockList.length)) {
                                                world.pipe.lightAdditionNeeded[neighbor_x - par_x + 14][neighbor_y - par_y + 14][neighbor_z - par_z + 14] = world.pipe.updateFlag; // Mark neighbor to be processed
                                                world.pipe.lightAdditionBlockList[getter++] = ((long) neighbor_x - (long) par_x + 32L) | (((long) neighbor_y - (long) par_y + 32L) << 6L) | (((long) neighbor_z - (long) par_z + 32L) << 12L) | ((ll | rl | gl | bl) << 18L);
                                                lightAdditionsCalled++;
                                            } else if (((queueLightEntry & 0x0000F) + (opacity) < (neighborLightEntry & 0x0000F)) ||
                                                    ((queueLightEntry & 0x001E0) + (opacity << 5) < (neighborLightEntry & 0x001E0)) ||
                                                    ((queueLightEntry & 0x03C00) + (opacity << 10) < (neighborLightEntry & 0x03C00)) ||
                                                    ((queueLightEntry & 0x78000) + (opacity << 15) < (neighborLightEntry & 0x78000))) {
                                                if (Math.abs(queue_x - rel_x) < 14 && Math.abs(queue_y - rel_y) < 14 && Math.abs(queue_z - rel_z) < 14) {
                                                    world.pipe.lightBackfillNeeded[queue_x - rel_x + 14][queue_y - rel_y + 14][queue_z - rel_z + 14] = world.pipe.updateFlag; // Mark queue location to be re-processed
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if ((filler > 24389) || (lightAdditionsCalled != lightAdditionsSatisfied)) {
                CLLog.warn("Error in Light Addition:" + filler + (par1Enu==EnumSkyBlock.Block?" (isBlock)": " (isSky)") + " Saved:" + Integer.toBinaryString((int) savedLightValue) + " Comp:" + Integer.toBinaryString((int)compLightValue) + " isBackfill:" + " updateFlag:" + world.pipe.updateFlag + " Called:" + lightAdditionsCalled + " Satisfied:" + lightAdditionsSatisfied);
            }

            if (shouldIncrement) { // Only proceed if we are NOT in a recursive call
                world.theProfiler.endStartSection("lightSubtraction");

                //Reset indexes
                filler = 0;
                getter = 0;

                if ((((0x100000 | compLightValue) - savedLightValue) & 0x84210) > 0) { //savedLightValue has components that are larger than compLightValue
                    //Light Destruction

                    world.pipe.setLightValue(par1Enu, par_x, par_y, par_z, (int) compLightValue); // This kills the light
                    world.pipe.lightAdditionBlockList[getter++] = (0x20820L | (savedLightValue << 18L));

                    while (filler <= getter) {
                        queueEntry = world.pipe.lightAdditionBlockList[filler++]; //Get Entry at l, which starts at 0
                        queue_x = ((int) (queueEntry & 0x3f) - 32 + par_x); //Get Entry X coord
                        queue_y = ((int) (queueEntry >> 6 & 0x3f) - 32 + par_y); //Get Entry Y coord
                        queue_z = ((int) (queueEntry >> 12 & 0x3f) - 32 + par_z); //Get Entry Z coord
                        queueLightEntry = ((int) ((queueEntry >>> 18) & 0x7bdef)); //Get Entry's saved Light (0111 1011 1101 1110 1111)

                        man_x = MathHelper.abs_int(queue_x - par_x);
                        man_y = MathHelper.abs_int(queue_y - par_y);
                        man_z = MathHelper.abs_int(queue_z - par_z);
                        manhattan_distance = man_x + man_y + man_z;

                        if (manhattan_distance < ((savedLightValue & 0x0000F))) { //Limits the splat size to the initial brightness value
                            for (neighborIndex = 0; neighborIndex < 6; ++neighborIndex) {
                                neighbor_x = queue_x + Facing.offsetsXForSide[neighborIndex];
                                neighbor_y = queue_y + Facing.offsetsYForSide[neighborIndex];
                                neighbor_z = queue_z + Facing.offsetsZForSide[neighborIndex];
                                if (neighbor_y < 0 || neighbor_y > 255)
                                    continue;

                                man_x = MathHelper.abs_int(neighbor_x - par_x);
                                man_y = MathHelper.abs_int(neighbor_y - par_y);
                                man_z = MathHelper.abs_int(neighbor_z - par_z);

                                opacity = Math.max(1, world.getBlock(neighbor_x, neighbor_y, neighbor_z).getLightOpacity(world, neighbor_x, neighbor_y, neighbor_z));
                                neighborLightEntry = world.pipe.getSavedLightValue(par1Enu, neighbor_x, neighbor_y, neighbor_z);

                                if (opacity < 15 || neighborLightEntry > 0) {
                                    //Get Saved light value from face

                                    //   |------------------maximum theoretical light value------------------|    |------saved light value------|
                                    ll = (Math.max((queueLightEntry & 0x0000F) - ((man_x + man_y + man_z)), 0) >= (neighborLightEntry & 0x0000F)) ? 0 : (neighborLightEntry & 0x0000F);
                                    rl = (Math.max((queueLightEntry & 0x001E0) - ((man_x + man_y + man_z) << 5), 0) >= (neighborLightEntry & 0x001E0)) ? 0 : (neighborLightEntry & 0x001E0);
                                    gl = (Math.max((queueLightEntry & 0x03C00) - ((man_x + man_y + man_z) << 10), 0) >= (neighborLightEntry & 0x03C00)) ? 0 : (neighborLightEntry & 0x03C00);
                                    bl = (Math.max((queueLightEntry & 0x78000) - ((man_x + man_y + man_z) << 15), 0) >= (neighborLightEntry & 0x78000)) ? 0 : (neighborLightEntry & 0x78000);

                                    sortValue = 0;
                                    if (((queueLightEntry & 0x0000F) > 0) && (ll != 0)) {
                                        sortValue = (int) ll;
                                    }
                                    if (((queueLightEntry & 0x001E0) > 0) && ((rl >> 5) > sortValue)) {
                                        sortValue = (int) (rl >> 5);
                                    }
                                    if (((queueLightEntry & 0x03C00) > 0) && ((gl >> 10) > sortValue)) {
                                        sortValue = (int) (gl >> 10);
                                    }
                                    if (((queueLightEntry & 0x78000) > 0) && ((bl >> 15) > sortValue)) {
                                        sortValue = (int) (bl >> 15);
                                    }

                                    //If the light we are looking at on the edge is brighter or equal to the current light in any way, then there must be a light over there that's doing it, so we'll stop eating colors and lights in that direction
                                    if (neighborLightEntry != (ll | rl | gl | bl)) {

                                        if (sortValue != 0) {
                                            if (ll == sortValue) {
                                                queueLightEntry &= ~(0x0000F);
                                            }
                                            if ((rl >> 5) == sortValue) {
                                                queueLightEntry &= ~(0x001E0);
                                            }
                                            if ((gl >> 10) == sortValue) {
                                                queueLightEntry &= ~(0x03C00);
                                            }
                                            if ((bl >> 15) == sortValue) {
                                                queueLightEntry &= ~(0x78000);
                                            }

                                            world.pipe.lightBackfillNeeded[queue_x - par_x + 14][queue_y - par_y + 14][queue_z - par_z + 14] = world.pipe.updateFlag;
                                            world.pipe.lightBackfillBlockList[sortValue - 1][world.pipe.lightBackfillIndexes[sortValue - 1]++] = (neighbor_x - par_x + 32) | ((neighbor_y - par_y + 32) << 6) | ((neighbor_z - par_z + 32) << 12); //record coordinates for backfill
                                        }

                                        world.pipe.setLightValue(par1Enu, neighbor_x, neighbor_y, neighbor_z, (int) (ll | rl | gl | bl)); // This kills the light
                                        world.pipe.lightAdditionBlockList[getter++] = ((long) neighbor_x - (long) par_x + 32L) | (((long) neighbor_y - (long) par_y + 32L) << 6L) | (((long) neighbor_z - (long) par_z + 32L) << 12L) | ((long) queueLightEntry << 18L); //this array keeps the algorithm going, don't touch
                                    } else {
                                        if (sortValue != 0) {
                                            world.pipe.lightBackfillNeeded[queue_x - par_x + 14][queue_y - par_y + 14][queue_z - par_z + 14] = world.pipe.updateFlag;
                                            world.pipe.lightBackfillBlockList[sortValue - 1][world.pipe.lightBackfillIndexes[sortValue - 1]++] = (queue_x - par_x + 32) | ((queue_y - par_y + 32) << 6) | ((queue_z - par_z + 32) << 12); //record coordinates for backfill
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (filler > 4097) {
                        CLLog.warn("Light Subtraction Overfilled:" + filler + (par1Enu == EnumSkyBlock.Block ? " (isBlock)" : " (isSky)") + " Saved:" + Integer.toBinaryString((int) savedLightValue) + " Comp:" + Integer.toBinaryString((int) compLightValue) + " isBackfill:" + " updateFlag:" + world.pipe.updateFlag + " Called:" + lightAdditionsCalled + " Satisfied:" + lightAdditionsSatisfied);
                    }

                    world.theProfiler.endStartSection("lightBackfill");

                    //Backfill
                    for (filler = world.pipe.lightBackfillIndexes.length - 1; filler >= 0; filler--) {
                        while (world.pipe.lightBackfillIndexes[filler] > 0) {
                            getter = world.pipe.lightBackfillBlockList[filler][--world.pipe.lightBackfillIndexes[filler]];
                            queue_x = (getter & 0x3f) - 32 + par_x; //Get Entry X coord
                            queue_y = (getter >> 6 & 0x3f) - 32 + par_y; //Get Entry Y coord
                            queue_z = (getter >> 12 & 0x3f) - 32 + par_z; //Get Entry Z coord

                            if (world.pipe.lightBackfillNeeded[queue_x - par_x + 14][queue_y - par_y + 14][queue_z - par_z + 14] == world.pipe.updateFlag) {
                                CLWorldHelper.updateLightByType_withIncrement(world, par1Enu, queue_x, queue_y, queue_z, false, rel_x, rel_y, rel_z); ///oooooOOOOoooo spoooky!
                            }
                        }
                    }
                }
            }

            world.theProfiler.endSection();
            return true;
        }
    }

    /**
     * Patching in Dynamic Lights Compatibility
     */
    private static int getLightValueSomehow(Block block, World world, int par_x, int par_y, int par_z) {
        if (ColoredLightsCoreDummyContainer.getDynamicLight != null && world.isRemote) {
                nop();
            try {
                int a = (Integer)ColoredLightsCoreDummyContainer.getDynamicLight.invoke(null, world, block, par_x, par_y, par_z);
                if (a != 0) CLLog.info("got :" + a);
                return a;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return block.getLightValue(world, par_x, par_y, par_z);
    }

    //TODO: Remove nop()
    private static void nop() {
        return;
    }
}