package kovukore.asm.overriddenclasses;

import kovukore.asm.transformer.ASMAddMethod;
import kovukore.asm.transformer.ASMReplaceMethod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class Lights_RenderBlocks extends RenderBlocks
{
	@ASMReplaceMethod
	public boolean a(Block par1Block, int x, int y, int z, float r, float g, float b)
	{
		this.enableAO = true;
		boolean flag = false;
		float f3 = 0.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		float lc = 1.0F;
		float rc = 1.0F;
		float gc = 1.0F;
		float bc = 1.0F;
		boolean flag1 = true;
		int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z);
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(983055);
		if (this.getBlockIcon(par1Block).getIconName().equals("grass_top"))
		{
			flag1 = false;
		}
		else if (this.hasOverrideBlockTexture())
		{
			flag1 = false;
		}
		boolean flag2;
		boolean flag3;
		boolean flag4;
		boolean flag5;
		float f7;
		int i1;
		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0))
		{
			if (this.renderMinY <= 0.0D)
			{
				--y;
			}
			this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z) & 15728880;
			this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1) & 15728880;
			this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1) & 15728880;
			this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z) & 15728880;
			this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z);
			this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z - 1);
			this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z + 1);
			this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z);
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y - 1, z)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y - 1, z)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y - 1, z + 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y - 1, z - 1)];
			if (!flag4 && !flag2)
			{
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
			}
			else
			{
				this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z - 1);
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z - 1) & 15728880;
			}

			if (!flag5 && !flag2)
			{
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
				this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
			}
			else
			{
				this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z + 1);
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z + 1) & 15728880;
			}

			if (!flag4 && !flag3)
			{
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
			}
			else
			{
				this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z - 1);
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z - 1) & 15728880;
			}

			if (!flag5 && !flag3)
			{
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
				this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
			}
			else
			{
				this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z + 1);
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z + 1) & 15728880;
			}
			if (this.renderMinY <= 0.0D)
			{
				++y;
			}
			i1 = l;
			if (this.renderMinY <= 0.0D || !this.blockAccess.isBlockOpaqueCube(x, y - 1, z))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;

					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}

			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z);
			f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
			f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
			f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
			f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);
			if (flag1)
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * 0.5F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * 0.5F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * 0.5F * bc;
			}
			else
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F * bc;
			}
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			this.renderFaceYNeg(par1Block, (double) x, (double) y, (double) z, this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 0));
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}
		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1))
		{
			if (this.renderMaxY >= 1.0D)
			{
				++y;
			}
			int lightVal = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
			this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z) & 15728880;
			this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z) & 15728880;
			this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1) & 15728880;
			this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1) & 15728880;
			this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z);
			this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z);
			this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z - 1);
			this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z + 1);
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y + 1, z)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y + 1, z)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y + 1, z + 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y + 1, z - 1)];
			if (!flag4 && !flag2)
			{
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
				this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
			}
			else
			{
				this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z - 1);
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z - 1) & 15728880;
			}

			if (!flag4 && !flag3)
			{
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
				this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
			}
			else
			{
				this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z - 1);
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z - 1) & 15728880;
			}

			if (!flag5 && !flag2)
			{
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
			}
			else
			{
				this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z + 1);
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z + 1) & 15728880;
			}

			if (!flag5 && !flag3)
			{
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
			}
			else
			{
				this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z + 1);
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z + 1) & 15728880;
			}
			if (this.renderMaxY >= 1.0D)
			{
				--y;
			}
			i1 = l;
			if (this.renderMaxY >= 1.0D || !this.blockAccess.isBlockOpaqueCube(x, y + 1, z))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;
					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}
			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z);
			f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
			f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
			f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
			f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * rc;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * gc;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * bc;
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			this.renderFaceYPos(par1Block, (double) x, (double) y, (double) z, this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 1));
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}

		Icon icon;

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y, z - 1, 2))
		{
			if (this.renderMinZ <= 0.0D)
			{
				--z;
			}

			this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z);
			this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z);
			this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z);
			this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z);
			this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z) & 15728880;
			this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z) & 15728880;
			this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z) & 15728880;
			this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z) & 15728880;
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y, z - 1)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y, z - 1)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y + 1, z - 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y - 1, z - 1)];

			if (!flag2 && !flag4)
			{
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
			}
			else
			{
				this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y - 1, z);
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y - 1, z) & 15728880;
			}

			if (!flag2 && !flag5)
			{
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
			}
			else
			{
				this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y + 1, z);
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y + 1, z) & 15728880;
			}

			if (!flag3 && !flag4)
			{
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
			}
			else
			{
				this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y - 1, z);
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y - 1, z) & 15728880;
			}

			if (!flag3 && !flag5)
			{
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
			}
			else
			{
				this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y + 1, z);
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y + 1, z) & 15728880;
			}

			if (this.renderMinZ <= 0.0D)
			{
				++z;
			}

			i1 = l;

			if (this.renderMinZ <= 0.0D || !this.blockAccess.isBlockOpaqueCube(x, y, z - 1))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;

					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}
			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z - 1);
			f3 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
			f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
			f5 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
			f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);
			if (flag1)
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * 0.8F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * 0.8F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * 0.8F * bc;
			}
			else
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F * bc;
			}
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 2);
			this.renderFaceZNeg(par1Block, (double) x, (double) y, (double) z, icon);
			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				this.colorRedTopLeft *= r;
				this.colorRedBottomLeft *= r;
				this.colorRedBottomRight *= r;
				this.colorRedTopRight *= r;
				this.colorGreenTopLeft *= g;
				this.colorGreenBottomLeft *= g;
				this.colorGreenBottomRight *= g;
				this.colorGreenTopRight *= g;
				this.colorBlueTopLeft *= b;
				this.colorBlueBottomLeft *= b;
				this.colorBlueBottomRight *= b;
				this.colorBlueTopRight *= b;
				this.renderFaceZNeg(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}
		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y, z + 1, 3))
		{
			if (this.renderMaxZ >= 1.0D)
			{
				++z;
			}
			this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z);
			this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z);
			this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z);
			this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z);
			this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z) & 15728880;
			this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z) & 15728880;
			this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z) & 15728880;
			this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z) & 15728880;
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y, z + 1)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y, z + 1)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y + 1, z + 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x, y - 1, z + 1)];
			if (!flag2 && !flag4)
			{
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
			}
			else
			{
				this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y - 1, z);
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y - 1, z) & 15728880;
			}
			if (!flag2 && !flag5)
			{
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
			}
			else
			{
				this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y + 1, z);
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y + 1, z) & 15728880;
			}

			if (!flag3 && !flag4)
			{
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
			}
			else
			{
				this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y - 1, z);
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y - 1, z) & 15728880;
			}

			if (!flag3 && !flag5)
			{
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
			}
			else
			{
				this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y + 1, z);
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y + 1, z) & 15728880;
			}
			if (this.renderMaxZ >= 1.0D)
			{
				--z;
			}
			i1 = l;
			if (this.renderMaxZ >= 1.0D || !this.blockAccess.isBlockOpaqueCube(x, y, z + 1))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;
					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}
			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z + 1);
			f3 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
			f6 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
			f5 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
			f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);
			if (flag1)
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * 0.8F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * 0.8F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * 0.8F * bc;
			}
			else
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F * bc;
			}
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 3);
			this.renderFaceZPos(par1Block, (double) x, (double) y, (double) z, this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 3));
			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				this.colorRedTopLeft *= r;
				this.colorRedBottomLeft *= r;
				this.colorRedBottomRight *= r;
				this.colorRedTopRight *= r;
				this.colorGreenTopLeft *= g;
				this.colorGreenBottomLeft *= g;
				this.colorGreenBottomRight *= g;
				this.colorGreenTopRight *= g;
				this.colorBlueTopLeft *= b;
				this.colorBlueBottomLeft *= b;
				this.colorBlueBottomRight *= b;
				this.colorBlueTopRight *= b;
				this.renderFaceZPos(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}
		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x - 1, y, z, 4))
		{
			if (this.renderMinX <= 0.0D)
			{
				--x;
			}
			this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z);
			this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z - 1);
			this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z + 1);
			this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z);
			this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z) & 15728880;
			this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1) & 15728880;
			this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1) & 15728880;
			this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z) & 15728880;
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y + 1, z)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y - 1, z)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y, z - 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x - 1, y, z + 1)];
			if (!flag5 && !flag2)
			{
				this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
			}
			else
			{
				this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z - 1);
				this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z - 1) & 15728880;
			}

			if (!flag4 && !flag2)
			{
				this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
			}
			else
			{
				this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z + 1);
				this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z + 1) & 15728880;
			}

			if (!flag5 && !flag3)
			{
				this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
				this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
			}
			else
			{
				this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z - 1);
				this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z - 1) & 15728880;
			}

			if (!flag4 && !flag3)
			{
				this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
				this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
			}
			else
			{
				this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z + 1);
				this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z + 1) & 15728880;
			}
			if (this.renderMinX <= 0.0D)
			{
				++x;
			}
			i1 = l;
			if (this.renderMinX <= 0.0D || !this.blockAccess.isBlockOpaqueCube(x - 1, y, z))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z) & 15728880;
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;
					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}
			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x - 1, y, z);
			f6 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
			f3 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
			f4 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
			f5 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);
			if (flag1)
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * 0.6F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * 0.6F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * 0.6F * bc;
			}
			else
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F * bc;
			}
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 4);
			this.renderFaceXNeg(par1Block, (double) x, (double) y, (double) z, icon);
			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				this.colorRedTopLeft *= r;
				this.colorRedBottomLeft *= r;
				this.colorRedBottomRight *= r;
				this.colorRedTopRight *= r;
				this.colorGreenTopLeft *= g;
				this.colorGreenBottomLeft *= g;
				this.colorGreenBottomRight *= g;
				this.colorGreenTopRight *= g;
				this.colorBlueTopLeft *= b;
				this.colorBlueBottomLeft *= b;
				this.colorBlueBottomRight *= b;
				this.colorBlueTopRight *= b;
				this.renderFaceXNeg(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}
		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x + 1, y, z, 5))
		{
			if (this.renderMaxX >= 1.0D)
			{
				++x;
			}
			this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z);
			this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z - 1);
			this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y, z + 1);
			this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z);
			this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z) & 15728880;
			this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1) & 15728880;
			this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1) & 15728880;
			this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z) & 15728880;
			flag3 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y + 1, z)];
			flag2 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y - 1, z)];
			flag5 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y, z + 1)];
			flag4 = Block.canBlockGrass[this.blockAccess.getBlockId(x + 1, y, z - 1)];
			if (!flag2 && !flag4)
			{
				this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
			}
			else
			{
				this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z - 1);
				this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z - 1) & 15728880;
			}

			if (!flag2 && !flag5)
			{
				this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
			}
			else
			{
				this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y - 1, z + 1);
				this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z + 1) & 15728880;
			}

			if (!flag3 && !flag4)
			{
				this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
				this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
			}
			else
			{
				this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z - 1);
				this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z - 1) & 15728880;
			}

			if (!flag3 && !flag5)
			{
				this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
				this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
			}
			else
			{
				this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x, y + 1, z + 1);
				this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z + 1) & 15728880;
			}
			if (this.renderMaxX >= 1.0D)
			{
				--x;
			}
			i1 = l;
			if (this.renderMaxX >= 1.0D || !this.blockAccess.isBlockOpaqueCube(x + 1, y, z))
			{
				i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
				if ((i1 & 1048320) > 0)
				{
					lc = 1f - (i1 & 240) / 240f;
					rc = (i1 & 3840) / 3840f;
					gc = (i1 & 61440) / 61440f;
					bc = (i1 & 983040) / 983040f;
					rc = (rc + lc > 1) ? 1 : rc + lc;
					gc = (gc + lc > 1) ? 1 : gc + lc;
					bc = (bc + lc > 1) ? 1 : bc + lc;
					i1 &= 15728880;
				}
			}
			f7 = par1Block.getAmbientOcclusionLightValue(this.blockAccess, x + 1, y, z);
			f3 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
			f4 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
			f5 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
			f6 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
			this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
			this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
			this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
			this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);
			if (flag1)
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = r * 0.6F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = g * 0.6F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = b * 0.6F * bc;
			}
			else
			{
				this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F * rc;
				this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F * gc;
				this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F * bc;
			}
			this.colorRedTopLeft *= f3;
			this.colorGreenTopLeft *= f3;
			this.colorBlueTopLeft *= f3;
			this.colorRedBottomLeft *= f4;
			this.colorGreenBottomLeft *= f4;
			this.colorBlueBottomLeft *= f4;
			this.colorRedBottomRight *= f5;
			this.colorGreenBottomRight *= f5;
			this.colorBlueBottomRight *= f5;
			this.colorRedTopRight *= f6;
			this.colorGreenTopRight *= f6;
			this.colorBlueTopRight *= f6;
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 5);
			this.renderFaceXPos(par1Block, (double) x, (double) y, (double) z, icon);
			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				this.colorRedTopLeft *= r;
				this.colorRedBottomLeft *= r;
				this.colorRedBottomRight *= r;
				this.colorRedTopRight *= r;
				this.colorGreenTopLeft *= g;
				this.colorGreenBottomLeft *= g;
				this.colorGreenBottomRight *= g;
				this.colorGreenTopRight *= g;
				this.colorBlueTopLeft *= b;
				this.colorBlueBottomLeft *= b;
				this.colorBlueBottomRight *= b;
				this.colorBlueTopRight *= b;
				this.renderFaceXPos(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}
			lc = 1.0F;
			rc = 1.0F;
			gc = 1.0F;
			bc = 1.0F;
			flag = true;
		}

		this.enableAO = false;
		return flag;
	}

	@ASMReplaceMethod
	public boolean d(Block par1Block, int x, int y, int z, float r, float g, float b)
	{
		this.enableAO = false;
		Tessellator tessellator = Tessellator.instance;
		boolean flag = false;
		float f3 = 0.5F;
		float f4 = 1.0F;
		float f5 = 0.8F;
		float f6 = 0.6F;
		float f7 = f4 * r;
		float f8 = f4 * g;
		float f9 = f4 * b;
		float f10 = f3;
		float f11 = f5;
		float f12 = f6;
		float f13 = f3;
		float f14 = f5;
		float f15 = f6;
		float f16 = f3;
		float f17 = f5;
		float f18 = f6;

		if (par1Block != Block.grass)
		{
			f10 = f3 * r;
			f11 = f5 * r;
			f12 = f6 * r;
			f13 = f3 * g;
			f14 = f5 * g;
			f15 = f6 * g;
			f16 = f3 * b;
			f17 = f5 * b;
			f18 = f6 * b;
		}

		int l = par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z);

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0))
		{
			int i = this.renderMinY > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f10 * rc, f13 * gc, f16 * bc);
			this.renderFaceYNeg(par1Block, (double) x, (double) y, (double) z, this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 0));
			flag = true;
		}

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1))
		{
			int i = this.renderMaxY < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);

			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f7 * rc, f8 * gc, f9 * bc);
			this.renderFaceYPos(par1Block, (double) x, (double) y, (double) z, this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 1));
			flag = true;
		}

		Icon icon;

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y, z - 1, 2))
		{
			int i = this.renderMinZ > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);

			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f11 * rc, f14 * gc, f17 * bc);
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 2);
			this.renderFaceZNeg(par1Block, (double) x, (double) y, (double) z, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				tessellator.setColorOpaque_F(f11 * r * rc, f14 * g * gc, f17 * b * bc);
				this.renderFaceZNeg(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x, y, z + 1, 3))
		{
			int i = this.renderMaxZ < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);

			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f11 * rc, f14 * gc, f17 * bc);
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 3);
			this.renderFaceZPos(par1Block, (double) x, (double) y, (double) z, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				tessellator.setColorOpaque_F(f11 * r * rc, f14 * g * gc, f17 * b * bc);
				this.renderFaceZPos(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x - 1, y, z, 4))
		{
			int i = this.renderMinX > 0.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);

			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f12 * rc, f15 * gc, f18 * bc);
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 4);
			this.renderFaceXNeg(par1Block, (double) x, (double) y, (double) z, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				tessellator.setColorOpaque_F(f12 * r * rc, f15 * g * gc, f18 * b * bc);
				this.renderFaceXNeg(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		if (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, x + 1, y, z, 5))
		{
			int i = this.renderMaxX < 1.0D ? l : par1Block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);

			float rc = 1;
			float gc = 1;
			float bc = 1;
			float lc = 1;

			if ((i & 1048320) > 0)
			{
				lc = 1f - (i & 240) / 240f;
				rc = (i & 3840) / 3840f;
				gc = (i & 61440) / 61440f;
				bc = (i & 983040) / 983040f;

				rc = (rc + lc > 1) ? 1 : rc + lc;
				gc = (gc + lc > 1) ? 1 : gc + lc;
				bc = (bc + lc > 1) ? 1 : bc + lc;
			}

			tessellator.setBrightness(i);
			tessellator.setColorOpaque_F(f12 * rc, f15 * gc, f18 * bc);
			icon = this.getBlockIcon(par1Block, this.blockAccess, x, y, z, 5);
			this.renderFaceXPos(par1Block, (double) x, (double) y, (double) z, icon);

			if (fancyGrass && icon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
			{
				tessellator.setColorOpaque_F(f12 * r * rc, f15 * g * gc, f18 * b * bc);
				this.renderFaceXPos(par1Block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
			}

			flag = true;
		}

		return flag;
	}
}
