package kovukore.asm.overriddenclasses;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.WorldSpecificSaveHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kovukore.asm.transformer.ASMAddField;
import kovukore.asm.transformer.ASMAddMethod;
import kovukore.asm.transformer.ASMReplaceMethod;

/*
 * XXX UPDATE THESE FIELD NAMES AND METHOD NAMES/SIGS ON EACH NEW VERSION OF MINECRAFT
 */
public abstract class Lights_World extends World
{
	@ASMAddField
	private long[] lightUpdateBlockList;

	public Lights_World(ISaveHandler par1ISaveHandler, String par2Str, WorldSettings par3WorldSettings, WorldProvider par4WorldProvider, Profiler par5Profiler, ILogAgent par6ILogAgent)
	{
		super(par1ISaveHandler, par2Str, par3WorldSettings, par4WorldProvider, par5Profiler, par6ILogAgent);
		lightUpdateBlockList = new long[32768];
	}

	@ASMReplaceMethod
	public int a(int par1, int par2, int par3, boolean par4)
	{
		if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
		{
			if (par4)
			{
				int l = this.getBlockId(par1, par2, par3);
				if (Block.useNeighborBrightness[l])
				{
					int i1 = this.getBlockLightValue_do(par1, par2 + 1, par3, false);
					int j1 = this.getBlockLightValue_do(par1 + 1, par2, par3, false);
					int k1 = this.getBlockLightValue_do(par1 - 1, par2, par3, false);
					int l1 = this.getBlockLightValue_do(par1, par2, par3 + 1, false);
					int i2 = this.getBlockLightValue_do(par1, par2, par3 - 1, false);
					if ((j1 & 15) > (i1 & 15))
					{
						i1 = j1;
					}
					if ((k1 & 15) > (i1 & 15))
					{
						i1 = k1;
					}
					if ((l1 & 15) > (i1 & 15))
					{
						i1 = l1;
					}
					if ((i2 & 15) > (i1 & 15))
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
			else
			{
				if (par2 >= 256)
				{
					par2 = 255;
				}
				Chunk chunk = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
				par1 &= 15;
				par3 &= 15;
				return chunk.getBlockLightValue(par1, par2, par3, this.skylightSubtracted);
			}
		}
		else
		{
			return 15;
		}
	}

	@ASMReplaceMethod
	public float q(int par1, int par2, int par3)
	{
		return this.provider.lightBrightnessTable[this.getBlockLightValue(par1, par2, par3) & 15];
	}

	@ASMReplaceMethod
	private int a(int x, int y, int z, EnumSkyBlock par4EnumSkyBlock)
	{
		if (par4EnumSkyBlock == EnumSkyBlock.Sky && this.canBlockSeeTheSky(x, y, z))
		{
			return 15;
		}
		else
		{
			int l = this.getBlockId(x, y, z);
			Block block = Block.blocksList[l];
			int blockLight = (block == null ? 0 : block.getLightValue(this, x, y, z));
			int currentLight = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
			int opacity = (block == null ? 0 : block.getLightOpacity(this, x, y, z));

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
			else
			{
				for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
				{
					int l1 = x + Facing.offsetsXForSide[faceIndex];
					int i2 = y + Facing.offsetsYForSide[faceIndex];
					int j2 = z + Facing.offsetsZForSide[faceIndex];

					int neighboorLight = this.getSavedLightValue(par4EnumSkyBlock, l1, i2, j2);
					int ll = neighboorLight & 15;
					int rl = neighboorLight & 480;
					int gl = neighboorLight & 15360;
					int bl = neighboorLight & 491520;

					ll -= opacity;
					rl -= 32 * opacity;
					gl -= 1024 * opacity;
					bl -= 32768 * opacity;

					if (ll > (currentLight & 15))
					{
						currentLight = (currentLight & 507360) | ll;
					}
					if (rl > (currentLight & 480))
					{
						currentLight = (currentLight & 506895) | rl;
					}
					if (gl > (currentLight & 15360))
					{
						currentLight = (currentLight & 492015) | gl;
					}
					if (bl > (currentLight & 491520))
					{
						currentLight = (currentLight & 15855) | bl;
					}
				}

				return currentLight;
			}
		}
	}

	@ASMReplaceMethod
	public void c(EnumSkyBlock par1Enu, int x, int y, int z)
	{
		if (this.doChunksNearChunkExist(x, y, z, 17))
		{
			int l = 0;
			int i1 = 0;
			this.theProfiler.startSection("getBrightness");
			int savedLightValue = this.getSavedLightValue(par1Enu, x, y, z);
			int compLightValue = this.a(x, y, z, par1Enu);
			long l1;
			int x1;
			int y1;
			int z1;
			int lightEntry;
			int expectedEntryLight;
			int x2;
			int z2;
			int y2;

			if (compLightValue > savedLightValue)
			{
				this.lightUpdateBlockList[i1++] = 133152; // Save Entry at pos 0 (move i1)
			}
			else if (compLightValue < savedLightValue)
			{
				this.lightUpdateBlockList[i1++] = 133152 | savedLightValue << 18; // Save Entry at pos 0 with its Light Value (move i1)

				while (l < i1)
				{
					l1 = this.lightUpdateBlockList[l++]; // Get Entry at l, which starts at 0
					x1 = ((int) (l1 & 63) - 32 + x); // Get Entry X coord
					y1 = ((int) (l1 >> 6 & 63) - 32 + y); // Get Entry Y coord
					z1 = ((int) (l1 >> 12 & 63) - 32 + z); // Get Entry Z coord
					lightEntry = (int) (l1 >>> 18) & 507375; // Get Entry's saved Light
					expectedEntryLight = this.getSavedLightValue(par1Enu, x1, y1, z1); // Get the saved Light Level at the entry's location

					if ((expectedEntryLight & 15) >= (lightEntry & 15))
					{
						this.setLightValue(par1Enu, x1, y1, z1, 0);

						if (lightEntry > 0)
						{
							x2 = MathHelper.abs_int(x1 - x);
							y2 = MathHelper.abs_int(y1 - y);
							z2 = MathHelper.abs_int(z1 - z);

							if (x2 + y2 + z2 < 17)
							{
								for (int faceIndex = 0; faceIndex < 6; ++faceIndex)
								{
									int xFace = x1 + Facing.offsetsXForSide[faceIndex];
									int yFace = y1 + Facing.offsetsYForSide[faceIndex];
									int zFace = z1 + Facing.offsetsZForSide[faceIndex];
									Block block = Block.blocksList[getBlockId(xFace, yFace, zFace)];
									int blockOpacity = (block == null ? 0 : block.getLightOpacity(this, xFace, yFace, zFace));
									int opacity = Math.max(1, blockOpacity);
									// Get Saved light value from face
									expectedEntryLight = this.getSavedLightValue(par1Enu, xFace, yFace, zFace);
									int ll = lightEntry & 15;
									int rl = lightEntry & 480;
									int gl = lightEntry & 15360;
									int bl = lightEntry & 491520;

									ll -= opacity;
									rl -= 32 * opacity;
									gl -= 1024 * opacity;
									bl -= 32768 * opacity;

									if (((expectedEntryLight & 15) >= ll) && (i1 < this.lightUpdateBlockList.length))
										this.lightUpdateBlockList[i1++] = xFace - x + 32 | (yFace - y + 32 << 6) | (zFace - z + 32 << 12) | ((ll | rl | gl | bl) << 18);
								}
							}
						}
					}
				}
				// reset l, so we can loop through all of the updates again!
				l = 0;
			}

			this.theProfiler.endSection();
			this.theProfiler.startSection("checkedPosition < toCheckCount");

			while (l < i1)
			{
				l1 = this.lightUpdateBlockList[l++]; // Get Entry and it's light value (if there is one)
				x1 = ((int) (l1 & 63) - 32 + x); // Get Entry X coord
				y1 = ((int) (l1 >> 6 & 63) - 32 + y); // Get Entry Y coord
				z1 = ((int) (l1 >> 12 & 63) - 32 + z); // Get Entry Z coord

				// Get the Saved Light at the Entry's Position
				lightEntry = this.getSavedLightValue(par1Enu, x1, y1, z1);

				// Compute the light level at the entry's location. If the light's have been set to zero before this occurs,
				// then the computation will change dynamically
				expectedEntryLight = this.a(x1, y1, z1, par1Enu);

				if (expectedEntryLight != lightEntry)
				{
					int tempStorageLightValue = lightEntry;

					if ((expectedEntryLight & 15) > (lightEntry & 15))
						tempStorageLightValue = tempStorageLightValue & 507360 | expectedEntryLight & 15;
					if ((expectedEntryLight & 480) > (lightEntry & 480))
						tempStorageLightValue = tempStorageLightValue & 506895 | expectedEntryLight & 480;
					if ((expectedEntryLight & 15360) > (lightEntry & 15360))
						tempStorageLightValue = tempStorageLightValue & 492015 | expectedEntryLight & 15360;
					if ((expectedEntryLight & 491520) > (lightEntry & 491520))
						tempStorageLightValue = tempStorageLightValue & 15855 | expectedEntryLight & 491520;

					if ((((1048576 | lightEntry) - expectedEntryLight) & 541200) > 0)
					{
						// Moved this here, from the lines above
						this.setLightValue(par1Enu, x1, y1, z1, tempStorageLightValue);

						x2 = Math.abs(x1 - x);
						y2 = Math.abs(y1 - y);
						z2 = Math.abs(z1 - z);
						boolean flag = i1 < this.lightUpdateBlockList.length - 6; // What's with the minus 6? 6 Sides on cube?

						if (x2 + y2 + z2 < 17 && flag)
						{
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1 - 1, y1, z1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 - 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1 + 1, y1, z1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 + 1 - x + 32 + (y1 - y + 32 << 6) + (z1 - z + 32 << 12);
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1, y1 - 1, z1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - 1 - y + 32 << 6) + (z1 - z + 32 << 12);
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1, y1 + 1, z1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 + 1 - y + 32 << 6) + (z1 - z + 32 << 12);
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1, y1, z1 - 1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 - 1 - z + 32 << 12);
							if ((((1048576 | this.getSavedLightValue(par1Enu, x1, y1, z1 + 1)) - expectedEntryLight) & 541200) > 0)
								this.lightUpdateBlockList[i1++] = x1 - x + 32 + (y1 - y + 32 << 6) + (z1 + 1 - z + 32 << 12);
						}
					}
				}
			}

			this.theProfiler.endSection();
		}
	}
}
