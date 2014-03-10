package kovukore.coloredlights.src.asm;

import java.util.Map;

import kovukore.coloredlights.src.asm.transformer.*;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;

@MCVersion("1.7.2")
public class ColoredLightsCoreLoadingPlugin implements IFMLLoadingPlugin
{
	public static LaunchClassLoader CLASSLOADER;
	public static boolean MCP_ENVIRONMENT;
	
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {
				TransformBlock.class.getName(),
				TransformWorld.class.getName(),
				TransformRenderBlocks.class.getName(),
				TransformTessellator.class.getName(),
				TransformChunkCache.class.getName(),
				TransformExtendedBlockStorage.class.getName()
				};
	}

	@Override
	public String getModContainerClass()
	{
		return ColoredLightsCoreDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		MCP_ENVIRONMENT = !((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
		CLASSLOADER = (LaunchClassLoader)data.get("classLoader");
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
