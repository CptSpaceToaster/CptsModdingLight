package coloredlightscore.src.asm;



import java.util.Map;

import net.minecraft.launchwrapper.LaunchClassLoader;
import coloredlightscore.src.asm.transformer.TransformBlock;
import coloredlightscore.src.asm.transformer.TransformChunkCache;
import coloredlightscore.src.asm.transformer.TransformEntityPlayerMP;
import coloredlightscore.src.asm.transformer.TransformEntityRenderer;
import coloredlightscore.src.asm.transformer.TransformExtendedBlockStorage;
import coloredlightscore.src.asm.transformer.TransformPlayerInstance;
import coloredlightscore.src.asm.transformer.TransformRenderBlocks;
import coloredlightscore.src.asm.transformer.TransformTessellator;
import coloredlightscore.src.asm.transformer.TransformWorld;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.7.2")
//@SortingIndex(value=999)
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
				TransformExtendedBlockStorage.class.getName(),
				TransformPlayerInstance.class.getName(),
				TransformEntityPlayerMP.class.getName(),
				TransformEntityRenderer.class.getName()
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
		return ColoredLightsCoreAccessTransformer.class.getName();
	}
}
