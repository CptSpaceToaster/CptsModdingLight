package kovukore.coloredlightscore.src.asm.transformer.core;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLLog;

public abstract class SelectiveTransformer implements IClassTransformer
{
	@Override
	public final byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (bytes != null && transforms(transformedName))
		{
			try
			{
				ClassNode clazz = ASMUtils.getClassNode(bytes);
				if (transform(clazz, transformedName))
				{
					FMLLog.info("Transforming class " + transformedName);
					ClassWriter writer = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
					clazz.accept(writer);
					bytes = writer.toByteArray();
				}
			}
			catch (Exception e)
			{
				FMLLog.severe("Exception during transformation of class " + transformedName);
				e.printStackTrace();
				Throwables.propagate(e);
			}
		}
		return bytes;
	}

	protected abstract boolean transforms(String className);

	protected abstract boolean transform(ClassNode clazz, String className);
}