package kovukore.coloredlights.src.asm.transformer.core;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

public abstract class MethodTransformer extends SelectiveTransformer
{
	@Override
	protected boolean transform(ClassNode clazz, String className)
	{
		// 03-06-2014 heaton84: Made so that it will transform more than one method
		boolean result = false;
		
		for (MethodNode method : clazz.methods)
		{
			if (transforms(clazz, method))
			{
				FMLLog.info("Transforming method " + method.name);
				result |= transform(clazz, method);
			}
		}
		return result;
	}

	protected abstract boolean transforms(ClassNode clazz, MethodNode method);

	protected abstract boolean transform(ClassNode clazz, MethodNode method);
}