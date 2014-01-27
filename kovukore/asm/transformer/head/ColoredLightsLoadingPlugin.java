package kovukore.asm.transformer.head;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.6.4")
public class ColoredLightsLoadingPlugin implements IFMLLoadingPlugin
{
	public static File location;
	public static boolean devEnvironment = false;

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { Transformer.class.getName() };
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		location = (File) data.get("coremodLocation");
		devEnvironment = !(Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getModContainerClass()
	{
		return ColoredLightsDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		return Scanner.class.getName();
	}

	@Override
	@Deprecated
	public	String[] getLibraryRequestClass() {
		// TODO Auto-generated method stub
		return null;
	}
}