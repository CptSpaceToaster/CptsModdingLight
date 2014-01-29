package kovukore.coloredlights.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

public abstract class MethodTransformer extends SelectiveTransformer
{
	@Override
	protected boolean transform(ClassNode clazz, String className)
	{
		for (MethodNode method : clazz.methods)
		{
			if (transforms(clazz, method))
			{
				FMLLog.info("Transforming method " + method.name);
				return transform(clazz, method);
			}
		}
		return false;
	}

	protected abstract boolean transforms(ClassNode clazz, MethodNode method);

	protected abstract boolean transform(ClassNode clazz, MethodNode method);
}