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
    public static boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks renderBlocks, Block block, int x, int y, int z, float r, float g, float b)
    {
        renderBlocks.enableAO = true;
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
        int l = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (renderBlocks.getBlockIcon(block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (renderBlocks.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, 0))
        {
            if (renderBlocks.renderMinY <= 0.0D)
            {
                --y;
            }

            renderBlocks.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z)&15728880;
            renderBlocks.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1)&15728880;
            renderBlocks.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1)&15728880;
            renderBlocks.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z)&15728880;
            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            flag2 = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag3 = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            
            if (!flag4 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXYNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXYNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z + 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXYPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXYPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z + 1)&15728880;
            }

            if (renderBlocks.renderMinY <= 0.0D)
            {
                ++y;
            }

            i1 = l;

            if (renderBlocks.renderMinY <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y - 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z);
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

            f7 = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            f3 = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (renderBlocks.aoLightValueScratchYZNP + f7 + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + f7 + renderBlocks.aoLightValueScratchYZNN) / 4.0F;
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, i1);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNN, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessYZNN, i1);

            if (flag1)
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * 0.5F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * 0.5F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * 0.5F * bc;
            }
            else
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = 0.5F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = 0.5F* gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = 0.5F * bc;
            }

            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            renderBlocks.renderFaceYNeg(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 0));
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, 1))
        {
            if (renderBlocks.renderMaxY >= 1.0D)
            {
                ++y;
            }
            
            int lightVal = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
            
            renderBlocks.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z)&15728880;
            renderBlocks.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z)&15728880;
            renderBlocks.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1)&15728880;
            renderBlocks.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1)&15728880;
            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            flag3 = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag2 = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXYNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z - 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXYPP;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXYPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z - 1)&15728880;
            }

            if (!flag5 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXYNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z + 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXYPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXYPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z + 1)&15728880;
            }

            if (renderBlocks.renderMaxY >= 1.0D)
            {
                --y;
            }

            i1 = l;

            if (renderBlocks.renderMaxY >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y + 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z);
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

            f7 = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            f6 = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (renderBlocks.aoLightValueScratchYZPP + f7 + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + f7 + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, i1);
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXYPP, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPN, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, i1);
            
            renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * rc;
            renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * gc;
            renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * bc;
            
            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            renderBlocks.renderFaceYPos(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 1));
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, 2))
        {
            if (renderBlocks.renderMinZ <= 0.0D)
            {
                --z;
            }

            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z)&15728880;
            renderBlocks.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z)&15728880;
            renderBlocks.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z)&15728880;
            renderBlocks.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z)&15728880;
            flag3 = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();
            flag2 = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, z)&15728880;
            }

            if (!flag2 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, z)&15728880;
            }

            if (!flag3 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, z)&15728880;
            }

            if (!flag3 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, z)&15728880;
            }

            if (renderBlocks.renderMinZ <= 0.0D)
            {
                ++z;
            }

            i1 = l;

            if (renderBlocks.renderMinZ <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y, z - 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1);
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

            f7 = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            f3 = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + f7 + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderBlocks.aoLightValueScratchYZNN + f7 + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
            f6 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + f7) / 4.0F;
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXZPN, i1);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, i1);

            if (flag1)
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * 0.8F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * 0.8F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * 0.8F * bc;
            }
            else
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = 0.8F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = 0.8F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = 0.8F * bc;
            }

            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            blockIcon = renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 2);
            renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                renderBlocks.colorRedTopLeft *= r;
                renderBlocks.colorRedBottomLeft *= r;
                renderBlocks.colorRedBottomRight *= r;
                renderBlocks.colorRedTopRight *= r;
                renderBlocks.colorGreenTopLeft *= g;
                renderBlocks.colorGreenBottomLeft *= g;
                renderBlocks.colorGreenBottomRight *= g;
                renderBlocks.colorGreenTopRight *= g;
                renderBlocks.colorBlueTopLeft *= b;
                renderBlocks.colorBlueBottomLeft *= b;
                renderBlocks.colorBlueBottomRight *= b;
                renderBlocks.colorBlueTopRight *= b;
                renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, 3))
        {
            if (renderBlocks.renderMaxZ >= 1.0D)
            {
                ++z;
            }

            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z)&15728880;
            renderBlocks.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z)&15728880;
            renderBlocks.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z)&15728880;
            renderBlocks.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z)&15728880;
            flag3 = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag2 = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, z)&15728880;
            }

            if (!flag2 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, z)&15728880;
            }

            if (!flag3 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, z)&15728880;
            }

            if (!flag3 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, z)&15728880;
            }

            if (renderBlocks.renderMaxZ >= 1.0D)
            {
                --z;
            }

            i1 = l;

            if (renderBlocks.renderMaxZ >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y, z + 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1);
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

            f7 = renderBlocks.blockAccess.getBlock( x, y, z + 1).getAmbientOcclusionLightValue();
            f3 = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + f7 + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (renderBlocks.aoLightValueScratchYZNP + f7 + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            f4 = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + f7) / 4.0F;
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessYZPP, i1);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYZPPP, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZNP, i1);

            if (flag1)
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * 0.8F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * 0.8F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * 0.8F * bc;
            }
            else
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = 0.8F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = 0.8F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = 0.8F * bc;
            }

            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            blockIcon = renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 3);
            renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 3));

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                renderBlocks.colorRedTopLeft *= r;
                renderBlocks.colorRedBottomLeft *= r;
                renderBlocks.colorRedBottomRight *= r;
                renderBlocks.colorRedTopRight *= r;
                renderBlocks.colorGreenTopLeft *= g;
                renderBlocks.colorGreenBottomLeft *= g;
                renderBlocks.colorGreenBottomRight *= g;
                renderBlocks.colorGreenTopRight *= g;
                renderBlocks.colorBlueTopLeft *= b;
                renderBlocks.colorBlueBottomLeft *= b;
                renderBlocks.colorBlueBottomRight *= b;
                renderBlocks.colorBlueTopRight *= b;
                renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }

            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, 4))
        {
            if (renderBlocks.renderMinX <= 0.0D)
            {
                --x;
            }

            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z)&15728880;
            renderBlocks.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1)&15728880;
            renderBlocks.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1)&15728880;
            renderBlocks.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z)&15728880;
            flag3 = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag2 = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!flag5 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z - 1)&15728880;
            }

            if (!flag4 && !flag2)
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z + 1)&15728880;
            }

            if (!flag5 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z - 1)&15728880;
            }

            if (!flag4 && !flag3)
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z + 1)&15728880;
            }

            if (renderBlocks.renderMinX <= 0.0D)
            {
                ++x;
            }

            i1 = l;

            if (renderBlocks.renderMinX <= 0.0D || !renderBlocks.blockAccess.getBlock(x - 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, z);
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

            f7 = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            f6 = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + f7 + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (renderBlocks.aoLightValueScratchXZNN + f7 + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
            f5 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + f7) / 4.0F;
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, i1);
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPP, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, i1);

            if (flag1)
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * 0.6F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * 0.6F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * 0.6F * bc;
            }
            else
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = 0.6F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = 0.6F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = 0.6F * bc;
            }

            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            blockIcon = renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 4);
            renderBlocks.renderFaceXNeg(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                renderBlocks.colorRedTopLeft *= r;
                renderBlocks.colorRedBottomLeft *= r;
                renderBlocks.colorRedBottomRight *= r;
                renderBlocks.colorRedTopRight *= r;
                renderBlocks.colorGreenTopLeft *= g;
                renderBlocks.colorGreenBottomLeft *= g;
                renderBlocks.colorGreenBottomRight *= g;
                renderBlocks.colorGreenTopRight *= g;
                renderBlocks.colorBlueTopLeft *= b;
                renderBlocks.colorBlueBottomLeft *= b;
                renderBlocks.colorBlueBottomRight *= b;
                renderBlocks.colorBlueTopRight *= b;
                renderBlocks.renderFaceXNeg(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }
            
            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        if (renderBlocks.renderAllFaces || block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, 5))
        {
            if (renderBlocks.renderMaxX >= 1.0D)
            {
                ++x;
            }

            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z)&15728880;
            renderBlocks.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z - 1)&15728880;
            renderBlocks.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z + 1)&15728880;
            renderBlocks.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z)&15728880;
            flag3 = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag2 = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag5 = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag4 = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z - 1)&15728880;
            }

            if (!flag2 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, z + 1)&15728880;
            }

            if (!flag3 && !flag4)
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z - 1)&15728880;
            }

            if (!flag3 && !flag5)
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            }
            else
            {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, z + 1)&15728880;
            }

            if (renderBlocks.renderMaxX >= 1.0D)
            {
                --x;
            }

            i1 = l;

            if (renderBlocks.renderMaxX >= 1.0D || !renderBlocks.blockAccess.getBlock(x + 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, z);
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

            f7 = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            f3 = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + f7 + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            f4 = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (renderBlocks.aoLightValueScratchXZPN + f7 + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, i1);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPP, i1);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXYPP, i1);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, i1);

            if (flag1)
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r * 0.6F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g * 0.6F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b * 0.6F * bc;
            }
            else
            {
                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = 0.6F * rc;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = 0.6F * gc;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = 0.6F * bc;
            }

            renderBlocks.colorRedTopLeft *= f3;
            renderBlocks.colorGreenTopLeft *= f3;
            renderBlocks.colorBlueTopLeft *= f3;
            renderBlocks.colorRedBottomLeft *= f4;
            renderBlocks.colorGreenBottomLeft *= f4;
            renderBlocks.colorBlueBottomLeft *= f4;
            renderBlocks.colorRedBottomRight *= f5;
            renderBlocks.colorGreenBottomRight *= f5;
            renderBlocks.colorBlueBottomRight *= f5;
            renderBlocks.colorRedTopRight *= f6;
            renderBlocks.colorGreenTopRight *= f6;
            renderBlocks.colorBlueTopRight *= f6;
            blockIcon = renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 5);
            renderBlocks.renderFaceXPos(block, (double)x, (double)y, (double)z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
                renderBlocks.colorRedTopLeft *= r;
                renderBlocks.colorRedBottomLeft *= r;
                renderBlocks.colorRedBottomRight *= r;
                renderBlocks.colorRedTopRight *= r;
                renderBlocks.colorGreenTopLeft *= g;
                renderBlocks.colorGreenBottomLeft *= g;
                renderBlocks.colorGreenBottomRight *= g;
                renderBlocks.colorGreenTopRight *= g;
                renderBlocks.colorBlueTopLeft *= b;
                renderBlocks.colorBlueBottomLeft *= b;
                renderBlocks.colorBlueBottomRight *= b;
                renderBlocks.colorBlueTopRight *= b;
                renderBlocks.renderFaceXPos(block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
            }

            lc = 1.0F;
            rc = 1.0F;
            gc = 1.0F;
            bc = 1.0F;
            
            flag = true;
        }

        renderBlocks.enableAO = false;
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
    public static boolean renderStandardBlockWithColorMultiplier(RenderBlocks renderBlocks, Block par1Block, int par2X, int par3Y, int par4Z, float par5R, float par6G, float par7B)
    {
        renderBlocks.enableAO = false;
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

        int l = par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X, par3Y, par4Z);

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X, par3Y - 1, par4Z, 0))
        {
        	int i = renderBlocks.renderMinY > 0.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X, par3Y - 1, par4Z);
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
            renderBlocks.renderFaceYNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 0));
            flag = true;
        }

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X, par3Y + 1, par4Z, 1))
        {
        	int i = renderBlocks.renderMaxY < 1.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X, par3Y + 1, par4Z);
            		
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
            renderBlocks.renderFaceYPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 1));
            flag = true;
        }

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X, par3Y, par4Z - 1, 2))
        {
        	int i = renderBlocks.renderMinZ > 0.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X, par3Y, par4Z - 1);
            
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
            blockIcon = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 2);
            renderBlocks.renderFaceZNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f11 * par5R*rc, f14 * par6G*gc, f17 * par7B*bc);
                renderBlocks.renderFaceZNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X, par3Y, par4Z + 1, 3))
        {
        	int i = renderBlocks.renderMaxZ < 1.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X, par3Y, par4Z + 1);
            

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
            blockIcon = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 3);
            renderBlocks.renderFaceZPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f11 * par5R*rc, f14 * par6G*gc, f17 * par7B*bc);
                renderBlocks.renderFaceZPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X - 1, par3Y, par4Z, 4))
        {
        	int i = renderBlocks.renderMinX > 0.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X - 1, par3Y, par4Z);
            
            
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
            blockIcon = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 4);
            renderBlocks.renderFaceXNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
            	tessellator.setColorOpaque_F(f12 * par5R*rc, f15 * par6G*gc, f18 * par7B*bc);
                renderBlocks.renderFaceXNeg(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, par2X + 1, par3Y, par4Z, 5))
        {
        	int i = renderBlocks.renderMaxX < 1.0D ? l : par1Block.getMixedBrightnessForBlock(renderBlocks.blockAccess, par2X + 1, par3Y, par4Z);
             
             
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
            blockIcon = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, par2X, par3Y, par4Z, 5);
            renderBlocks.renderFaceXPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
            {
            	 tessellator.setColorOpaque_F(f12 * par5R*rc, f15 * par6G*gc, f18 * par7B*bc);
                renderBlocks.renderFaceXPos(par1Block, (double)par2X, (double)par3Y, (double)par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }
    
    
}
