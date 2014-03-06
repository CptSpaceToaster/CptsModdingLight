package kovukore.coloredlights.src.helper;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.IIcon;


public class CLRenderBlocksHelper {

	public CLRenderBlocksHelper() {
		// 
			
	}

    /**
     * Gutted to work with colors
     * CptSpaceToaster
     * 
     * 03-05-2014 heaton84 - Ported to helper method, refactored to match 1.7.2 architecture
     * 
     * @param block Block Type (Sky or Normal Block)
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param r amount of red
     * @param g amount of green
     * @param b amount of blue
     */
    public static boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks instance, Block block, int x, int y, int z, float r, float g, float b)
    {
        instance.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        
        float lc = 1.0F;
        float rc = 1.0F;
        float gc = 1.0F;
        float bc = 1.0F;
        IIcon blockIcon;
        
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (instance.getBlockIcon(block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (instance.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y - 1, z, 0))
        {
            if (instance.renderMinY <= 0.0D)
            {
                --y;
            }

            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z)&15728880;
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1)&15728880;
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1)&15728880;
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z)&15728880;
            instance.aoLightValueScratchXYNN = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPN = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            flag2 = instance.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag3 = instance.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            
            if (!flag4 && !flag2)
            {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXYNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag2)
            {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXYNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXYPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXYPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1)&15728880;
            }

            if (instance.renderMinY <= 0.0D)
            {
                ++y;
            }

            i1 = l;

            if (instance.renderMinY <= 0.0D || !instance.blockAccess.getBlock(x, y - 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
                
            }

            f7 = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            f3 = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (instance.aoLightValueScratchYZNP + f7 + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + instance.aoLightValueScratchYZNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNN + f7 + instance.aoLightValueScratchYZNN) / 4.0F;
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXYNN, instance.aoBrightnessYZNP, i1);
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXYPN, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNN, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNN, instance.aoBrightnessYZNN, i1);

            if (flag1)
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * 0.5F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * 0.5F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * 0.5F * bc;
            }
            else
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = 0.5F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = 0.5F* gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = 0.5F * bc;
            }

            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            instance.renderFaceYNeg(block, (double)x, (double)y, (double)z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 0));
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y + 1, z, 1))
        {
            if (instance.renderMaxY >= 1.0D)
            {
                ++y;
            }
            
            int lightVal = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            
            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z)&15728880;
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z)&15728880;
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1)&15728880;
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1)&15728880;
            instance.aoLightValueScratchXYNP = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPP = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            flag3 = instance.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag2 = instance.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXYNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXYPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag2)
            {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXYNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXYPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1)&15728880;
            }

            if (instance.renderMaxY >= 1.0D)
            {
                --y;
            }

            i1 = l;

            if (instance.renderMaxY >= 1.0D || !instance.blockAccess.getBlock(x, y + 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
            }

            f7 = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            f6 = (instance.aoLightValueScratchXYZNPP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (instance.aoLightValueScratchYZPP + f7 + instance.aoLightValueScratchXYZPPP + instance.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPN + f7 + instance.aoLightValueScratchYZPN) / 4.0F;
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessXYZNPP, instance.aoBrightnessXYNP, instance.aoBrightnessYZPP, i1);
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXYZPPP, instance.aoBrightnessXYPP, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPN, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, i1);
            
            instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * rc;
            instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * gc;
            instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * bc;
            
            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            instance.renderFaceYPos(block, (double)x, (double)y, (double)z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 1));
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z - 1, 2))
        {
            if (instance.renderMinZ <= 0.0D)
            {
                --z;
            }

            instance.aoLightValueScratchXZNN = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPN = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPN = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z)&15728880;
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z)&15728880;
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z)&15728880;
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z)&15728880;
            flag3 = instance.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();
            flag2 = instance.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z)&15728880;
            }

            if (!flag2 && !flag5)
            {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z)&15728880;
            }

            if (!flag3 && !flag4)
            {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z)&15728880;
            }

            if (!flag3 && !flag5)
            {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z)&15728880;
            }

            if (instance.renderMinZ <= 0.0D)
            {
                ++z;
            }

            i1 = l;

            if (instance.renderMinZ <= 0.0D || !instance.blockAccess.getBlock(x, y, z - 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
            }

            f7 = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            f3 = (instance.aoLightValueScratchXZNN + instance.aoLightValueScratchXYZNPN + f7 + instance.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXZPN + instance.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (instance.aoLightValueScratchYZNN + f7 + instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXZPN) / 4.0F;
            f6 = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXZNN + instance.aoLightValueScratchYZNN + f7) / 4.0F;
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYZPNN, instance.aoBrightnessXZPN, i1);
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXZNN, instance.aoBrightnessYZNN, i1);

            if (flag1)
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * 0.8F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * 0.8F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * 0.8F * bc;
            }
            else
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = 0.8F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = 0.8F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = 0.8F * bc;
            }

            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            blockIcon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 2);
            instance.renderFaceZNeg(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceZNeg(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z + 1, 3))
        {
            if (instance.renderMaxZ >= 1.0D)
            {
                ++z;
            }

            instance.aoLightValueScratchXZNP = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPP = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNP = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z)&15728880;
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z)&15728880;
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z)&15728880;
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z)&15728880;
            flag3 = instance.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag2 = instance.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z)&15728880;
            }

            if (!flag2 && !flag5)
            {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z)&15728880;
            }

            if (!flag3 && !flag4)
            {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z)&15728880;
            }

            if (!flag3 && !flag5)
            {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z)&15728880;
            }

            if (instance.renderMaxZ >= 1.0D)
            {
                --z;
            }

            i1 = l;

            if (instance.renderMaxZ >= 1.0D || !instance.blockAccess.getBlock(x, y, z + 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
            }

            f7 = instance.blockAccess.getBlock( x, y, z + 1).getAmbientOcclusionLightValue();
            f3 = (instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYZNPP + f7 + instance.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + instance.aoLightValueScratchYZPP + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (instance.aoLightValueScratchYZNP + f7 + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXZPP) / 4.0F;
            f4 = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchYZNP + f7) / 4.0F;
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYZNPP, instance.aoBrightnessYZPP, i1);
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXZPP, instance.aoBrightnessXYZPPP, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, instance.aoBrightnessYZNP, i1);

            if (flag1)
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * 0.8F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * 0.8F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * 0.8F * bc;
            }
            else
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = 0.8F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = 0.8F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = 0.8F * bc;
            }

            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            blockIcon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3);
            instance.renderFaceZPos(block, (double)x, (double)y, (double)z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3));

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceZPos(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }

            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x - 1, y, z, 4))
        {
            if (instance.renderMinX <= 0.0D)
            {
                --x;
            }

            instance.aoLightValueScratchXYNN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZNN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZNP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYNP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z)&15728880;
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1)&15728880;
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1)&15728880;
            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z)&15728880;
            flag3 = instance.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag2 = instance.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!flag5 && !flag2)
            {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1)&15728880;
            }

            if (!flag4 && !flag2)
            {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            }
            else
            {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            }
            else
            {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1)&15728880;
            }

            if (instance.renderMinX <= 0.0D)
            {
                ++x;
            }

            i1 = l;

            if (instance.renderMinX <= 0.0D || !instance.blockAccess.getBlock(x - 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
            }

            f7 = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            f6 = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNP + f7 + instance.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (instance.aoLightValueScratchXZNN + f7 + instance.aoLightValueScratchXYZNPN + instance.aoLightValueScratchXYNP) / 4.0F;
            f5 = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXZNN + f7) / 4.0F;
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, i1);
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPP, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessXYNP, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXYNN, instance.aoBrightnessXZNN, i1);

            if (flag1)
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * 0.6F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * 0.6F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * 0.6F * bc;
            }
            else
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = 0.6F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = 0.6F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = 0.6F * bc;
            }

            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            blockIcon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 4);
            instance.renderFaceXNeg(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceXNeg(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x + 1, y, z, 5))
        {
            if (instance.renderMaxX >= 1.0D)
            {
                ++x;
            }

            instance.aoLightValueScratchXYPN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z)&15728880;
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1)&15728880;
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1)&15728880;
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z)&15728880;
            flag3 = instance.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag2 = instance.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag5 = instance.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag4 = instance.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1)&15728880;
            }

            if (!flag2 && !flag5)
            {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1)&15728880;
            }

            if (!flag3 && !flag4)
            {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            }
            else
            {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1)&15728880;
            }

            if (!flag3 && !flag5)
            {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            }
            else
            {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1)&15728880;
            }

            if (instance.renderMaxX >= 1.0D)
            {
                --x;
            }

            i1 = l;

            if (instance.renderMaxX >= 1.0D || !instance.blockAccess.getBlock(x + 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
                if ((i1&1048320) > 0) {
                	lc = 1f-(i1 & 240)/240f;
                	rc = (i1 & 3840)/3840f;
    	            gc = (i1 & 61440)/61440f;
    	            bc = (i1 & 983040)/983040f;
    	            
    	            rc = (rc+lc>1)?1:rc+lc;
    	            gc = (gc+lc>1)?1:gc+lc;
    	            bc = (bc+lc>1)?1:bc+lc;
    	            i1 &= 15728880;
                }
            }

            f7 = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            f3 = (instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNP + f7 + instance.aoLightValueScratchXZPP) / 4.0F;
            f4 = (instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (instance.aoLightValueScratchXZPN + f7 + instance.aoLightValueScratchXYZPPN + instance.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            instance.brightnessTopLeft = instance.getAoBrightness(instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, i1);
            instance.brightnessTopRight = instance.getAoBrightness(instance.aoBrightnessXZPP, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPP, i1);
            instance.brightnessBottomRight = instance.getAoBrightness(instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, instance.aoBrightnessXYPP, i1);
            instance.brightnessBottomLeft = instance.getAoBrightness(instance.aoBrightnessXYZPNN, instance.aoBrightnessXYPN, instance.aoBrightnessXZPN, i1);

            if (flag1)
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * 0.6F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * 0.6F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * 0.6F * bc;
            }
            else
            {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = 0.6F * rc;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = 0.6F * gc;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = 0.6F * bc;
            }

            instance.colorRedTopLeft *= f3;
            instance.colorGreenTopLeft *= f3;
            instance.colorBlueTopLeft *= f3;
            instance.colorRedBottomLeft *= f4;
            instance.colorGreenBottomLeft *= f4;
            instance.colorBlueBottomLeft *= f4;
            instance.colorRedBottomRight *= f5;
            instance.colorGreenBottomRight *= f5;
            instance.colorBlueBottomRight *= f5;
            instance.colorRedTopRight *= f6;
            instance.colorGreenTopRight *= f6;
            instance.colorBlueTopRight *= f6;
            blockIcon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 5);
            instance.renderFaceXPos(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
                instance.colorRedTopLeft *= r;
                instance.colorRedBottomLeft *= r;
                instance.colorRedBottomRight *= r;
                instance.colorRedTopRight *= r;
                instance.colorGreenTopLeft *= g;
                instance.colorGreenBottomLeft *= g;
                instance.colorGreenBottomRight *= g;
                instance.colorGreenTopRight *= g;
                instance.colorBlueTopLeft *= b;
                instance.colorBlueBottomLeft *= b;
                instance.colorBlueBottomRight *= b;
                instance.colorBlueTopRight *= b;
                instance.renderFaceXPos(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }

            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        instance.enableAO = false;
        return flag;
    }
	
    /**
     * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b
     *
     * Accepts and tints blocks according to their colored light value 
     * CptSpaceToaster
     * 
     * 03-05-2014 heaton84 - Ported to helper method, refactored to match 1.7.2 architecture
     */
    public static boolean renderStandardBlockWithColorMultiplier(RenderBlocks instance, Block par1Block, int par2X, int par3Y, int par4Z, float par5R, float par6G, float par7B)
    {
        instance.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * par5R;
        float f8 = f4 * par6G;
        float f9 = f4 * par7B;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;
        IIcon blockIcon;

        if (par1Block != Blocks.grass)
        {
            f10 = f3 * par5R;
            f11 = f5 * par5R;
            f12 = f6 * par5R;
            f13 = f3 * par6G;
            f14 = f5 * par6G;
            f15 = f6 * par6G;
            f16 = f3 * par7B;
            f17 = f5 * par7B;
            f18 = f6 * par7B;
        }

        int l = par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X, par3Y, par4Z);

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y - 1, par4Z, 0))
        {
        	int i = instance.renderMinY > 0.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X, par3Y - 1, par4Z);
        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
             
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
            
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f10*rc, f13*gc, f16*bc);
            instance.renderFaceYNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 0));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y + 1, par4Z, 1))
        {
        	int i = instance.renderMaxY < 1.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X, par3Y + 1, par4Z);
            		
        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
             
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
            
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f7*rc, f8*gc, f9*bc);
            instance.renderFaceYPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 1));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z - 1, 2))
        {
        	int i = instance.renderMinZ > 0.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X, par3Y, par4Z - 1);
            
        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
             
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
            
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11*rc, f14*gc, f17*bc);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 2);
            instance.renderFaceZNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f11 * par5R*rc, f14 * par6G*gc, f17 * par7B*bc);
                instance.renderFaceZNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z + 1, 3))
        {
        	int i = instance.renderMaxZ < 1.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X, par3Y, par4Z + 1);
            

        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
             
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
            
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11*rc, f14*gc, f17*bc);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 3);
            instance.renderFaceZPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f11 * par5R*rc, f14 * par6G*gc, f17 * par7B*bc);
                instance.renderFaceZPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X - 1, par3Y, par4Z, 4))
        {
        	int i = instance.renderMinX > 0.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X - 1, par3Y, par4Z);
            
            
        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
             
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
            
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12*rc, f15*gc, f18*bc);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 4);
            instance.renderFaceXNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f12 * par5R*rc, f15 * par6G*gc, f18 * par7B*bc);
                instance.renderFaceXNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X + 1, par3Y, par4Z, 5))
        {
        	int i = instance.renderMaxX < 1.0D ? l : par1Block.getMixedBrightnessForBlock(instance.blockAccess, par2X + 1, par3Y, par4Z);
             
             
        	float rc = 1;
            float gc = 1;
            float bc = 1;
            float lc = 1;
              
            if ((i & 1048320) > 0) {
            	lc = 1f-(i & 240)/240f;
	            rc = (i & 3840)/3840f;
	            gc = (i & 61440)/61440f;
	            bc = (i & 983040)/983040f;
	            
	            rc = (rc+lc>1)?1:rc+lc;
	            gc = (gc+lc>1)?1:gc+lc;
	            bc = (bc+lc>1)?1:bc+lc;
        	}
             
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12*rc, f15*gc, f18*bc);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 5);
            instance.renderFaceXPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture())
            {
            	 tessellator.setColorOpaque_F(f12 * par5R*rc, f15 * par6G*gc, f18 * par7B*bc);
                instance.renderFaceXPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }
    
    
}
