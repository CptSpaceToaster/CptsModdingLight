package yamhaven.easycoloredlights;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class CLWorldGen implements IWorldGenerator {
    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId == -1) {
            BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
            if(b.biomeName.equals("Hell")) {
                int x = chunkX * 16;
                int y = chunkZ * 16;
                boolean genSucceeded = false;

                for (int j1 = 0; !genSucceeded && j1 < 2 + rand.nextInt(6); ++j1) {
                    int k1 = x + rand.nextInt(16) + 8;
                    int l1 = rand.nextInt(120) + 4;
                    int i2 = y + rand.nextInt(16) + 8;
                    genSucceeded = generateCLStoneCluster(world, rand, k1, l1, i2);
                }
            }
        }
    }

    public boolean generateCLStoneCluster(World world, Random rand, int par_x, int par_y, int par_z) {
        int meta = 1 + rand.nextInt(15);

        if (!world.isAirBlock(par_x, par_y, par_z)) {
            return false;
        } else if (world.getBlock(par_x, par_y + 1, par_z) != Blocks.netherrack) {
            return false;
        } else {
            world.setBlock(par_x, par_y, par_z, CLMaterialsController.CLStone, meta, 2);

            for (int l = 0; l < 1500; ++l) {
                int i = par_x + rand.nextInt(8) - rand.nextInt(8);
                int j = par_y - rand.nextInt(12);
                int k = par_z + rand.nextInt(8) - rand.nextInt(8);

                if (world.getBlock(i, j, k).getMaterial() == Material.air) {
                    int l1 = 0;

                    for (int i2 = 0; i2 < 6; ++i2) {
                        Block block = null;

                        if (i2 == 0) {
                            block = world.getBlock(i - 1, j, k);
                        }

                        if (i2 == 1) {
                            block = world.getBlock(i + 1, j, k);
                        }

                        if (i2 == 2) {
                            block = world.getBlock(i, j - 1, k);
                        }

                        if (i2 == 3) {
                            block = world.getBlock(i, j + 1, k);
                        }

                        if (i2 == 4) {
                            block = world.getBlock(i, j, k - 1);
                        }

                        if (i2 == 5) {
                            block = world.getBlock(i, j, k + 1);
                        }

                        if (block == CLMaterialsController.CLStone) {
                            ++l1;
                        }
                    }

                    if (l1 == 1) {
                        world.setBlock(i, j, k, CLMaterialsController.CLStone, meta, 2);
                    }
                }
            }

            return true;
        }
    }
}
