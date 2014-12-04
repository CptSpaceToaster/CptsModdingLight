package coloredlightscore.src.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

// 56-59 fps w/o renderCache

public class CLRenderBlocksHelper {

    public CLRenderBlocksHelper() {
        // Ehhhh?
    }

    public static boolean renderStandardBlockWithAmbientOcclusionPartial(RenderBlocks instance, Block block, int x, int y, int z, float r, float g, float b) {
        return renderStandardBlockWithAmbientOcclusion(instance, block, x, y, z, r, g, b);
    }

    public static boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks instance, Block block, int x, int y, int z, float r, float g, float b) {
        instance.enableAO = true;
        boolean flag = false;
        float topLeftAoLightValue = 0.0F;
        float bottomLeftAoLightValue = 0.0F;
        float bottomRightAoLightValue = 0.0F;
        float topRightAoLightValue = 0.0F;
        boolean notGrassAndNotOverridden = true;
        int blockBrightness = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(0xf000f);

        if (instance.getBlockIcon(block).getIconName().equals("grass_top")) {
            // Don't tint the dirt part of grass blocks!
            notGrassAndNotOverridden = false;
        } else if (instance.hasOverrideBlockTexture()) {
            // Err... only tint the top of overridden textures?
            notGrassAndNotOverridden = false;
        }

        // Whether kitty-corner blocks are air or similar (fire, redstone, etc.)
        boolean isAirish1N;
        boolean isAirish1P;
        boolean isAirish2N;
        boolean isAirish2P;
        // Extra shading per-side to add depth
        float topColorMultiplier = 1.0f;
        float bottomColorMultiplier = 0.5f;
        float northSouthColorMultiplier = 0.8f;
        float eastWestColorMultiplier = 0.6f;

        float normalAoValue;
        int brightnessScratchValue;

        // Under side of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y - 1, z, 0)) {
            if (instance.renderMinY <= 0.0D) {
                --y;
            }

            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoLightValueScratchXYNN = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPN = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            isAirish1P = instance.blockAccess.getBlock(x + 1, y, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x - 1, y, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y, z + 1).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y, z - 1).getCanBlockGrass();

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXYNN;
            } else {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXYNN;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXYNN;
            } else {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXYPN;
            } else {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXYPN;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXYPN;
            } else {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1);
            }

            if (instance.renderMinY <= 0.0D) {
                ++y;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinY <= 0.0D || !instance.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            }

            normalAoValue = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            topLeftAoLightValue = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchYZNP + normalAoValue) / 4.0F;
            topRightAoLightValue = (instance.aoLightValueScratchYZNP + normalAoValue + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXYPN) / 4.0F;
            bottomRightAoLightValue = (normalAoValue + instance.aoLightValueScratchYZNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNN) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNN + normalAoValue + instance.aoLightValueScratchYZNN) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXYNN, instance.aoBrightnessYZNP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXYPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNN, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNN, instance.aoBrightnessYZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * bottomColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * bottomColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * bottomColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = bottomColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = bottomColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = bottomColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            instance.renderFaceYNeg(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 0));
            flag = true;
        }

        // Top face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y + 1, z, 1)) {
            if (instance.renderMaxY >= 1.0D) {
                ++y;
            }

            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoLightValueScratchXYNP = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPP = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            isAirish1P = instance.blockAccess.getBlock(x + 1, y, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x - 1, y, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y, z + 1).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y, z - 1).getCanBlockGrass();

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXYNP;
            } else {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z - 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXYPP;
            } else {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z - 1);
            }

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXYNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXYNP;
            } else {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z + 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXYPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXYPP;
            } else {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z + 1);
            }

            if (instance.renderMaxY >= 1.0D) {
                --y;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxY >= 1.0D || !instance.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            }

            normalAoValue = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            topRightAoLightValue = (instance.aoLightValueScratchXYZNPP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchYZPP + normalAoValue) / 4.0F;
            topLeftAoLightValue = (instance.aoLightValueScratchYZPP + normalAoValue + instance.aoLightValueScratchXYZPPP + instance.aoLightValueScratchXYPP) / 4.0F;
            bottomLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPN) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPN + normalAoValue + instance.aoLightValueScratchYZPN) / 4.0F;
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYZNPP, instance.aoBrightnessXYNP, instance.aoBrightnessYZPP, brightnessScratchValue);
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXYZPPP, instance.aoBrightnessXYPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, brightnessScratchValue);
            instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * topColorMultiplier;
            instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * topColorMultiplier;
            instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * topColorMultiplier;
            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            instance.renderFaceYPos(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 1));
            flag = true;
        }

        IIcon iicon;

        // North face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z - 1, 2)) {
            if (instance.renderMinZ <= 0.0D) {
                --z;
            }

            instance.aoLightValueScratchXZNN = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPN = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPN = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessYZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessYZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            isAirish1P = instance.blockAccess.getBlock(x + 1, y, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x - 1, y, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y + 1, z).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y - 1, z).getCanBlockGrass();

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z);
            }

            if (instance.renderMinZ <= 0.0D) {
                ++z;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinZ <= 0.0D || !instance.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            }

            normalAoValue = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            topLeftAoLightValue = (instance.aoLightValueScratchXZNN + instance.aoLightValueScratchXYZNPN + normalAoValue + instance.aoLightValueScratchYZPN) / 4.0F;
            bottomLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPN + instance.aoLightValueScratchXZPN + instance.aoLightValueScratchXYZPPN) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchYZNN + normalAoValue + instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXZPN) / 4.0F;
            topRightAoLightValue = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXZNN + instance.aoLightValueScratchYZNN + normalAoValue) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessYZPN, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessYZPN, instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNN, instance.aoBrightnessXYZPNN, instance.aoBrightnessXZPN, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXZNN, instance.aoBrightnessYZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * northSouthColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = northSouthColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 2);
            instance.renderFaceZNeg(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
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
                instance.renderFaceZNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // South face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x, y, z + 1, 3)) {
            if (instance.renderMaxZ >= 1.0D) {
                ++z;
            }

            instance.aoLightValueScratchXZNP = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPP = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZNP = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchYZPP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            instance.aoBrightnessYZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessYZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = instance.blockAccess.getBlock(x + 1, y, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x - 1, y, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y + 1, z).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y - 1, z).getCanBlockGrass();

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y - 1, z);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y + 1, z);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y - 1, z);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y + 1, z);
            }

            if (instance.renderMaxZ >= 1.0D) {
                --z;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxZ >= 1.0D || !instance.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            }

            normalAoValue = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            topLeftAoLightValue = (instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYZNPP + normalAoValue + instance.aoLightValueScratchYZPP) / 4.0F;
            topRightAoLightValue = (normalAoValue + instance.aoLightValueScratchYZPP + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchYZNP + normalAoValue + instance.aoLightValueScratchXYZPNP + instance.aoLightValueScratchXZPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYZNNP + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchYZNP + normalAoValue) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYZNPP, instance.aoBrightnessYZPP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessYZPP, instance.aoBrightnessXZPP, instance.aoBrightnessXYZPPP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessYZNP, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, instance.aoBrightnessYZNP, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * northSouthColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = northSouthColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = northSouthColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = northSouthColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3);
            instance.renderFaceZPos(block, (double) x, (double) y, (double) z, instance.getBlockIcon(block, instance.blockAccess, x, y, z, 3));

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
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
                instance.renderFaceZPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // West face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x - 1, y, z, 4)) {
            if (instance.renderMinX <= 0.0D) {
                --x;
            }

            instance.aoLightValueScratchXYNN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZNN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZNP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYNP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXYNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessXZNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessXZNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = instance.blockAccess.getBlock(x, y + 1, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x, y - 1, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y, z - 1).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y, z + 1).getCanBlockGrass();

            if (!isAirish2P && !isAirish1N) {
                instance.aoLightValueScratchXYZNNN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNNN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNNN = instance.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1);
            }

            if (!isAirish2N && !isAirish1N) {
                instance.aoLightValueScratchXYZNNP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNNP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNNP = instance.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1);
            }

            if (!isAirish2P && !isAirish1P) {
                instance.aoLightValueScratchXYZNPN = instance.aoLightValueScratchXZNN;
                instance.aoBrightnessXYZNPN = instance.aoBrightnessXZNN;
            } else {
                instance.aoLightValueScratchXYZNPN = instance.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1);
            }

            if (!isAirish2N && !isAirish1P) {
                instance.aoLightValueScratchXYZNPP = instance.aoLightValueScratchXZNP;
                instance.aoBrightnessXYZNPP = instance.aoBrightnessXZNP;
            } else {
                instance.aoLightValueScratchXYZNPP = instance.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1);
            }

            if (instance.renderMinX <= 0.0D) {
                ++x;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMinX <= 0.0D || !instance.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x - 1, y, z);
            }

            normalAoValue = instance.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            topRightAoLightValue = (instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXYZNNP + normalAoValue + instance.aoLightValueScratchXZNP) / 4.0F;
            topLeftAoLightValue = (normalAoValue + instance.aoLightValueScratchXZNP + instance.aoLightValueScratchXYNP + instance.aoLightValueScratchXYZNPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXZNN + normalAoValue + instance.aoLightValueScratchXYZNPN + instance.aoLightValueScratchXYNP) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXYZNNN + instance.aoLightValueScratchXYNN + instance.aoLightValueScratchXZNN + normalAoValue) / 4.0F;
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXYNN, instance.aoBrightnessXYZNNP, instance.aoBrightnessXZNP, brightnessScratchValue);
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXZNP, instance.aoBrightnessXYNP, instance.aoBrightnessXYZNPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXZNN, instance.aoBrightnessXYZNPN, instance.aoBrightnessXYNP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXYZNNN, instance.aoBrightnessXYNN, instance.aoBrightnessXZNN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * eastWestColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = eastWestColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 4);
            instance.renderFaceXNeg(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
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
                instance.renderFaceXNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        // East face of block
        if (instance.renderAllFaces || block.shouldSideBeRendered(instance.blockAccess, x + 1, y, z, 5)) {
            if (instance.renderMaxX >= 1.0D) {
                ++x;
            }

            instance.aoLightValueScratchXYPN = instance.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPN = instance.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXZPP = instance.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            instance.aoLightValueScratchXYPP = instance.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            instance.aoBrightnessXYPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z);
            instance.aoBrightnessXZPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z - 1);
            instance.aoBrightnessXZPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y, z + 1);
            instance.aoBrightnessXYPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z);
            isAirish1P = instance.blockAccess.getBlock(x, y + 1, z).getCanBlockGrass();
            isAirish1N = instance.blockAccess.getBlock(x, y - 1, z).getCanBlockGrass();
            isAirish2P = instance.blockAccess.getBlock(x, y, z + 1).getCanBlockGrass();
            isAirish2N = instance.blockAccess.getBlock(x, y, z - 1).getCanBlockGrass();

            if (!isAirish1N && !isAirish2N) {
                instance.aoLightValueScratchXYZPNN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPNN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPNN = instance.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z - 1);
            }

            if (!isAirish1N && !isAirish2P) {
                instance.aoLightValueScratchXYZPNP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPNP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPNP = instance.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y - 1, z + 1);
            }

            if (!isAirish1P && !isAirish2N) {
                instance.aoLightValueScratchXYZPPN = instance.aoLightValueScratchXZPN;
                instance.aoBrightnessXYZPPN = instance.aoBrightnessXZPN;
            } else {
                instance.aoLightValueScratchXYZPPN = instance.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z - 1);
            }

            if (!isAirish1P && !isAirish2P) {
                instance.aoLightValueScratchXYZPPP = instance.aoLightValueScratchXZPP;
                instance.aoBrightnessXYZPPP = instance.aoBrightnessXZPP;
            } else {
                instance.aoLightValueScratchXYZPPP = instance.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                instance.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(instance.blockAccess, x, y + 1, z + 1);
            }

            if (instance.renderMaxX >= 1.0D) {
                --x;
            }

            brightnessScratchValue = blockBrightness;

            if (instance.renderMaxX >= 1.0D || !instance.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                brightnessScratchValue = block.getMixedBrightnessForBlock(instance.blockAccess, x + 1, y, z);
            }

            normalAoValue = instance.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            topLeftAoLightValue = (instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXYZPNP + normalAoValue + instance.aoLightValueScratchXZPP) / 4.0F;
            bottomLeftAoLightValue = (instance.aoLightValueScratchXYZPNN + instance.aoLightValueScratchXYPN + instance.aoLightValueScratchXZPN + normalAoValue) / 4.0F;
            bottomRightAoLightValue = (instance.aoLightValueScratchXZPN + normalAoValue + instance.aoLightValueScratchXYZPPN + instance.aoLightValueScratchXYPP) / 4.0F;
            topRightAoLightValue = (normalAoValue + instance.aoLightValueScratchXZPP + instance.aoLightValueScratchXYPP + instance.aoLightValueScratchXYZPPP) / 4.0F;
            instance.brightnessTopLeft = getAoBrightness(instance.aoBrightnessXYPN, instance.aoBrightnessXYZPNP, instance.aoBrightnessXZPP, brightnessScratchValue);
            instance.brightnessTopRight = getAoBrightness(instance.aoBrightnessXZPP, instance.aoBrightnessXYPP, instance.aoBrightnessXYZPPP, brightnessScratchValue);
            instance.brightnessBottomRight = getAoBrightness(instance.aoBrightnessXZPN, instance.aoBrightnessXYZPPN, instance.aoBrightnessXYPP, brightnessScratchValue);
            instance.brightnessBottomLeft = getAoBrightness(instance.aoBrightnessXYZPNN, instance.aoBrightnessXYPN, instance.aoBrightnessXZPN, brightnessScratchValue);

            if (notGrassAndNotOverridden) {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = r * eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = g * eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = b * eastWestColorMultiplier;
            } else {
                instance.colorRedTopLeft = instance.colorRedBottomLeft = instance.colorRedBottomRight = instance.colorRedTopRight = eastWestColorMultiplier;
                instance.colorGreenTopLeft = instance.colorGreenBottomLeft = instance.colorGreenBottomRight = instance.colorGreenTopRight = eastWestColorMultiplier;
                instance.colorBlueTopLeft = instance.colorBlueBottomLeft = instance.colorBlueBottomRight = instance.colorBlueTopRight = eastWestColorMultiplier;
            }

            instance.colorRedTopLeft *= topLeftAoLightValue;
            instance.colorGreenTopLeft *= topLeftAoLightValue;
            instance.colorBlueTopLeft *= topLeftAoLightValue;
            instance.colorRedBottomLeft *= bottomLeftAoLightValue;
            instance.colorGreenBottomLeft *= bottomLeftAoLightValue;
            instance.colorBlueBottomLeft *= bottomLeftAoLightValue;
            instance.colorRedBottomRight *= bottomRightAoLightValue;
            instance.colorGreenBottomRight *= bottomRightAoLightValue;
            instance.colorBlueBottomRight *= bottomRightAoLightValue;
            instance.colorRedTopRight *= topRightAoLightValue;
            instance.colorGreenTopRight *= topRightAoLightValue;
            instance.colorBlueTopRight *= topRightAoLightValue;
            iicon = instance.getBlockIcon(block, instance.blockAccess, x, y, z, 5);
            instance.renderFaceXPos(block, (double) x, (double) y, (double) z, iicon);

            if (RenderBlocks.fancyGrass && iicon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
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
                instance.renderFaceXPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        instance.enableAO = false;
        return flag;
    }

    /**
     * DEPRECATED
     * Gutted to work with colors
     * CptSpaceToaster
     *
     * Reset to vanilla and fixed by Glitchfinder
     *
     * @param par1Block Block Type (Sky or Normal Block)
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param par5 amount of red
     * @param par6 amount of green
     * @param par7 amount of blue
    public static boolean renderStandardBlockWithAmbientOcclusion(RenderBlocks renderBlocks, Block par1Block, int x, int y, int z, float par5, float par6, float par7)
    {		
    	renderBlocks.enableAO = true;
    	boolean renderedSomething = false;
    	float topLeftaoLightValueAvg = 0.0F;
    	float bottomLeftaoLightValueAvg = 0.0F;
    	float bottomRightaoLightValueAvg = 0.0F;
    	float topRightaoLightValueAvg = 0.0F;
    	boolean overrideBlockColor = true;
    	int blockBrightnessWithColor = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z);
    	Tessellator tessellator = Tessellator.instance;
    	tessellator.setBrightness(983055); // 1111 0000 0000 0000 1111
    	//float blockSunlightFactor;
    	//float sunlightBrightnesss = renderBlocks.minecraftRB.theWorld.getSunBrightness(1.0f);
    	
    	if (renderBlocks.getBlockIcon(par1Block).getIconName().equals("grass_top"))
    	{
    		overrideBlockColor = false;
    	}
    	else if (renderBlocks.hasOverrideBlockTexture())
    	{
    		overrideBlockColor = false;
    	}

    	boolean canBlockGrassXYNN;
    	boolean canBlockGrassXYPN;
    	boolean canBlockGrassYZNN;
    	boolean canBlockGrassYZNP;
    	float neighborAoValue;
    	int neighborLightValue;

    	//YYY-1
    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, 0))
    	{
    		if (renderBlocks.renderMinY <= 0.0D)
    		{
    			--y;
    		}

    		renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXYNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z);
    		renderBlocks.aoBrightnessYZNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z - 1);
    		renderBlocks.aoBrightnessYZNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z + 1);
    		renderBlocks.aoBrightnessXYPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

    		if (!canBlockGrassYZNN && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXYNN;
    			renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXYNN;
    		}
    		else
    		{
    			
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z - 1);
    		}

    		if (!canBlockGrassYZNP && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXYNN;
    			renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXYNN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z + 1);
    		}

    		if (!canBlockGrassYZNN && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXYPN;
    			renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXYPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z - 1);
    		}

    		if (!canBlockGrassYZNP && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXYPN;
    			renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXYPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z + 1);
    		}

    		if (renderBlocks.renderMinY <= 0.0D)
    		{
    			++y;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMinY <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y - 1, z).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z);
    		}

    		neighborAoValue = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
    		topLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + neighborAoValue) / 4.0F;
    		topRightaoLightValueAvg = (renderBlocks.aoLightValueScratchYZNP + neighborAoValue + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
    		bottomRightaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
    		bottomLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + neighborAoValue + renderBlocks.aoLightValueScratchYZNN) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNN, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYPN, neighborLightValue);

    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 12) & 240) + ((renderBlocks.aoBrightnessXYNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZNP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 12) & 240) + ((renderBlocks.aoBrightnessXYNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZNN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZNN >> 12) & 240) + ((renderBlocks.aoBrightnessXYPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZNP >> 12) & 240) + ((renderBlocks.aoBrightnessXYPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight);
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight);
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight);
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// If the block color is not being overridden
    		if (overrideBlockColor) {
    			renderBlocks.colorRedTopLeft       *= par5;
    			renderBlocks.colorGreenTopLeft     *= par6;
    			renderBlocks.colorBlueTopLeft      *= par7;
    			renderBlocks.colorRedBottomLeft    *= par5;
    			renderBlocks.colorGreenBottomLeft  *= par6;
    			renderBlocks.colorBlueBottomLeft   *= par7;
    			renderBlocks.colorRedBottomRight   *= par5;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorBlueBottomRight  *= par7;
    			renderBlocks.colorRedTopRight      *= par5;
    			renderBlocks.colorGreenTopRight    *= par6;
    			renderBlocks.colorBlueTopRight     *= par7;
    		}

    		// Apply shadows next to walls and highlights next to edges
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * 0.5f;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * 0.5f;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * 0.5f;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * 0.5f;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * 0.5f;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * 0.5f;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * 0.5f;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * 0.5f;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * 0.5f;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * 0.5f;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * 0.5f;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * 0.5f;

    		// END - GlitchLights

    		renderBlocks.renderFaceYNeg(par1Block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 0));
    		renderedSomething = true;
    	}
    	//YYY+1
    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, 1))
    	{
    		if (renderBlocks.renderMaxY >= 1.0D)
    		{
    			++y;
    		}
    		
    		renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXYNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z);
    		renderBlocks.aoBrightnessXYPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z);
    		renderBlocks.aoBrightnessYZPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z - 1);
    		renderBlocks.aoBrightnessYZPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z + 1);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

    		if (!canBlockGrassYZNN && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXYNP;
    			renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXYNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z - 1);
    		}

    		if (!canBlockGrassYZNN && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXYPP;
    			renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXYPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z - 1);
    		}

    		if (!canBlockGrassYZNP && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXYNP;
    			renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXYNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z + 1);
    		}

    		if (!canBlockGrassYZNP && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXYPP;
    			renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXYPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z + 1);
    		}

    		if (renderBlocks.renderMaxY >= 1.0D)
    		{
    			--y;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMaxY >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y + 1, z).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z);
    		}

    		//blockSunlightFactor = ((float)(neighborLightValue >> 20) / 30f) * sunlightBrightnesss;
    		
    		//if (blockSunlightFactor > 0.5f)
    		//	blockSunlightFactor = 0.5f;
    		
    		
    		neighborAoValue = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
    		topRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + neighborAoValue) / 4.0F;
    		topLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchYZPP + neighborAoValue + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
    		bottomLeftaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
    		bottomRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + neighborAoValue + renderBlocks.aoLightValueScratchYZPN) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses for the top of the block
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYPP, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPN, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, neighborLightValue);
    		
    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 12) & 240) + ((renderBlocks.aoBrightnessYZPP >> 12) & 240) + ((renderBlocks.aoBrightnessXYPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 12) & 240) + ((renderBlocks.aoBrightnessYZPN >> 12) & 240) + ((renderBlocks.aoBrightnessXYPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 12) & 240) + ((renderBlocks.aoBrightnessXYNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 12) & 240) + ((renderBlocks.aoBrightnessXYNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight );
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight );
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight );
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );				
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// Apply shadows next to walls, highlights next to edges, and the supplied argument colors
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * par5;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * par6;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * par7;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * par5;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * par6;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * par7;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * par5;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * par6;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * par7;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * par5;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * par6;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * par7;

    		// END - GlitchLights

    		renderBlocks.renderFaceYPos(par1Block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 1));
    		renderedSomething = true;
    	}

    	IIcon var22;

    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, 2))
    	{
    		if (renderBlocks.renderMinZ <= 0.0D)
    		{
    			--z;
    		}

    		renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXZNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z);
    		renderBlocks.aoBrightnessYZNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z);
    		renderBlocks.aoBrightnessYZPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z);
    		renderBlocks.aoBrightnessXZPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();
    		
    		if (!canBlockGrassXYNN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
    			renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y - 1, z);
    		}

    		if (!canBlockGrassXYNN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
    			renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y + 1, z);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
    			renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y - 1, z);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
    			renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y + 1, z);
    		}

    		if (renderBlocks.renderMinZ <= 0.0D)
    		{
    			++z;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMinZ <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y, z - 1).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z - 1);
    		}

    		neighborAoValue = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
    		topLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + neighborAoValue + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
    		bottomLeftaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
    		bottomRightaoLightValueAvg = (renderBlocks.aoLightValueScratchYZNN + neighborAoValue + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
    		topRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + neighborAoValue) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses
    //			renderBlocks.brightnessTopLeft     = ((renderBlocks.aoBrightnessXYZNPN & 15728880) + (renderBlocks.aoBrightnessXZNN & 15728880) + (renderBlocks.aoBrightnessYZPN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomLeft  = ((renderBlocks.aoBrightnessXYZPPN & 15728880) + (renderBlocks.aoBrightnessYZPN & 15728880) + (renderBlocks.aoBrightnessXZPN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomRight = ((renderBlocks.aoBrightnessXYZPNN & 15728880) + (renderBlocks.aoBrightnessYZNN & 15728880) + (renderBlocks.aoBrightnessXZPN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessTopRight    = ((renderBlocks.aoBrightnessXYZNNN & 15728880) + (renderBlocks.aoBrightnessXZNN & 15728880) + (renderBlocks.aoBrightnessYZNN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZPN, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXZPN, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, neighborLightValue);


    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 12) & 240) + ((renderBlocks.aoBrightnessYZPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZNN >> 12) & 240) + ((renderBlocks.aoBrightnessXZPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 12) & 240) + ((renderBlocks.aoBrightnessXZNN >> 12) & 240) + ((renderBlocks.aoBrightnessYZNN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight);
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight);
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight);
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// If the block color is not being overridden
    		if (overrideBlockColor) {
    			renderBlocks.colorRedTopLeft       *= par5;
    			renderBlocks.colorGreenTopLeft     *= par6;
    			renderBlocks.colorBlueTopLeft      *= par7;
    			renderBlocks.colorRedBottomLeft    *= par5;
    			renderBlocks.colorGreenBottomLeft  *= par6;
    			renderBlocks.colorBlueBottomLeft   *= par7;
    			renderBlocks.colorRedBottomRight   *= par5;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorBlueBottomRight  *= par7;
    			renderBlocks.colorRedTopRight      *= par5;
    			renderBlocks.colorGreenTopRight    *= par6;
    			renderBlocks.colorBlueTopRight     *= par7;
    		}

    		// Apply shadows next to walls and highlights next to edges
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * 0.8f;

    		// END - GlitchLights

    		var22 = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 2);
    		renderBlocks.renderFaceZNeg(par1Block, (double)x, (double)y, (double)z, var22);

    		if (RenderBlocks.fancyGrass  && var22.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
    		{
    			renderBlocks.colorRedTopLeft *= par5;
    			renderBlocks.colorRedBottomLeft *= par5;
    			renderBlocks.colorRedBottomRight *= par5;
    			renderBlocks.colorRedTopRight *= par5;
    			renderBlocks.colorGreenTopLeft *= par6;
    			renderBlocks.colorGreenBottomLeft *= par6;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorGreenTopRight *= par6;
    			renderBlocks.colorBlueTopLeft *= par7;
    			renderBlocks.colorBlueBottomLeft *= par7;
    			renderBlocks.colorBlueBottomRight *= par7;
    			renderBlocks.colorBlueTopRight *= par7;
    			renderBlocks.renderFaceZNeg(par1Block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
    		}

    		renderedSomething = true;
    	}

    	//ZZZ+1
    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, 3))
    	{
    		if (renderBlocks.renderMaxZ >= 1.0D)
    		{
    			++z;
    		}

    		renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXZNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z);
    		renderBlocks.aoBrightnessXZPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z);
    		renderBlocks.aoBrightnessYZNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z);
    		renderBlocks.aoBrightnessYZPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

    		if (!canBlockGrassXYNN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
    			renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y - 1, z);
    		}

    		if (!canBlockGrassXYNN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
    			renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y + 1, z);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
    			renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y - 1, z);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
    			renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y + 1, z);
    		}

    		if (renderBlocks.renderMaxZ >= 1.0D)
    		{
    			--z;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMaxZ >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y, z + 1).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z + 1);
    		}

    		neighborAoValue = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
    		topLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + neighborAoValue + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
    		topRightaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
    		bottomRightaoLightValueAvg = (renderBlocks.aoLightValueScratchYZNP + neighborAoValue + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
    		bottomLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + neighborAoValue) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses
    //			renderBlocks.brightnessTopLeft     = ((renderBlocks.aoBrightnessXYZNPP & 15728880) + (renderBlocks.aoBrightnessXZNP & 15728880) + (renderBlocks.aoBrightnessYZPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomLeft  = ((renderBlocks.aoBrightnessXYZNNP & 15728880) + (renderBlocks.aoBrightnessYZNP & 15728880) + (renderBlocks.aoBrightnessXZNP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomRight = ((renderBlocks.aoBrightnessXYZPNP & 15728880) + (renderBlocks.aoBrightnessYZNP & 15728880) + (renderBlocks.aoBrightnessXZPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessTopRight    = ((renderBlocks.aoBrightnessXYZPPP & 15728880) + (renderBlocks.aoBrightnessXZPP & 15728880) + (renderBlocks.aoBrightnessYZPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZPP, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXZNP, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXZPP, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessYZPP, neighborLightValue);

    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 12) & 240) + ((renderBlocks.aoBrightnessXZNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZNP >> 12) & 240) + ((renderBlocks.aoBrightnessXZNP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 12) & 240) + ((renderBlocks.aoBrightnessYZNP >> 12) & 240) + ((renderBlocks.aoBrightnessXZPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessYZPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 12) & 240) + ((renderBlocks.aoBrightnessXZPP >> 12) & 240) + ((renderBlocks.aoBrightnessYZPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight);
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight);
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight);
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// If the block color is not being overridden
    		if (overrideBlockColor) {
    			renderBlocks.colorRedTopLeft       *= par5;
    			renderBlocks.colorGreenTopLeft     *= par6;
    			renderBlocks.colorBlueTopLeft      *= par7;
    			renderBlocks.colorRedBottomLeft    *= par5;
    			renderBlocks.colorGreenBottomLeft  *= par6;
    			renderBlocks.colorBlueBottomLeft   *= par7;
    			renderBlocks.colorRedBottomRight   *= par5;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorBlueBottomRight  *= par7;
    			renderBlocks.colorRedTopRight      *= par5;
    			renderBlocks.colorGreenTopRight    *= par6;
    			renderBlocks.colorBlueTopRight     *= par7;
    		}

    		// Apply shadows next to walls and highlights next to edges
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * 0.8f;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * 0.8f;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * 0.8f;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * 0.8f;

    		// END - GlitchLights

    		var22 = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 3);
    		renderBlocks.renderFaceZPos(par1Block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 3));

    		if (RenderBlocks.fancyGrass  && var22.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
    		{
    			renderBlocks.colorRedTopLeft *= par5;
    			renderBlocks.colorRedBottomLeft *= par5;
    			renderBlocks.colorRedBottomRight *= par5;
    			renderBlocks.colorRedTopRight *= par5;
    			renderBlocks.colorGreenTopLeft *= par6;
    			renderBlocks.colorGreenBottomLeft *= par6;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorGreenTopRight *= par6;
    			renderBlocks.colorBlueTopLeft *= par7;
    			renderBlocks.colorBlueBottomLeft *= par7;
    			renderBlocks.colorBlueBottomRight *= par7;
    			renderBlocks.colorBlueTopRight *= par7;
    			renderBlocks.renderFaceZPos(par1Block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
    		}

    		renderedSomething = true;
    	}

    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, 4))
    	{
    		if (renderBlocks.renderMinX <= 0.0D)
    		{
    			--x;
    		}

    		renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXYNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z);
    		renderBlocks.aoBrightnessXZNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z - 1);
    		renderBlocks.aoBrightnessXZNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z + 1);
    		renderBlocks.aoBrightnessXYNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

    		if (!canBlockGrassYZNP && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
    			renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z - 1);
    		}

    		if (!canBlockGrassYZNN && !canBlockGrassXYNN)
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
    			renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z + 1);
    		}

    		if (!canBlockGrassYZNP && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
    			renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z - 1);
    		}

    		if (!canBlockGrassYZNN && !canBlockGrassXYPN)
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
    			renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZNPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z + 1);
    		}

    		if (renderBlocks.renderMinX <= 0.0D)
    		{
    			++x;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMinX <= 0.0D || !renderBlocks.blockAccess.getBlock(x - 1, y, z).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x - 1, y, z);
    		}

    		neighborAoValue = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
    		topRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + neighborAoValue + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
    		topLeftaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;
    		bottomLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXZNN + neighborAoValue + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
    		bottomRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + neighborAoValue) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses
    //			renderBlocks.brightnessTopLeft     = ((renderBlocks.aoBrightnessXYZNPP & 15728880) + (renderBlocks.aoBrightnessXZNP & 15728880) + (renderBlocks.aoBrightnessXYNP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomLeft  = ((renderBlocks.aoBrightnessXYZNPN & 15728880) + (renderBlocks.aoBrightnessXZNN & 15728880) + (renderBlocks.aoBrightnessXYNP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomRight = ((renderBlocks.aoBrightnessXYZNNN & 15728880) + (renderBlocks.aoBrightnessXYNN & 15728880) + (renderBlocks.aoBrightnessXZNN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessTopRight    = ((renderBlocks.aoBrightnessXYZNNP & 15728880) + (renderBlocks.aoBrightnessXYNN & 15728880) + (renderBlocks.aoBrightnessXZNP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYNP, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNP, neighborLightValue);

    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZNPP >> 12) & 240) + ((renderBlocks.aoBrightnessXZNP >> 12) & 240) + ((renderBlocks.aoBrightnessXYNP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZNPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZNN >> 12) & 240) + ((renderBlocks.aoBrightnessXYNP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZNNN >> 12) & 240) + ((renderBlocks.aoBrightnessXYNN >> 12) & 240) + ((renderBlocks.aoBrightnessXZNN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZNP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZNNP >> 12) & 240) + ((renderBlocks.aoBrightnessXYNN >> 12) & 240) + ((renderBlocks.aoBrightnessXZNP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight);
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight);
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight);
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// If the block color is not being overridden
    		if (overrideBlockColor) {
    			renderBlocks.colorRedTopLeft       *= par5;
    			renderBlocks.colorGreenTopLeft     *= par6;
    			renderBlocks.colorBlueTopLeft      *= par7;
    			renderBlocks.colorRedBottomLeft    *= par5;
    			renderBlocks.colorGreenBottomLeft  *= par6;
    			renderBlocks.colorBlueBottomLeft   *= par7;
    			renderBlocks.colorRedBottomRight   *= par5;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorBlueBottomRight  *= par7;
    			renderBlocks.colorRedTopRight      *= par5;
    			renderBlocks.colorGreenTopRight    *= par6;
    			renderBlocks.colorBlueTopRight     *= par7;
    		}

    		// Apply shadows next to walls and highlights next to edges
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * 0.6f;

    		// END - GlitchLights

    		var22 = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 4);
    		renderBlocks.renderFaceXNeg(par1Block, (double)x, (double)y, (double)z, var22);

    		if (RenderBlocks.fancyGrass  && var22.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
    		{
    			renderBlocks.colorRedTopLeft *= par5;
    			renderBlocks.colorRedBottomLeft *= par5;
    			renderBlocks.colorRedBottomRight *= par5;
    			renderBlocks.colorRedTopRight *= par5;
    			renderBlocks.colorGreenTopLeft *= par6;
    			renderBlocks.colorGreenBottomLeft *= par6;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorGreenTopRight *= par6;
    			renderBlocks.colorBlueTopLeft *= par7;
    			renderBlocks.colorBlueBottomLeft *= par7;
    			renderBlocks.colorBlueBottomRight *= par7;
    			renderBlocks.colorBlueTopRight *= par7;
    			renderBlocks.renderFaceXNeg(par1Block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
    		}

    		renderedSomething = true;
    	}

    	//XXX+1
    	if (renderBlocks.renderAllFaces || par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, 5))
    	{
    		if (renderBlocks.renderMaxX >= 1.0D)
    		{
    			++x;
    		}

    		renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
    		renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
    		renderBlocks.aoBrightnessXYPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z);
    		renderBlocks.aoBrightnessXZPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z - 1);
    		renderBlocks.aoBrightnessXZPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y, z + 1);
    		renderBlocks.aoBrightnessXYPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z);
    		canBlockGrassXYPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
    		canBlockGrassXYNN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
    		canBlockGrassYZNP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
    		canBlockGrassYZNN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

    		if (!canBlockGrassXYNN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
    			renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z - 1);
    		}

    		if (!canBlockGrassXYNN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
    			renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPNP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y - 1, z + 1);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNN)
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
    			renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPN = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z - 1);
    		}

    		if (!canBlockGrassXYPN && !canBlockGrassYZNP)
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
    			renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
    		}
    		else
    		{
    			renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
    			renderBlocks.aoBrightnessXYZPPP = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x, y + 1, z + 1);
    		}

    		if (renderBlocks.renderMaxX >= 1.0D)
    		{
    			--x;
    		}

    		neighborLightValue = blockBrightnessWithColor;

    		if (renderBlocks.renderMaxX >= 1.0D || !renderBlocks.blockAccess.getBlock(x + 1, y, z).isOpaqueCube())
    		{
    			neighborLightValue = CLBlockHelper.getMixedBrightnessForBlockWithColor(renderBlocks.blockAccess, x + 1, y, z);
    		}

    		neighborAoValue = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();;
    		topLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + neighborAoValue + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
    		bottomLeftaoLightValueAvg = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + neighborAoValue) / 4.0F;
    		bottomRightaoLightValueAvg = (renderBlocks.aoLightValueScratchXZPN + neighborAoValue + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
    		topRightaoLightValueAvg = (neighborAoValue + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;

    		// BEGIN - GlitchLights

    		// Calculate the basic corner brightnesses
    //			renderBlocks.brightnessTopLeft     = ((renderBlocks.aoBrightnessXYZPNP & 15728880) + (renderBlocks.aoBrightnessXYPN & 15728880) + (renderBlocks.aoBrightnessXZPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomLeft  = ((renderBlocks.aoBrightnessXYZPNN & 15728880) + (renderBlocks.aoBrightnessXYPN & 15728880) + (renderBlocks.aoBrightnessXZPN & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessBottomRight = ((renderBlocks.aoBrightnessXYZPPN & 15728880) + (renderBlocks.aoBrightnessXZPN & 15728880) + (renderBlocks.aoBrightnessXYPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    //			renderBlocks.brightnessTopRight    = ((renderBlocks.aoBrightnessXYZPPP & 15728880) + (renderBlocks.aoBrightnessXZPP & 15728880) + (renderBlocks.aoBrightnessXYPP & 15728880) + (neighborLightValue & 15728880)) >> 2;
    		renderBlocks.brightnessTopLeft     = getAoBrightness(renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPP, neighborLightValue);
    		renderBlocks.brightnessBottomLeft  = getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, neighborLightValue);
    		renderBlocks.brightnessBottomRight = getAoBrightness(renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYPP, neighborLightValue);
    		renderBlocks.brightnessTopRight    = getAoBrightness(renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, neighborLightValue);

    		// Calculate a basic corner color: x+ z+
    		renderBlocks.colorRedTopLeft       = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopLeft     = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopLeft      = ((float) ((((renderBlocks.aoBrightnessXYZPNP >> 12) & 240) + ((renderBlocks.aoBrightnessXYPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x+ z-
    		renderBlocks.colorRedBottomLeft    = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomLeft  = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomLeft   = ((float) ((((renderBlocks.aoBrightnessXYZPNN >> 12) & 240) + ((renderBlocks.aoBrightnessXYPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZPN >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z-
    		renderBlocks.colorRedBottomRight   = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenBottomRight = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPN >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueBottomRight  = ((float) ((((renderBlocks.aoBrightnessXYZPPN >> 12) & 240) + ((renderBlocks.aoBrightnessXZPN >> 12) & 240) + ((renderBlocks.aoBrightnessXYPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);
    		// Calculate a basic corner color: x- z+
    		renderBlocks.colorRedTopRight      = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 4 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 4 ) & 240) + ((neighborLightValue >> 4 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorGreenTopRight    = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXZPP >> 8 ) & 240) + ((renderBlocks.aoBrightnessXYPP >> 8 ) & 240) + ((neighborLightValue >> 8 ) & 240)) >> 2) / 240f);
    		renderBlocks.colorBlueTopRight     = ((float) ((((renderBlocks.aoBrightnessXYZPPP >> 12) & 240) + ((renderBlocks.aoBrightnessXZPP >> 12) & 240) + ((renderBlocks.aoBrightnessXYPP >> 12) & 240) + ((neighborLightValue >> 12) & 240)) >> 2) / 240f);

    		// If colors are applied to any corners
    		if ((renderBlocks.colorRedTopLeft + renderBlocks.colorGreenTopLeft + renderBlocks.colorBlueTopLeft + renderBlocks.colorRedBottomLeft + renderBlocks.colorGreenBottomLeft + renderBlocks.colorBlueBottomLeft + renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight + renderBlocks.colorRedTopRight + renderBlocks.colorGreenTopRight + renderBlocks.colorBlueTopRight) > 0f) {
    			// Calculate a rough brightness modifier for each corner color
    			float brightnessTopLeft     = (1f - ((renderBlocks.colorRedTopLeft     + renderBlocks.colorGreenTopLeft     + renderBlocks.colorBlueTopLeft    ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopLeft     & 240))) / 240f);
    			float brightnessBottomLeft  = (1f - ((renderBlocks.colorRedBottomLeft  + renderBlocks.colorGreenBottomLeft  + renderBlocks.colorBlueBottomLeft ) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomLeft  & 240))) / 240f);
    			float brightnessBottomRight = (1f - ((renderBlocks.colorRedBottomRight + renderBlocks.colorGreenBottomRight + renderBlocks.colorBlueBottomRight) / 3f)) * (((float) (240 - (renderBlocks.brightnessBottomRight & 240))) / 240f);
    			float brightnessTopRight    = (1f - ((renderBlocks.colorRedTopRight    + renderBlocks.colorGreenTopRight    + renderBlocks.colorBlueTopRight   ) / 3f)) * (((float) (240 - (renderBlocks.brightnessTopRight    & 240))) / 240f);

    			// Finalize the corner color: x+ z+
    			renderBlocks.colorRedTopLeft       = Math.min(1f, renderBlocks.colorRedTopLeft       + brightnessTopLeft    );
    			renderBlocks.colorGreenTopLeft     = Math.min(1f, renderBlocks.colorGreenTopLeft     + brightnessTopLeft    );
    			renderBlocks.colorBlueTopLeft      = Math.min(1f, renderBlocks.colorBlueTopLeft      + brightnessTopLeft    );
    			// Finalize the corner color: x+ z-
    			renderBlocks.colorRedBottomLeft    = Math.min(1f, renderBlocks.colorRedBottomLeft    + brightnessBottomLeft );
    			renderBlocks.colorGreenBottomLeft  = Math.min(1f, renderBlocks.colorGreenBottomLeft  + brightnessBottomLeft );
    			renderBlocks.colorBlueBottomLeft   = Math.min(1f, renderBlocks.colorBlueBottomLeft   + brightnessBottomLeft );
    			// Finalize the corner color: x- z-
    			renderBlocks.colorRedBottomRight   = Math.min(1f, renderBlocks.colorRedBottomRight   + brightnessBottomRight);
    			renderBlocks.colorGreenBottomRight = Math.min(1f, renderBlocks.colorGreenBottomRight + brightnessBottomRight);
    			renderBlocks.colorBlueBottomRight  = Math.min(1f, renderBlocks.colorBlueBottomRight  + brightnessBottomRight);
    			// Finalize the corner color: x- z+
    			renderBlocks.colorRedTopRight      = Math.min(1f, renderBlocks.colorRedTopRight      + brightnessTopRight   );
    			renderBlocks.colorGreenTopRight    = Math.min(1f, renderBlocks.colorGreenTopRight    + brightnessTopRight   );
    			renderBlocks.colorBlueTopRight     = Math.min(1f, renderBlocks.colorBlueTopRight     + brightnessTopRight   );
    		}
    		// If no colors are applied to any of the corners
    		else {
    			// Reset the corners to white
    			renderBlocks.colorRedTopLeft = renderBlocks.colorGreenTopLeft = renderBlocks.colorBlueTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorGreenBottomRight = renderBlocks.colorBlueBottomRight = renderBlocks.colorRedTopRight = renderBlocks.colorGreenTopRight = renderBlocks.colorBlueTopRight = 1f;
    		}

    		// If the block color is not being overridden
    		if (overrideBlockColor) {
    			renderBlocks.colorRedTopLeft       *= par5;
    			renderBlocks.colorGreenTopLeft     *= par6;
    			renderBlocks.colorBlueTopLeft      *= par7;
    			renderBlocks.colorRedBottomLeft    *= par5;
    			renderBlocks.colorGreenBottomLeft  *= par6;
    			renderBlocks.colorBlueBottomLeft   *= par7;
    			renderBlocks.colorRedBottomRight   *= par5;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorBlueBottomRight  *= par7;
    			renderBlocks.colorRedTopRight      *= par5;
    			renderBlocks.colorGreenTopRight    *= par6;
    			renderBlocks.colorBlueTopRight     *= par7;
    		}

    		// Apply shadows next to walls and highlights next to edges
    		renderBlocks.colorRedTopLeft       *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorGreenTopLeft     *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorBlueTopLeft      *= topLeftaoLightValueAvg  * 0.6f;
    		renderBlocks.colorRedBottomLeft    *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenBottomLeft  *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueBottomLeft   *= bottomLeftaoLightValueAvg * 0.6f;
    		renderBlocks.colorRedBottomRight   *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenBottomRight *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueBottomRight  *= bottomRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorRedTopRight      *= topRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorGreenTopRight    *= topRightaoLightValueAvg * 0.6f;
    		renderBlocks.colorBlueTopRight     *= topRightaoLightValueAvg * 0.6f;

    		// END - GlitchLights

    		var22 = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, 5);
    		renderBlocks.renderFaceXPos(par1Block, (double)x, (double)y, (double)z, var22);

    		if (RenderBlocks.fancyGrass && var22.getIconName().equals("grass_side") && !renderBlocks.hasOverrideBlockTexture())
    		{
    			renderBlocks.colorRedTopLeft *= par5;
    			renderBlocks.colorRedBottomLeft *= par5;
    			renderBlocks.colorRedBottomRight *= par5;
    			renderBlocks.colorRedTopRight *= par5;
    			renderBlocks.colorGreenTopLeft *= par6;
    			renderBlocks.colorGreenBottomLeft *= par6;
    			renderBlocks.colorGreenBottomRight *= par6;
    			renderBlocks.colorGreenTopRight *= par6;
    			renderBlocks.colorBlueTopLeft *= par7;
    			renderBlocks.colorBlueBottomLeft *= par7;
    			renderBlocks.colorBlueBottomRight *= par7;
    			renderBlocks.colorBlueTopRight *= par7;
    			renderBlocks.renderFaceXPos(par1Block, (double)x, (double)y, (double)z, BlockGrass.getIconSideOverlay());
    		}

    		renderedSomething = true;
    	}

    	renderBlocks.enableAO = false;		
    	return renderedSomething;
    }
     */

    /**
     * Renders a standard cube block at the given coordinates, with a given color ratio.  Args: block, x, y, z, r, g, b
     *
     * Accepts and tints blocks according to their colored light value 
     * CptSpaceToaster
     * 
     * 03-05-2014 heaton84 - Ported to helper method, refactored to match 1.7.2 architecture
     */
    public static boolean renderStandardBlockWithColorMultiplier(RenderBlocks instance, Block par1Block, int par2X, int par3Y, int par4Z, float par5R, float par6G, float par7B) {
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

        if (par1Block != Blocks.grass) {
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

        int l = CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z);

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y - 1, par4Z, 0)) {
            int i = instance.renderMinY > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y - 1, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f10, f13, f16);
            instance.renderFaceYNeg(par1Block, par2X, par3Y, par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 0));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y + 1, par4Z, 1)) {
            int i = instance.renderMaxY < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y + 1, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f7, f8, f9);
            instance.renderFaceYPos(par1Block, par2X, par3Y, par4Z, instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 1));
            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z - 1, 2)) {
            int i = instance.renderMinZ > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z - 1);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11, f14, f17);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 2);
            instance.renderFaceZNeg(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f11 * par5R, f14 * par6G, f17 * par7B);
                instance.renderFaceZNeg(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X, par3Y, par4Z + 1, 3)) {
            int i = instance.renderMaxZ < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X, par3Y, par4Z + 1);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f11, f14, f17);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 3);
            instance.renderFaceZPos(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f11 * par5R, f14 * par6G, f17 * par7B);
                instance.renderFaceZPos(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X - 1, par3Y, par4Z, 4)) {
            int i = instance.renderMinX > 0.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X - 1, par3Y, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12, f15, f18);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 4);
            instance.renderFaceXNeg(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f12 * par5R, f15 * par6G, f18 * par7B);
                instance.renderFaceXNeg(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (instance.renderAllFaces || par1Block.shouldSideBeRendered(instance.blockAccess, par2X + 1, par3Y, par4Z, 5)) {
            int i = instance.renderMaxX < 1.0D ? l : CLBlockHelper.getMixedBrightnessForBlockWithColor(instance.blockAccess, par2X + 1, par3Y, par4Z);
            tessellator.setBrightness(i);
            tessellator.setColorOpaque_F(f12, f15, f18);
            blockIcon = instance.getBlockIcon(par1Block, instance.blockAccess, par2X, par3Y, par4Z, 5);
            instance.renderFaceXPos(par1Block, par2X, par3Y, par4Z, blockIcon);

            if (RenderBlocks.fancyGrass && blockIcon.getIconName().equals("grass_side") && !instance.hasOverrideBlockTexture()) {
                tessellator.setColorOpaque_F(f12 * par5R, f15 * par6G, f18 * par7B);
                instance.renderFaceXPos(par1Block, par2X, par3Y, par4Z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }

    public static int getAoBrightness(RenderBlocks instance, int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
        return getAoBrightness(p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_);
    }

    /**
     * Get ambient occlusion brightness
     */
    public static int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
        // SSSS BBBB GGGG RRRR LLLL 0000
        // 1111 0000 0000 0000 1111 0000 = 15728880

        if (p_147778_1_ == 0) {
            p_147778_1_ = p_147778_4_;
        }

        if (p_147778_2_ == 0) {
            p_147778_2_ = p_147778_4_;
        }

        if (p_147778_3_ == 0) {
            p_147778_3_ = p_147778_4_;
        }

        //return (p_147778_1_ & 15728880) + (p_147778_2_ & 15728880) + (p_147778_3_ & 15728880) + (p_147778_4_ & 15728880) >> 2 & 15728880;

        // Must mix all 5 channels now
        return mixColorChannel(20, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // SSSS
                mixColorChannel(16, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // BBBB
                mixColorChannel(12, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // GGGG this is the problem child
                mixColorChannel(8, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_) | // RRRR
                mixColorChannel(4, p_147778_1_, p_147778_2_, p_147778_3_, p_147778_4_); // LLLL
    }

    public static int mixColorChannel(int startBit, int p1, int p2, int p3, int p4) {
        int avg;

        int q1 = (p1 >> startBit) & 0xf;
        int q2 = (p2 >> startBit) & 0xf;
        int q3 = (p3 >> startBit) & 0xf;
        int q4 = (p4 >> startBit) & 0xf;

        avg = (q1 + q2 + q3 + q4) / 4;

        if (avg > 15)
            avg = 15; // Cap to 4 bits again

        return avg << startBit;
    }
    /* 
     * Unused
     *  
    private static class renderCacheEntry
    {
    	public boolean isEmpty;
    	public float aoLightValue;
    	public int mixedBrightness;
    	public boolean canGrass;
    	
    	public renderCacheEntry fill(IBlockAccess world, int x, int y, int z)
    	{
    		Block block = world.getBlock(x,  y, z);
    		
    		aoLightValue = block.getAmbientOcclusionLightValue();
    		canGrass = block.getCanBlockGrass();
    		mixedBrightness = CLBlockHelper.getMixedBrightnessForBlockWithColor(world, x, y, z);

    		isEmpty = false;
    		
    		return this;
    	}
    	
    	public void discard()
    	{
    		isEmpty = true;
    	}
    }
    */
}
