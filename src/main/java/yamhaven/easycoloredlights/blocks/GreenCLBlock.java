package yamhaven.easycoloredlights.blocks;

import java.util.Random;

public class GreenCLBlock extends CLBlock {
	public GreenCLBlock(int id, int switchID, boolean isPowered) {
		super(id, switchID, isPowered);
		func_149663_c("greenCLBlock");	//setUnlocalizedName(BlockInfo.greenColoredLightBlock_unlocalizedName);
	}

//	@SideOnly(Side.CLIENT)
//	public void registerIcons(IconRegister icon) {
//		blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":" + BlockInfo.greenColoredLightBlock_unlocalizedName + (powered?"On":""));
//	}
	
	@Override
	protected void turnLightsOn(Random r) {
		addColorLightValue(0.0F, 1.0F, 0.0F);
	}
}
