package kovukore.asm.transformer.head;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class ColoredLightsPatchTransformer implements IClassTransformer
{
	private static Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

	public static void put(String className, byte[] bytes)
	{
		classBytes.put(className, bytes);
	}

	public static int size()
	{
		return classBytes.size();
	}

	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (classBytes.containsKey(name))
		{
			ColoredLightsLoadingPlugin.log(Level.INFO, "Patching " + name + " from binary patch file");
			return classBytes.get(name);
		}
		return bytes;
	}
}