package kovukore.coloredlights.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.AddField_Block;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.AddField_ExtendedBlockStorage_A;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.AddField_ExtendedBlockStorage_B;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.AddField_ExtendedBlockStorage_C;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.LightsCoreAccessTransformer;
import com.ajwgeek.geekcore.common.asm.transformer.manualTransformations.impl.SetField_Block;

import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@SortingIndex(1337)
@MCVersion("1.6.4")
@TransformerExclusions({ "kovukore", "com.ajwgeek", "yamhaven" })
public class ColoredLightsLoadingPlugin implements IFMLLoadingPlugin
{
	public static File location;
	public static boolean devEnvironment;
	public static LaunchClassLoader classLoader;

	@Override
	public String[] getASMTransformerClass()
	{
		List<String> transformers = new ArrayList<String>();

		//Access
		transformers.add(LightsCoreAccessTransformer.class.getName());
		
		//Add Fields
		transformers.add(AddField_Block.class.getName());
		transformers.add(AddField_ExtendedBlockStorage_C.class.getName());
		transformers.add(AddField_ExtendedBlockStorage_B.class.getName());
		transformers.add(AddField_ExtendedBlockStorage_A.class.getName());
		
		///Prepend Methods
		transformers.add(SetField_Block.class.getName());
		
		Object[] ObjectList = transformers.toArray();
		String[] transFinal = Arrays.copyOf(ObjectList,ObjectList.length,String[].class);
		return transFinal;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		location = (File) data.get("coremodLocation");
		classLoader = (LaunchClassLoader) data.get("classLoader");
		devEnvironment = !(Boolean) data.get("runtimeDeobfuscationEnabled");
		FMLLog.log(Level.INFO, "ColoredLightsCore has started");
		FMLLog.log(Level.INFO, "ColoredLightsCore Location: %s", new Object[] { location });
		FMLLog.log(Level.INFO, "ColoredLightsCore DEVL_ENV: %s", new Object[] { devEnvironment });
	}

	@Override
	public String getModContainerClass()
	{
		return ColoredLightsDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}
}