package kovukore.asm.asmcore;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class CLFMLLoadingPlugin implements IFMLLoadingPlugin
{
	public static File location;
	
	@Override
	public String[] getASMTransformerClass() 
	{
		return new String[] { CLClassTransformer.class.getName() };
	}
	
	@Override
	public void injectData(Map<String, Object> data)
	{
		location = (File)data.get("coremodLocation");
	}

	@Override
	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String getModContainerClass() 
	{
		return CLDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass() 
	{
		return null;
	}
}