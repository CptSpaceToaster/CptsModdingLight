package yamhaven.easycoloredlights.lib;

import java.io.File;

import net.minecraftforge.common.Configuration;
import yamhaven.easycoloredlights.lib.BlockIds;
import yamhaven.easycoloredlights.lib.BlockInfo;

public class ConfigHandler {
	public static void init(File configFile) {
		Configuration config = new Configuration(configFile);
		config.load();
		BlockIds.whiteLightBlockID_actual = config.getBlock(BlockInfo.whiteColoredLightBlock_name, BlockIds.whiteLightBlockID_default).getInt();
		BlockIds.blackLightBlockID_actual = config.getBlock(BlockInfo.blackColoredLightBlock_name, BlockIds.blackLightBlockID_default).getInt();
		BlockIds.redLightBlockID_actual = config.getBlock(BlockInfo.redColoredLightBlock_name, BlockIds.redLightBlockID_default).getInt();
		BlockIds.greenLightBlockID_actual = config.getBlock(BlockInfo.greenColoredLightBlock_name, BlockIds.greenLightBlockID_default).getInt();
		BlockIds.blueLightBlockID_actual = config.getBlock(BlockInfo.blueColoredLightBlock_name, BlockIds.blueLightBlockID_default).getInt();
		
		BlockIds.cyanLightBlockID_actual = config.getBlock(BlockInfo.cyanColoredLightBlock_name, BlockIds.cyanLightBlockID_default).getInt();
		BlockIds.yellowLightBlockID_actual = config.getBlock(BlockInfo.yellowColoredLightBlock_name, BlockIds.yellowLightBlockID_default).getInt();
		BlockIds.magentaLightBlockID_actual = config.getBlock(BlockInfo.magentaColoredLightBlock_name, BlockIds.magentaLightBlockID_default).getInt();
		config.save();
	}
}