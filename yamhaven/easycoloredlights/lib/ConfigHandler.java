package yamhaven.easycoloredlights.lib;

import java.io.File;

import net.minecraftforge.common.Configuration;
import yamhaven.easycoloredlights.lib.BlockIds;
import yamhaven.easycoloredlights.lib.BlockInfo;

public class ConfigHandler
{
	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		config.load();
		BlockIds.whiteLightBlockID_actual = config.getBlock(BlockInfo.whiteColoredLighBlock_name, BlockIds.whiteLightBlockID_default).getInt();
		BlockIds.blackLightBlockID_actual = config.getBlock(BlockInfo.blackColoredLighBlock_name, BlockIds.blackLightBlockID_default).getInt();
		BlockIds.redLightBlockID_actual = config.getBlock(BlockInfo.redColoredLighBlock_name, BlockIds.redLightBlockID_default).getInt();
		BlockIds.greenLightBlockID_actual = config.getBlock(BlockInfo.greenColoredLighBlock_name, BlockIds.greenLightBlockID_default).getInt();
		BlockIds.blueLightBlockID_actual = config.getBlock(BlockInfo.blueColoredLighBlock_name, BlockIds.blueLightBlockID_default).getInt();
		config.save();
	}
}