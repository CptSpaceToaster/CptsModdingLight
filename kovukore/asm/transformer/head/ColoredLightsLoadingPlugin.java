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
	public static void log(Level l, String msg)
	{
		FMLLog.log(l, "[ColoredLightsCore] " + msg);
	}
	
	public boolean isObfuscated()
	{
		try
		{
			Class.forName("net.minecraft.world.World");
		}
		catch (ClassNotFoundException e)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getASMTransformerClass()
	{
		if (isObfuscated())
		{
			//Run the transformer only in an obfuscated environment.
			return new String[] { ColoredLightsPatchTransformer.class.getName() };
		}
		else
		{
			return null;
		}
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
	}

	@Override
	public String getModContainerClass()
	{
		return ColoredLightsDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		if (isObfuscated())
		{
			//Run the preloader only in an obfuscated environment.
			return ColoredLightsPatchPreloader.class.getName();
		}
		else
		{
			return null;
		}
	}
}